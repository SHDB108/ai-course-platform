package com.example.aicourse.vo.exam;

import lombok.Data;

@Data
public class ExamStatisticsVO {
    private Integer totalExams;
    private Integer completedExams;
    private Integer pendingExams;
    private Integer expiredExams;
    private Double averageScore;
    private Integer totalScore;
    private Integer passedExams;
    private Integer failedExams;
}