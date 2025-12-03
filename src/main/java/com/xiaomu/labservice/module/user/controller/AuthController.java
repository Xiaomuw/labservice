package com.xiaomu.labservice.module.user.controller;

import com.xiaomu.labservice.common.response.Result;
import com.xiaomu.labservice.module.user.dto.*;
import com.xiaomu.labservice.module.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@Tag(name = "认证管理", description = "用户认证相关接口")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<UserVO> register(@Valid @RequestBody RegisterDTO registerDTO) {
        UserVO user = userService.register(registerDTO);
        return Result.success("注册成功", user);
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<AuthResponseDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        AuthResponseDTO response = userService.login(loginDTO);
        return Result.success("登录成功", response);
    }

    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        String token = extractToken(request);
        userService.logout(token);
        return Result.success();
    }

    @Operation(summary = "刷新 Token")
    @PostMapping("/refresh-token")
    public Result<AuthResponseDTO> refreshToken(@RequestParam String refreshToken) {
        AuthResponseDTO response = userService.refreshToken(refreshToken);
        return Result.success(response);
    }

    @Operation(summary = "发送验证码")
    @PostMapping("/send-code")
    public Result<Void> sendVerifyCode(@RequestParam String email) {
        userService.sendVerifyCode(email);
        return Result.success();
    }

    @Operation(summary = "重置密码")
    @PostMapping("/reset-password")
    public Result<Void> resetPassword(@Valid @RequestBody ResetPasswordDTO resetPasswordDTO) {
        userService.resetPassword(resetPasswordDTO);
        return Result.success();
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
