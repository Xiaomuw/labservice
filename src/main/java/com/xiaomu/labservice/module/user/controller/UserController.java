package com.xiaomu.labservice.module.user.controller;

import com.xiaomu.labservice.common.response.Result;
import com.xiaomu.labservice.module.user.dto.*;
import com.xiaomu.labservice.module.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户控制器
 */
@Tag(name = "用户管理", description = "用户信息相关接口")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class UserController {

    private final UserService userService;

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/me")
    public Result<UserVO> getCurrentUser() {
        UserVO user = userService.getCurrentUser();
        return Result.success(user);
    }

    @Operation(summary = "更新当前用户信息")
    @PutMapping("/me")
    public Result<UserVO> updateCurrentUser(@Valid @RequestBody UserUpdateDTO updateDTO) {
        UserVO user = userService.updateCurrentUser(updateDTO);
        return Result.success("更新成功", user);
    }

    @Operation(summary = "修改密码")
    @PutMapping("/me/password")
    public Result<Void> changePassword(@Valid @RequestBody PasswordChangeDTO passwordChangeDTO) {
        userService.changePassword(passwordChangeDTO);
        return Result.success();
    }

    @Operation(summary = "上传头像")
    @PostMapping("/me/avatar")
    public Result<UserVO> uploadAvatar(@RequestParam("file") MultipartFile file) {
        // TODO: 调用文件服务上传头像
        String avatarUrl = "avatars/" + file.getOriginalFilename();
        UserVO user = userService.uploadAvatar(avatarUrl);
        return Result.success("头像上传成功", user);
    }

    @Operation(summary = "注销账户")
    @DeleteMapping("/me")
    public Result<Void> deleteCurrentUser() {
        userService.deleteCurrentUser();
        return Result.success();
    }
}
