package com.example.aicourse.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("roles")
public class Role {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 角色名称（唯一标识）
     */
    @TableField("name")
    private String name;
    
    /**
     * 角色显示名称
     */
    @TableField("display_name")
    private String displayName;
    
    /**
     * 角色描述
     */
    @TableField("description")
    private String description;
    
    /**
     * 角色状态：1-启用，0-禁用
     */
    @TableField("status")
    private Integer status;
    
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
     * 角色对应的权限列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<String> permissions;
    
    /**
     * 用户数量（非数据库字段）
     */
    @TableField(exist = false)
    private Integer userCount;
}