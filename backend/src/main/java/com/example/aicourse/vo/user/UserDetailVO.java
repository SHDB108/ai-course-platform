package com.example.aicourse.vo.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * API 2.5 获取当前用户信息响应
 * 使用JsonInclude.Include.NON_NULL来动态隐藏不适用的字段
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDetailVO {
    // Common fields
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String role;

    // Student specific fields
    private String stuNo;
    private String name;
    private Integer gender;
    private String major;
    private String grade;

    // Teacher specific fields
    private String teacherNo;
    private String department;
    private String title;
}