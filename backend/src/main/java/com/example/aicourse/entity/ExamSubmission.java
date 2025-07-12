package com.example.aicourse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("exam_submission")
public class ExamSubmission {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long examId;
    private Long studentId;
    private LocalDateTime submissionTime;
    private Integer score;
    private Integer totalScore;
    private Double percentage;
    private String status; // SUBMITTED, GRADED, PENDING
    private Integer attemptNumber;
    private Integer timeSpent; // 用时（秒）
    private String feedback;
    private LocalDateTime createdAt;
}