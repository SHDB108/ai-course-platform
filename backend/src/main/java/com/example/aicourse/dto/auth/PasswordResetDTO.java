package com.example.aicourse.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * API 2.8 执行密码重置请求
 */
@Data
public class PasswordResetDTO {
    @NotBlank(message = "标识符不能为空")
    private String identifier;
    @NotBlank(message = "验证码不能为空")
    private String verificationCode; // 或 "resetToken"
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}