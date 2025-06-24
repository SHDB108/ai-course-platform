package com.example.aicourse.vo.student;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * API 3.1 & 3.2 学生信息响应
 */
@Data
public class StudentVO {
    private Long id;
    private String stuNo;
    private String name;
    private Integer gender;
    private String major;
    private String grade;
    private String phone;
    private String email;
    private LocalDateTime gmtCreate;
}