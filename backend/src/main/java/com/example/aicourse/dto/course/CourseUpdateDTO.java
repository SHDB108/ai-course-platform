package com.example.aicourse.dto.course;

import lombok.Data;

/**
 * API 4.4 更新课程信息请求
 */
@Data
public class CourseUpdateDTO {
    private String courseCode; // 新增此字段以匹配 ServiceImpl 中的逻辑
    private String courseName;
    private String description;
    private Integer credits;
    private String semester;
    private String department;
    private Integer capacity;
    private Long teacherId;
    private String classroom;
    private String scheduleTime;
}