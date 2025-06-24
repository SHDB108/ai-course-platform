package com.example.aicourse.vo.quiz;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.Data;

/**
 * API 9.3 测验提交详情响应
 */
@Data
public class QuizSubmissionDetailVO {
    private Long id;
    private Long paperId;
    private Long studentId;
    private String paperTitle;
    private String studentName;
    private LocalDateTime startAt;
    private LocalDateTime submitAt;
    private BigDecimal score;
    private Map<Long, String> answers;
    private LocalDateTime gmtCreate;
}