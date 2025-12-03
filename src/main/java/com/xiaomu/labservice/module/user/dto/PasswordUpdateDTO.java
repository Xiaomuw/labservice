package com.xiaomu.labservice.module.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 密码修改 DTO
 */
@Data
@Schema(description = "密码修改请求")
public class PasswordUpdateDTO {

    @Schema(description = "原密码", example = "oldpassword")
    @NotBlank(message = "原密码不能为空")
    private String oldPassword;

    @Schema(description = "新密码", example = "newpassword123")
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 100, message = "密码长度必须在6-100之间")
    private String newPassword;
}

