package com.xiaomu.labservice.module.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 发送验证码 DTO
 */
@Data
@Schema(description = "发送验证码请求")
public class SendCodeDTO {

    @Schema(description = "邮箱", example = "user@example.com")
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @Schema(description = "类型: register-注册, reset-重置密码", example = "register")
    @NotBlank(message = "类型不能为空")
    private String type;
}

