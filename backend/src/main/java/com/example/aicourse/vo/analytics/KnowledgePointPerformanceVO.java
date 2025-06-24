package com.example.aicourse.vo.analytics;

import java.math.BigDecimal;
import lombok.Data;

/**
 * API 10.6 知识点表现数据响应
 */
@Data
public class KnowledgePointPerformanceVO {
    private Long knowledgePointId;
    private String knowledgePointName;
    private BigDecimal averageScore;
    private String masteryLevel;
}