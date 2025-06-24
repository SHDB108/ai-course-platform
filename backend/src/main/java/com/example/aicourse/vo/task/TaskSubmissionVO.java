package com.example.aicourse.vo.task;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * API 6.2, 6.3, 6.4 任务提交信息响应
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // 动态包含字段
public class TaskSubmissionVO {
    private Long id;
    private Long taskId;
    private Long studentId;
    private String status;
    private BigDecimal score;
    private LocalDateTime submittedAt;
    private String answerPath;
    private String feedback;
    private Long graderId;

    // 拓展字段
    private String taskTitle;
    private String studentName;
    private String stuNo;
}