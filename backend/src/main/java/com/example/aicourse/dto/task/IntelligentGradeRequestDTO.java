package com.example.aicourse.dto.task;

import lombok.Data;

/**
 * API 6.6 智能批改请求
 */
@Data
public class IntelligentGradeRequestDTO {
    private String gradingRules;
    private String llmModel;
}