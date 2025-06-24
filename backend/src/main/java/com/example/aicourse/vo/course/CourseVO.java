package com.example.aicourse.vo.course;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * API 4.1 & 4.2 课程信息响应
 */
@Data
public class CourseVO {
    private Long id;
    private String courseCode;
    private String courseName;
    private BigDecimal credit;
    private Integer hours;
    private Long teacherId;
    private String teacherName; // 拓展字段
    private String description;
    private String prerequisites;
    private LocalDateTime gmtCreate;
}