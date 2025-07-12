package com.example.aicourse.vo.grade;

import lombok.Data;

@Data
public class GradeTrendVO {
    private String month;
    private Double averageScore;
    private Double gpa;
    private Integer courseCount;
}