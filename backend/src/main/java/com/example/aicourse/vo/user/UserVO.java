package com.example.aicourse.vo.user;

import lombok.Data;

/**
 * API 2.9 管理员获取用户列表响应
 */
@Data
public class UserVO {
    private Long id;
    private String username;
    private String role;
    private String name;
    private String email;
    private Integer status;
}