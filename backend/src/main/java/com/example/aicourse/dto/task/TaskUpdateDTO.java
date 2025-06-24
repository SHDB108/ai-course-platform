package com.example.aicourse.dto.task;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * API 5.4 更新任务信息请求
 */
@Data
public class TaskUpdateDTO {
    private String title;
    private String type;
    private String description;
    private LocalDateTime deadline;
    private String submitType;
    private BigDecimal maxScore;
}