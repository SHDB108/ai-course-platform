package com.example.aicourse.vo.analytics;

import java.math.BigDecimal;
import lombok.Data;

/**
 * API 10.4 任务完成统计响应
 */
@Data
public class TaskCompletionSummaryVO {
    private Long taskId;
    private String taskTitle;
    private int totalStudents;
    private int submittedCount;
    private int onTimeCount;
    private BigDecimal averageScore;
    private double completionRate;
    private double onTimeRate;
}