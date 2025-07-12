package com.example.aicourse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("semester_gpa")
public class SemesterGPA {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long studentId;
    private String semester;
    private Double gpa;
    private Integer credits;
    private Integer courses;
    private LocalDateTime createdAt;
}