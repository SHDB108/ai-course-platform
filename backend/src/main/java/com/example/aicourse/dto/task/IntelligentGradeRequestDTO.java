package com.example.aicourse.dto.task;

import lombok.Data;
import lombok.Getter;

/**
 * API 6.6 智能批改请求
 */
@Data
@Getter
public class IntelligentGradeRequestDTO {
    private String gradingRules;
    private String llmModel;
}