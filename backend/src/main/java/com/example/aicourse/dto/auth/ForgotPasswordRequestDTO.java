package com.example.aicourse.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * API 2.7 请求密码重置请求
 */
@Data
public class ForgotPasswordRequestDTO {
    @NotBlank(message = "标识符不能为空（用户名、邮箱或手机号）")
    private String identifier; // 可以是用户名、邮箱或手机号
}