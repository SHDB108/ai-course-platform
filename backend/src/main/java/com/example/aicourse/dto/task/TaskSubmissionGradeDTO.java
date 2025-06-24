package com.example.aicourse.dto.task;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

/**
 * API 6.5 教师批改任务请求
 */
@Data
public class TaskSubmissionGradeDTO {
    @NotNull
    private BigDecimal score;
    private String feedback;
    @NotNull
    private Long graderId;
}