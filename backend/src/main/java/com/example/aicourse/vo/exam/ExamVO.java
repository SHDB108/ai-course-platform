package com.example.aicourse.vo.exam;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExamVO {
    private Long id;
    private String examTitle;
    private Long courseId;
    private String courseName;
    private String teacherName;
    private String examType; // QUIZ, MIDTERM, FINAL, ASSIGNMENT
    private String status; // NOT_STARTED, IN_PROGRESS, COMPLETED, EXPIRED
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer duration; // 考试时长（分钟）
    private Integer totalScore; // 总分
    private Integer passingScore; // 及格分
    private String description;
    private String instructions;
    private Boolean allowRetake; // 是否允许重考
    private Integer maxAttempts; // 最大尝试次数
    private Integer currentAttempts; // 当前尝试次数
    private Integer lastAttemptScore; // 最后一次考试分数
    private Integer bestScore; // 最好成绩
    private Integer remainingTime; // 剩余时间（秒）
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}