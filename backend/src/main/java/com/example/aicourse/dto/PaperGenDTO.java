package com.example.aicourse.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 组卷请求参数
 */
@Data
public class PaperGenDTO {
    /** 课程 ID */
    private Long courseId;
    /** 试卷标题 */
    private String title;
    /** 希望抽题数量 */
    private Integer count;
    /** 总分 */
    private BigDecimal totalScore;
    /** 知识点过滤（可选） */
    private List<String> knowledge;
    /** 难度过滤（可选） */
    private Integer difficulty;
}