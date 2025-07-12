package com.example.aicourse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.aicourse.dto.admin.RoleStatusUpdateDTO;
import com.example.aicourse.entity.Permission;
import com.example.aicourse.entity.Role;
import com.example.aicourse.entity.RolePermission;
import com.example.aicourse.repository.PermissionMapper;
import com.example.aicourse.repository.RoleMapper;
import com.example.aicourse.repository.RolePermissionMapper;
import com.example.aicourse.service.RolePermissionService;
import com.example.aicourse.vo.admin.PermissionVO;
import com.example.aicourse.vo.admin.RoleVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色权限管理服务实现类
 */
@Service
@Transactional
public class RolePermissionServiceImpl implements RolePermissionService {

    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;
    private final RolePermissionMapper rolePermissionMapper;

    public RolePermissionServiceImpl(RoleMapper roleMapper, 
                                    PermissionMapper permissionMapper,
                                    RolePermissionMapper rolePermissionMapper) {
        this.roleMapper = roleMapper;
        this.permissionMapper = permissionMapper;
        this.rolePermissionMapper = rolePermissionMapper;
    }

    @Override
    public List<RoleVO> getAllRoles() {
        List<Role> roles = roleMapper.selectList(null);
        
        return roles.stream().map(role -> {
            RoleVO vo = new RoleVO();
            BeanUtils.copyProperties(role, vo);
            
            // 获取角色对应的权限
            QueryWrapper<RolePermission> wrapper = new QueryWrapper<>();
            wrapper.eq("role_id", role.getId());
            List<RolePermission> rolePermissions = rolePermissionMapper.selectList(wrapper);
            
            List<Long> permissionIds = rolePermissions.stream()
                    .map(RolePermission::getPermissionId)
                    .collect(Collectors.toList());
            vo.setPermissionIds(permissionIds);
            
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<PermissionVO> getAllPermissions() {
        List<Permission> permissions = permissionMapper.selectList(null);
        
        return permissions.stream().map(permission -> {
            PermissionVO vo = new PermissionVO();
            BeanUtils.copyProperties(permission, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public void updateRoleStatus(Long roleId, RoleStatusUpdateDTO dto) {
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }
        
        role.setStatus(dto.getStatus());
        roleMapper.updateById(role);
    }

    @Override
    public void deleteRole(Long roleId) {
        // 检查角色是否存在
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }
        
        // 删除角色权限关联
        QueryWrapper<RolePermission> wrapper = new QueryWrapper<>();
        wrapper.eq("role_id", roleId);
        rolePermissionMapper.delete(wrapper);
        
        // 删除角色
        roleMapper.deleteById(roleId);
    }
}