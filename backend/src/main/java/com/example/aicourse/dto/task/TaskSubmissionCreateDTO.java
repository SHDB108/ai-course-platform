package com.example.aicourse.dto.task;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * API 6.1 学生提交任务请求
 * 注意：文件上传将通过 Controller 中的 @RequestPart 处理，此 DTO 主要用于在线/链接提交。
 */
@Data
public class TaskSubmissionCreateDTO {
    @NotNull
    private Long taskId;
    @NotNull
    private Long studentId;
    private String answerContent; // For ONLINE or LINK submissions
}