package com.example.aicourse.vo.grade;

import lombok.Data;

import java.util.Map;

@Data
public class GradeStatisticsVO {
    private Integer totalCourses;
    private Integer completedCourses;
    private Integer inProgressCourses;
    private Integer failedCourses;
    private Double overallGPA;
    private Double semesterGPA;
    private Integer totalCredits;
    private Integer earnedCredits;
    private Double averageScore;
    private Double highestScore;
    private Double lowestScore;
    private Map<String, Integer> gradesDistribution; // A, B, C, D, F的数量分布
}