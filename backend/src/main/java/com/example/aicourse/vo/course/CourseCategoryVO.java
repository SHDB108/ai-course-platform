package com.example.aicourse.vo.course;

import lombok.Data;

/**
 * 课程分类VO
 */
@Data
public class CourseCategoryVO {
    
    private Long id;
    
    /**
     * 分类名称
     */
    private String name;
    
    /**
     * 分类描述
     */
    private String description;
    
    /**
     * 状态：ACTIVE-启用，INACTIVE-禁用
     */
    private String status;
    
    /**
     * 该分类下的课程数量
     */
    private Integer courseCount;
}