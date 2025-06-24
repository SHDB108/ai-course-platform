package com.example.aicourse.vo.quiz;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * API 9.2 学生测验提交记录响应
 */
@Data
public class QuizSubmissionVO {
    private Long id;
    private Long paperId;
    private Long studentId;
    private LocalDateTime startAt;
    private LocalDateTime submitAt;
    private BigDecimal score;
    private String status;
}