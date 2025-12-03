package com.xiaomu.labservice.module.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 管理员更新用户 DTO
 */
@Data
@Schema(description = "管理员更新用户请求")
public class AdminUserUpdateDTO {

    @Schema(description = "邮箱", example = "newemail@example.com")
    @Email(message = "邮箱格式不正确")
    private String email;

    @Schema(description = "昵称", example = "新昵称")
    @Size(max = 50, message = "昵称长度不能超过50")
    private String nickname;

    @Schema(description = "手机号", example = "13800138000")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Schema(description = "角色: ROLE_USER, ROLE_LAB_ADMIN, ROLE_ADMIN", example = "ROLE_USER")
    @Pattern(regexp = "^ROLE_(USER|LAB_ADMIN|ADMIN)$", message = "角色格式不正确")
    private String role;

    @Schema(description = "状态: 0-禁用, 1-正常", example = "1")
    private Integer status;

    @Schema(description = "密码(可选，不修改留空)")
    @Size(min = 6, max = 100, message = "密码长度必须在6-100之间")
    private String password;
}

