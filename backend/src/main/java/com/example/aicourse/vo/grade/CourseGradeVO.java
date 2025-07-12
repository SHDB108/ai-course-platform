package com.example.aicourse.vo.grade;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CourseGradeVO {
    private Long courseId;
    private String courseName;
    private String courseCode;
    private String teacherName;
    private Integer credits;
    private String semester;
    private Double finalGrade;
    private Double finalPercentage;
    private String finalLetterGrade;
    private Double finalGradePoint;
    private String status; // IN_PROGRESS, COMPLETED, FAILED
    private List<GradeVO> grades;
    private GradeBreakdownVO gradeBreakdown;
    private Integer rank; // 班级排名
    private Integer classSize; // 班级人数
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}