package com.example.aicourse.vo.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API 2.1 用户登录响应
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseVO {
    private String token;
    private Long userId;
    private String username;
    private String role;
}