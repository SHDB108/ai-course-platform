package com.example.aicourse.vo.task;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

/**
 * API 6.6 智能批改结果响应
 */
@Data
public class IntelligentGradeResultVO {
    private BigDecimal score;
    private String feedback;
    private List<String> suggestions;
}