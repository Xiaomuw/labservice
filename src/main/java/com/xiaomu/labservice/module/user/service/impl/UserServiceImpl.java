package com.xiaomu.labservice.module.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaomu.labservice.common.constant.RedisKeyConstant;
import com.xiaomu.labservice.common.constant.StatusConstant;
import com.xiaomu.labservice.common.exception.BusinessException;
import com.xiaomu.labservice.common.response.ResultCode;
import com.xiaomu.labservice.common.util.JwtUtil;
import com.xiaomu.labservice.common.util.RedisUtil;
import com.xiaomu.labservice.module.user.dto.*;
import com.xiaomu.labservice.module.user.entity.User;
import com.xiaomu.labservice.module.user.mapper.UserMapper;
import com.xiaomu.labservice.module.user.service.UserService;
import com.xiaomu.labservice.mq.producer.EmailProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final RedisUtil redisUtil;
    private final EmailProducer emailProducer;

    @Override
    @Transactional
    public UserVO register(RegisterDTO registerDTO) {
        // 检查用户名是否已存在
        if (userMapper.selectByUsername(registerDTO.getUsername()) != null) {
            throw new BusinessException(ResultCode.USERNAME_EXISTS);
        }

        // 检查邮箱是否已注册
        if (userMapper.selectByEmail(registerDTO.getEmail()) != null) {
            throw new BusinessException(ResultCode.EMAIL_EXISTS);
        }

        // 验证验证码
        String cachedCode = redisUtil.getString(RedisKeyConstant.VERIFY_CODE + registerDTO.getEmail());
        if (!StringUtils.hasText(cachedCode) || !cachedCode.equals(registerDTO.getVerifyCode())) {
            throw new BusinessException(ResultCode.VERIFY_CODE_ERROR);
        }

        // 创建用户
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setEmail(registerDTO.getEmail());
        user.setPhone(registerDTO.getPhone());
        user.setNickname(registerDTO.getNickname() != null ? registerDTO.getNickname() : registerDTO.getUsername());
        user.setRole("ROLE_USER");
        user.setStatus(StatusConstant.UserStatus.ENABLED);

        userMapper.insert(user);

        // 删除验证码
        redisUtil.delete(RedisKeyConstant.VERIFY_CODE + registerDTO.getEmail());

        return convertToVO(user);
    }

    @Override
    public AuthResponseDTO login(LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getUsername(),
                        loginDTO.getPassword()));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = findByUsername(userDetails.getUsername());

        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        // 将 refresh token 存入 Redis
        redisUtil.set(RedisKeyConstant.USER + user.getId() + ":refresh", refreshToken,
                Duration.ofDays(7).toMillis(), java.util.concurrent.TimeUnit.MILLISECONDS);

        AuthResponseDTO response = new AuthResponseDTO();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setUser(convertToVO(user));

        return response;
    }

    @Override
    public AuthResponseDTO refreshToken(String refreshToken) {
        if (jwtUtil.isTokenExpired(refreshToken)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "Refresh Token 已过期");
        }

        String username = jwtUtil.extractUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        User user = findByUsername(username);

        String newAccessToken = jwtUtil.generateAccessToken(userDetails);
        String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);

        // 更新 refresh token
        redisUtil.set(RedisKeyConstant.USER + user.getId() + ":refresh", newRefreshToken,
                Duration.ofDays(7).toMillis(), java.util.concurrent.TimeUnit.MILLISECONDS);

        AuthResponseDTO response = new AuthResponseDTO();
        response.setAccessToken(newAccessToken);
        response.setRefreshToken(newRefreshToken);
        response.setUser(convertToVO(user));

        return response;
    }

    @Override
    public void logout(String token) {
        // 将 token 加入黑名单
        if (StringUtils.hasText(token)) {
            String username = jwtUtil.extractUsername(token);
            User user = findByUsername(username);
            if (user != null) {
                redisUtil.set(RedisKeyConstant.TOKEN_BLACKLIST + token, "1",
                        jwtUtil.getExpirationDateFromToken(token) != null
                                ? jwtUtil.getExpirationDateFromToken(token).getTime() - System.currentTimeMillis()
                                : 7200000L,
                        java.util.concurrent.TimeUnit.MILLISECONDS);
                redisUtil.delete(RedisKeyConstant.USER + user.getId() + ":refresh");
            }
        }
    }

    @Override
    public void sendVerifyCode(String email) {
        // 生成6位验证码
        String code = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));

        // 存入 Redis，5分钟过期
        redisUtil.setString(RedisKeyConstant.VERIFY_CODE + email, code,
                Duration.ofMinutes(5).toMillis(), java.util.concurrent.TimeUnit.MILLISECONDS);

        // 通过 Kafka 发送邮件
        emailProducer.sendVerificationCode(email, code);
        log.info("验证码已生成并发送: {} -> {}", email, code);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordDTO resetPasswordDTO) {
        User user = userMapper.selectByEmail(resetPasswordDTO.getEmail());
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 验证验证码
        String cachedCode = redisUtil.getString(RedisKeyConstant.VERIFY_CODE + resetPasswordDTO.getEmail());
        if (!StringUtils.hasText(cachedCode) || !cachedCode.equals(resetPasswordDTO.getVerifyCode())) {
            throw new BusinessException(ResultCode.VERIFY_CODE_ERROR);
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(resetPasswordDTO.getNewPassword()));
        userMapper.updateById(user);

        // 删除验证码
        redisUtil.delete(RedisKeyConstant.VERIFY_CODE + resetPasswordDTO.getEmail());
    }

    @Override
    public UserVO getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = findByUsername(username);
        return convertToVO(user);
    }

    @Override
    @Transactional
    public UserVO updateCurrentUser(UserUpdateDTO updateDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = findByUsername(username);

        if (updateDTO.getEmail() != null && !updateDTO.getEmail().equals(user.getEmail())) {
            // 检查新邮箱是否已被使用
            User existingUser = userMapper.selectByEmail(updateDTO.getEmail());
            if (existingUser != null && !existingUser.getId().equals(user.getId())) {
                throw new BusinessException(ResultCode.EMAIL_EXISTS);
            }
            user.setEmail(updateDTO.getEmail());
        }

        if (updateDTO.getNickname() != null) {
            user.setNickname(updateDTO.getNickname());
        }
        if (updateDTO.getPhone() != null) {
            user.setPhone(updateDTO.getPhone());
        }

        userMapper.updateById(user);
        return convertToVO(user);
    }

    @Override
    @Transactional
    public void changePassword(PasswordChangeDTO passwordChangeDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = findByUsername(username);

        if (!passwordEncoder.matches(passwordChangeDTO.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }

        user.setPassword(passwordEncoder.encode(passwordChangeDTO.getNewPassword()));
        userMapper.updateById(user);
    }

    @Override
    @Transactional
    public UserVO uploadAvatar(String avatarUrl) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = findByUsername(username);
        user.setAvatar(avatarUrl);
        userMapper.updateById(user);
        return convertToVO(user);
    }

    @Override
    @Transactional
    public void deleteCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = findByUsername(username);
        userMapper.deleteById(user.getId());
    }

    @Override
    public Page<UserVO> getUserList(Long page, Long size, String keyword) {
        Page<User> userPage = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(User::getUsername, keyword)
                    .or().like(User::getEmail, keyword)
                    .or().like(User::getNickname, keyword));
        }

        wrapper.orderByDesc(User::getCreatedAt);
        Page<User> result = userMapper.selectPage(userPage, wrapper);

        Page<UserVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        List<UserVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    public UserVO getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return convertToVO(user);
    }

    @Override
    @Transactional
    public UserVO createUser(UserVO userVO) {
        if (userMapper.selectByUsername(userVO.getUsername()) != null) {
            throw new BusinessException(ResultCode.USERNAME_EXISTS);
        }
        if (userMapper.selectByEmail(userVO.getEmail()) != null) {
            throw new BusinessException(ResultCode.EMAIL_EXISTS);
        }

        User user = new User();
        BeanUtils.copyProperties(userVO, user);
        user.setPassword(passwordEncoder.encode("123456")); // 默认密码
        user.setRole(userVO.getRole() != null ? userVO.getRole() : "ROLE_USER");
        user.setStatus(userVO.getStatus() != null ? userVO.getStatus() : StatusConstant.UserStatus.ENABLED);

        userMapper.insert(user);
        return convertToVO(user);
    }

    @Override
    @Transactional
    public UserVO updateUser(Long id, UserVO userVO) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        if (userVO.getEmail() != null && !userVO.getEmail().equals(user.getEmail())) {
            User existingUser = userMapper.selectByEmail(userVO.getEmail());
            if (existingUser != null && !existingUser.getId().equals(id)) {
                throw new BusinessException(ResultCode.EMAIL_EXISTS);
            }
        }

        if (userVO.getNickname() != null) {
            user.setNickname(userVO.getNickname());
        }
        if (userVO.getEmail() != null) {
            user.setEmail(userVO.getEmail());
        }
        if (userVO.getPhone() != null) {
            user.setPhone(userVO.getPhone());
        }
        if (userVO.getRole() != null) {
            user.setRole(userVO.getRole());
        }

        userMapper.updateById(user);
        return convertToVO(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        userMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void updateUserStatus(Long id, Integer status) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        user.setStatus(status);
        userMapper.updateById(user);
    }

    @Override
    public User findByUsername(String username) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return user;
    }

    /**
     * 转换为 VO
     */
    private UserVO convertToVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
}
