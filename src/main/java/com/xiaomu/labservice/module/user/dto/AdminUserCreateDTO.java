package com.xiaomu.labservice.module.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 管理员创建用户 DTO
 */
@Data
@Schema(description = "管理员创建用户请求")
public class AdminUserCreateDTO {

    @Schema(description = "用户名", example = "newuser")
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;

    @Schema(description = "密码", example = "password123")
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 100, message = "密码长度必须在6-100之间")
    private String password;

    @Schema(description = "邮箱", example = "user@example.com")
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @Schema(description = "昵称", example = "新用户")
    private String nickname;

    @Schema(description = "手机号", example = "13800138000")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Schema(description = "角色: ROLE_USER, ROLE_LAB_ADMIN, ROLE_ADMIN", example = "ROLE_USER")
    @NotBlank(message = "角色不能为空")
    @Pattern(regexp = "^ROLE_(USER|LAB_ADMIN|ADMIN)$", message = "角色格式不正确")
    private String role;

    @Schema(description = "状态: 0-禁用, 1-正常", example = "1")
    private Integer status = 1;
}

