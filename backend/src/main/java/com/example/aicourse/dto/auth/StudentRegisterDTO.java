package com.example.aicourse.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * API 2.2 学生用户注册请求
 */
@Data
public class StudentRegisterDTO {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    private String stuNo;
    @NotBlank
    private String name;
    private Integer gender;
    private String major;
    private String grade;
    private String phone;
    @Email
    private String email;
}