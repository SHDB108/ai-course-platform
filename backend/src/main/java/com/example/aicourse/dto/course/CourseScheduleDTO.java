package com.example.aicourse.dto.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * API 4.6 课程排课请求
 */
@Data
public class CourseScheduleDTO {
    @NotNull(message = "授课教师ID不能为空")
    private Long teacherId;
    @NotBlank(message = "上课地点不能为空")
    private String classroom;
    @NotBlank(message = "上课时间不能为空")
    private String scheduleTime; // 例如: "周二 1-2节, 周四 3-4节"
}