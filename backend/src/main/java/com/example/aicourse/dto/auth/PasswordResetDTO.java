package com.example.aicourse.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * API 2.8 执行密码重置请求
 */
@Data
public class PasswordResetDTO {
    @NotBlank
    private String identifier;
    @NotBlank
    private String verificationCode;
    @NotBlank
    private String newPassword;
}