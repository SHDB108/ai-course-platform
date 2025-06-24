package com.example.aicourse.vo.task;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * API 5.1 & 5.2 任务信息响应
 */
@Data
public class TaskVO {
    private Long id;
    private Long courseId;
    private String title;
    private String type;
    private String description;
    private LocalDateTime deadline;
    private String submitType;
    private Long creatorId;
    private BigDecimal maxScore;
    private LocalDateTime gmtCreate;
}