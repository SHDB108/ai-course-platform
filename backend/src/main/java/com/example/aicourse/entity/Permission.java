package com.example.aicourse.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 权限实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("permissions")
public class Permission {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 权限名称（唯一标识）
     */
    @TableField("name")
    private String name;
    
    /**
     * 权限显示名称
     */
    @TableField("display_name")
    private String displayName;
    
    /**
     * 权限描述
     */
    @TableField("description")
    private String description;
    
    /**
     * 资源标识
     */
    @TableField("resource")
    private String resource;
    
    /**
     * 操作类型
     */
    @TableField("action")
    private String action;
    
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
}