package com.example.aicourse.dto.paper;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

/**
 * API 8.8 智能组卷请求 (更新)
 */
@Data
public class PaperGenDTO {
    private Long courseId;
    private String title;
    private Integer count;
    private BigDecimal totalScore;
    private List<String> knowledge;
    private Integer difficulty;

    // New fields from API doc
    private String strategy; // RANDOM, KNOWLEDGE_BASED, DIFFICULTY_BALANCED
    private Integer durationMinutes;
}