package com.example.aicourse.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 课程分类实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("course_categories")
public class CourseCategory {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 分类名称
     */
    @TableField("name")
    private String name;
    
    /**
     * 分类描述
     */
    @TableField("description")
    private String description;
    
    /**
     * 状态：ACTIVE-启用，INACTIVE-禁用
     */
    @TableField("status")
    private String status;
    
    /**
     * 排序顺序
     */
    @TableField("sort_order")
    private Integer sortOrder;
    
    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    /**
     * 该分类下的课程数量（非数据库字段）
     */
    @TableField(exist = false)
    private Integer courseCount;
}