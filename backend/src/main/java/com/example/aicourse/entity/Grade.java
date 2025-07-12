package com.example.aicourse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("grade")
public class Grade {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long studentId;
    private Long courseId;
    private Long examId; // 考试ID，可为空
    private Long taskId; // 任务ID，可为空
    private String gradeType; // EXAM, ASSIGNMENT, QUIZ, PROJECT, PARTICIPATION
    private Integer score;
    private Integer totalScore;
    private Double percentage;
    private String letterGrade; // A, B, C, D, F
    private Double gradePoint; // GPA点数
    private Double weight; // 权重
    private LocalDateTime submissionDate;
    private LocalDateTime gradedDate;
    private String feedback;
    private Boolean isLate;
    private Integer latePenalty;
    private String status; // GRADED, PENDING, RESUBMIT_REQUIRED
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}