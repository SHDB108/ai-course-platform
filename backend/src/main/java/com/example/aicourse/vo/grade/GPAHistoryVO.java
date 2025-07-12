package com.example.aicourse.vo.grade;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GPAHistoryVO {
    private String semester;
    private Double gpa;
    private Integer credits;
    private Integer courses;
    private LocalDateTime createdAt;
}