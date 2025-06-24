package com.example.aicourse.vo.paper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * API 8.9 测验试卷列表响应
 */
@Data
public class QuizPaperVO {
    private Long id;
    private Long courseId;
    private String title;
    private BigDecimal totalScore;
    private Integer durationMinutes;
    private LocalDateTime gmtCreate;
}