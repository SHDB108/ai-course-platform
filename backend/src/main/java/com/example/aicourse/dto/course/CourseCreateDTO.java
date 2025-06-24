package com.example.aicourse.dto.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

/**
 * API 4.3 创建新课程请求
 */
@Data
public class CourseCreateDTO {
    @NotBlank
    private String courseCode;
    @NotBlank
    private String courseName;
    @NotNull
    private BigDecimal credit;
    @NotNull
    private Integer hours;
    @NotNull
    private Long teacherId;
    private String description;
    private String prerequisites;
}