package com.example.aicourse.dto.capability;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * API 12.3 更新能力评分请求
 */
@Data
public class CapabilityScoreUpdateTriggerDTO {
    @NotBlank
    private String triggeredBy; // "TASK_SUBMISSION", "QUIZ_GRADED", etc.
    @NotNull
    private Long relatedEntityId;
}