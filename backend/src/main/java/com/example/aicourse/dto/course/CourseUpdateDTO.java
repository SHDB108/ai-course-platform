package com.example.aicourse.dto.course;

import lombok.Data;

/**
 * API 4.4 更新课程信息请求
 */
@Data
public class CourseUpdateDTO {
    private String courseCode; // 新增此字段以匹配 ServiceImpl 中的逻辑
    private String name; // 课程名称，映射到 courseName
    private String description;
    private Integer credits;
    private String semester;
    private String department;
    private Integer duration; // 课程时长，映射到 hours
    private Integer maxStudents; // 最大学生数，映射到 capacity
    private Long teacherId;
    private String classroom;
    private String scheduleTime;
    private String status; // 课程状态
    private Long categoryId; // 课程分类ID
}