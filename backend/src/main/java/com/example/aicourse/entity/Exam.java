package com.example.aicourse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("exam")
public class Exam {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String examTitle;
    private Long courseId;
    private String examType; // QUIZ, MIDTERM, FINAL, ASSIGNMENT
    private String status; // NOT_STARTED, IN_PROGRESS, COMPLETED, EXPIRED
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer duration; // 考试时长（分钟）
    private Integer totalScore; // 总分
    private Integer passingScore; // 及格分
    private String description;
    private String instructions;
    private Boolean allowRetake; // 是否允许重考
    private Integer maxAttempts; // 最大尝试次数
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}