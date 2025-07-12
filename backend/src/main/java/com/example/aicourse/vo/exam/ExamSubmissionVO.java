package com.example.aicourse.vo.exam;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ExamSubmissionVO {
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
    private List<ExamAnswerVO> answers;
    private String feedback;
    private LocalDateTime createdAt;
}