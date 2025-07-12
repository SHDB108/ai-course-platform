package com.example.aicourse.dto.course;

import lombok.Data;

/**
 * 课程分类更新DTO
 */
@Data
public class CourseCategoryUpdateDTO {
    
    /**
     * 分类名称
     */
    private String name;
    
    /**
     * 分类描述
     */
    private String description;
}