package com.example.aicourse.dto.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * API 4.3 新增课程请求
 */
@Data
public class CourseCreateDTO {
    @NotBlank(message = "课程名称不能为空")
    private String name;
    private String description;
    @NotNull(message = "学分不能为空")
    private Integer credits;
    @NotNull(message = "课时不能为空")
    private Integer duration;
    @NotBlank(message = "开始日期不能为空")
    private String startDate;
    @NotBlank(message = "结束日期不能为空")
    private String endDate;
    private Long teacherId;
    @NotNull(message = "最大学生数不能为空")
    private Integer maxStudents;
    @NotBlank(message = "状态不能为空")
    private String status;
    private String department;
    private String semester;
    private Long categoryId; // 课程分类ID
}