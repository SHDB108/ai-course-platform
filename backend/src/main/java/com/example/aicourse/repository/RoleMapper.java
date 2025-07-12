package com.example.aicourse.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.aicourse.entity.Role;
import com.example.aicourse.vo.admin.RoleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色Mapper接口
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {
    
    /**
     * 获取所有角色及其用户数量
     */
    @Select("SELECT r.*, " +
            "(SELECT COUNT(*) FROM users u WHERE u.role = r.name) as user_count " +
            "FROM roles r ORDER BY r.id")
    List<RoleVO> selectRolesWithUserCount();
    
    /**
     * 根据角色ID获取权限列表
     */
    @Select("SELECT p.name FROM permissions p " +
            "INNER JOIN role_permissions rp ON p.id = rp.permission_id " +
            "WHERE rp.role_id = #{roleId}")
    List<String> selectPermissionsByRoleId(Long roleId);
}