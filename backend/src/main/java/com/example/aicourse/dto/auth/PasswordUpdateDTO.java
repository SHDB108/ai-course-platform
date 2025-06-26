package com.example.aicourse.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * API 2.6 修改用户密码请求
 */
@Data
public class PasswordUpdateDTO {
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}