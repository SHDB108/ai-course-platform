package com.example.aicourse.dto.course;

import java.math.BigDecimal;
import lombok.Data;

/**
 * API 4.4 更新课程信息请求
 */
@Data
public class CourseUpdateDTO {
    private String courseCode;
    private String courseName;
    private BigDecimal credit;
    private Integer hours;
    private Long teacherId;
    private String description;
    private String prerequisites;
}