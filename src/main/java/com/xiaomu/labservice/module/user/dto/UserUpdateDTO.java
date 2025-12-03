package com.xiaomu.labservice.module.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

/**
 * 用户更新 DTO
 */
@Data
public class UserUpdateDTO {

    private String nickname;
    private String phone;

    @Email(message = "邮箱格式不正确")
    private String email;
}
