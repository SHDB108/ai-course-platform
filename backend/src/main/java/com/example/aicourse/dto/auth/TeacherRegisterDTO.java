package com.example.aicourse.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * API 2.3 教师用户注册请求
 */
@Data
public class TeacherRegisterDTO {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    private String teacherNo;
    @NotBlank
    private String name;
    private String department;
    private String title;
    private String phone;
    @Email
    private String email;
}