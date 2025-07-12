package com.example.aicourse.dto.course;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

/**
 * 课程分类创建DTO
 */
@Data
public class CourseCategoryCreateDTO {
    
    /**
     * 分类名称
     */
    @NotBlank(message = "分类名称不能为空")
    private String name;
    
    /**
     * 分类描述
     */
    private String description;
}