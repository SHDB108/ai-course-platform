package com.example.aicourse.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 成绩趋势单点数据，用于折线图
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrendPointVO {
    /** 日期字符串，例如 "2025-06-19" */
    private String date;
    /** 当日/当次成绩 */
    private Double score;
}