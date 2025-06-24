package com.example.aicourse.dto.quiz;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Map;
import lombok.Data;

/**
 * API 9.4 批改测验提交请求
 */
@Data
public class QuizGradeDTO {
    @NotNull
    private BigDecimal score;
    private Map<Long, BigDecimal> manualScores; // Key: questionId, Value: score
    private String feedback;
    @NotNull
    private Long graderId;
}