package com.example.aicourse.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * API 2.2 学生用户注册请求
 */
@Data
public class StudentRegisterDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank(message = "密码不能为空")
    private String password;
    @NotBlank(message = "学号不能为空")
    private String stuNo;
    @NotBlank(message = "姓名不能为空")
    private String name;
    // 性别字段不强制要求NotBlank，因为0或1是有效值
    private Integer gender; // 0=女, 1=男
    private String major;
    private String grade;
    private String phone;
    @Email(message = "邮箱格式不正确")
    private String email;
}