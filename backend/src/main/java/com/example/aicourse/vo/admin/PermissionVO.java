package com.example.aicourse.vo.admin;

import lombok.Data;

/**
 * 权限VO
 */
@Data
public class PermissionVO {
    
    private Long id;
    
    /**
     * 权限名称（唯一标识）
     */
    private String name;
    
    /**
     * 权限显示名称
     */
    private String displayName;
    
    /**
     * 权限分类
     */
    private String category;
    
    /**
     * 权限描述
     */
    private String description;
}