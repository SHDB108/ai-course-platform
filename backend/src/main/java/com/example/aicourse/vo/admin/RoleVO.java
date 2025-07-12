package com.example.aicourse.vo.admin;

import lombok.Data;

import java.util.List;

/**
 * 角色VO
 */
@Data
public class RoleVO {
    
    private Long id;
    
    /**
     * 角色名称（唯一标识）
     */
    private String name;
    
    /**
     * 角色显示名称
     */
    private String displayName;
    
    /**
     * 角色描述
     */
    private String description;
    
    /**
     * 角色状态：1-启用，0-禁用
     */
    private Integer status;
    
    /**
     * 用户数量
     */
    private Integer userCount;
    
    /**
     * 角色对应的权限列表
     */
    private List<String> permissions;
    
    /**
     * 角色对应的权限ID列表
     */
    private List<Long> permissionIds;
}