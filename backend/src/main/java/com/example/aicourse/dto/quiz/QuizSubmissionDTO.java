package com.example.aicourse.dto.quiz;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.Data;

/**
 * API 9.1 学生提交测验请求
 */
@Data
public class QuizSubmissionDTO {
    @NotNull
    private Long paperId;
    @NotNull
    private Long studentId;
    @NotNull
    private LocalDateTime startAt;
    @NotNull
    private LocalDateTime submitAt;
    private Map<Long, String> answers; // Key: questionId, Value: student's answer
}