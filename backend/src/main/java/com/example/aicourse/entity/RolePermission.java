package com.example.aicourse.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 角色权限关联实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("role_permissions")
public class RolePermission {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 角色ID
     */
    @TableField("role_id")
    private Long roleId;
    
    /**
     * 权限ID
     */
    @TableField("permission_id")
    private Long permissionId;
    
    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}