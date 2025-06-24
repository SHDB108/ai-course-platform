package com.example.aicourse.dto.task;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * API 5.3 创建新任务请求
 */
@Data
public class TaskCreateDTO {
    @NotNull
    private Long courseId;
    @NotBlank
    private String title;
    @NotBlank
    private String type;
    private String description;
    @Future
    private LocalDateTime deadline;
    private String submitType;
    @NotNull
    private Long creatorId;
    private BigDecimal maxScore;
}