package com.example.aicourse.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * API 2.3 教师用户注册请求
 */
@Data
public class TeacherRegisterDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank(message = "密码不能为空")
    private String password;
    @NotBlank(message = "教师工号不能为空")
    private String teacherNo;
    @NotBlank(message = "姓名不能为空")
    private String name;
    private String department;
    private String title;
    private String phone;
    @Email(message = "邮箱格式不正确")
    private String email;
}