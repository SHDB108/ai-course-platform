package com.example.aicourse.vo.grade;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GradeVO {
    private Long id;
    private Long studentId;
    private Long courseId;
    private String courseName;
    private String teacherName;
    private Long examId;
    private String examTitle;
    private Long taskId;
    private String taskTitle;
    private String gradeType; // EXAM, ASSIGNMENT, QUIZ, PROJECT, PARTICIPATION
    private Integer score;
    private Integer totalScore;
    private Double percentage;
    private String letterGrade; // A, B, C, D, F
    private Double gradePoint; // GPA点数
    private Double weight; // 权重
    private LocalDateTime submissionDate;
    private LocalDateTime gradedDate;
    private String feedback;
    private Boolean isLate;
    private Integer latePenalty;
    private String status; // GRADED, PENDING, RESUBMIT_REQUIRED
    private Integer passingScore; // 及格分
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}