package com.example.aicourse.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * API 2.6 修改用户密码请求
 */
@Data
public class PasswordUpdateDTO {
    @NotBlank
    private String oldPassword;
    @NotBlank
    private String newPassword;
}