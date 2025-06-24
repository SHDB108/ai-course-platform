package com.example.aicourse.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * API 2.7 请求密码重置请求
 */
@Data
public class ForgotPasswordRequestDTO {
    @NotBlank
    private String identifier; // Can be username, email, or phone
}