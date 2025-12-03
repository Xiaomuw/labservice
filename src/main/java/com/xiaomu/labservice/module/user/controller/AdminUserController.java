package com.xiaomu.labservice.module.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaomu.labservice.common.response.PageResult;
import com.xiaomu.labservice.common.response.Result;
import com.xiaomu.labservice.module.user.dto.UserVO;
import com.xiaomu.labservice.module.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员用户控制器
 */
@Tag(name = "管理员-用户管理", description = "管理员用户管理接口")
@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    @Operation(summary = "查询用户列表")
    @GetMapping
    public Result<PageResult<UserVO>> getUserList(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String keyword) {
        Page<UserVO> userPage = userService.getUserList(page, size, keyword);
        return Result.success(PageResult.of(userPage));
    }

    @Operation(summary = "查询用户详情")
    @GetMapping("/{id}")
    public Result<UserVO> getUserById(@PathVariable Long id) {
        UserVO user = userService.getUserById(id);
        return Result.success(user);
    }

    @Operation(summary = "新增用户")
    @PostMapping
    public Result<UserVO> createUser(@RequestBody UserVO userVO) {
        UserVO user = userService.createUser(userVO);
        return Result.success("用户创建成功", user);
    }

    @Operation(summary = "编辑用户")
    @PutMapping("/{id}")
    public Result<UserVO> updateUser(@PathVariable Long id, @RequestBody UserVO userVO) {
        UserVO user = userService.updateUser(id, userVO);
        return Result.success("用户更新成功", user);
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }

    @Operation(summary = "禁用/启用用户")
    @PutMapping("/{id}/status")
    public Result<Void> updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.updateUserStatus(id, status);
        return Result.success();
    }
}
