package com.example.aicourse.dto.admin;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * API 2.11 管理员创建新用户请求
 */
@Data
public class UserCreateByAdminDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank(message = "密码不能为空")
    private String password;
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    @NotBlank(message = "角色不能为空")
    private String role; // "STUDENT", "TEACHER", "ADMIN"

    // Student fields
    private String stuNo;
    private String name; // 学生和教师都有姓名
    private Integer gender;
    private String major;
    private String grade;

    // Teacher fields
    private String teacherNo;
    private String department;
    private String title;
}