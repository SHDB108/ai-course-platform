package com.example.aicourse.dto.student;

import lombok.Data;

/**
 * API 3.4 更新学生信息请求
 */
@Data
public class StudentUpdateDTO {
    private String name;
    private Integer gender;
    private String major;
    private String grade;
    private String phone;
    private String email;
}