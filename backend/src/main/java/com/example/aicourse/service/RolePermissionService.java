package com.example.aicourse.service;

import com.example.aicourse.dto.admin.RoleStatusUpdateDTO;
import com.example.aicourse.vo.admin.PermissionVO;
import com.example.aicourse.vo.admin.RoleVO;

import java.util.List;

/**
 * 角色权限管理服务接口
 */
public interface RolePermissionService {
    
    /**
     * 获取所有角色列表
     */
    List<RoleVO> getAllRoles();
    
    /**
     * 获取所有权限列表
     */
    List<PermissionVO> getAllPermissions();
    
    /**
     * 更新角色状态
     */
    void updateRoleStatus(Long roleId, RoleStatusUpdateDTO dto);
    
    /**
     * 删除角色
     */
    void deleteRole(Long roleId);
}