package com.example.aicourse.vo.paper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

/**
 * API 8.10 测验试卷详情响应
 */
@Data
public class QuizPaperDetailsVO {
    private Long id;
    private Long courseId;
    private String title;
    private BigDecimal totalScore;
    private Integer durationMinutes;
    private LocalDateTime gmtCreate;
    private List<QuestionInPaperVO> questions;

    @Data
    public static class QuestionInPaperVO {
        private Long questionId;
        private String stem;
        private String type;
        private BigDecimal score;
        private List<OptionInPaperVO> options;
    }

    @Data
    public static class OptionInPaperVO {
        private Long id;
        private String content;
        // 注意：为避免泄露答案，不返回 isCorrect
    }
}