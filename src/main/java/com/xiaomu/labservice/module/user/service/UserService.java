package com.xiaomu.labservice.module.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaomu.labservice.module.user.dto.*;
import com.xiaomu.labservice.module.user.entity.User;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户注册
     */
    UserVO register(RegisterDTO registerDTO);

    /**
     * 用户登录
     */
    AuthResponseDTO login(LoginDTO loginDTO);

    /**
     * 刷新 Token
     */
    AuthResponseDTO refreshToken(String refreshToken);

    /**
     * 用户登出
     */
    void logout(String token);

    /**
     * 发送验证码
     */
    void sendVerifyCode(String email);

    /**
     * 重置密码
     */
    void resetPassword(ResetPasswordDTO resetPasswordDTO);

    /**
     * 获取当前用户信息
     */
    UserVO getCurrentUser();

    /**
     * 更新当前用户信息
     */
    UserVO updateCurrentUser(UserUpdateDTO updateDTO);

    /**
     * 修改密码
     */
    void changePassword(PasswordChangeDTO passwordChangeDTO);

    /**
     * 上传头像
     */
    UserVO uploadAvatar(String avatarUrl);

    /**
     * 注销账户
     */
    void deleteCurrentUser();

    /**
     * 管理员：查询用户列表
     */
    Page<UserVO> getUserList(Long page, Long size, String keyword);

    /**
     * 管理员：查询用户详情
     */
    UserVO getUserById(Long id);

    /**
     * 管理员：新增用户
     */
    UserVO createUser(UserVO userVO);

    /**
     * 管理员：更新用户
     */
    UserVO updateUser(Long id, UserVO userVO);

    /**
     * 管理员：删除用户
     */
    void deleteUser(Long id);

    /**
     * 管理员：禁用/启用用户
     */
    void updateUserStatus(Long id, Integer status);

    /**
     * 根据用户名查询用户
     */
    User findByUsername(String username);
}
