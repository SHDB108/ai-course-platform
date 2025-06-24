package com.example.aicourse.dto.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * API 2.11 管理员创建新用户请求
 */
@Data
public class UserCreateByAdminDTO {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    private String email;
    @NotBlank
    private String role; // "STUDENT", "TEACHER", "ADMIN"

    // Student fields
    private String stuNo;
    private String name;
    private Integer gender;
    private String major;
    private String grade;

    // Teacher fields
    private String teacherNo;
    private String department;
    private String title;
}