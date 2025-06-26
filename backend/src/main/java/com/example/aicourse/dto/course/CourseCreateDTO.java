package com.example.aicourse.dto.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * API 4.3 新增课程请求
 */
@Data
public class CourseCreateDTO {
    @NotBlank(message = "课程代码不能为空")
    private String courseCode;
    @NotBlank(message = "课程名称不能为空")
    private String courseName;
    private String description;
    @NotNull(message = "学分不能为空")
    private Integer credits;
    @NotBlank(message = "开课学期不能为空")
    private String semester;
    @NotBlank(message = "开课学院不能为空")
    private String department;
    @NotNull(message = "课程容量不能为空")
    private Integer capacity;
    // 教师ID, 教室, 上课时间可以在创建时指定，也可以在排课时指定
    private Long teacherId;
    private String classroom;
    private String scheduleTime;
}