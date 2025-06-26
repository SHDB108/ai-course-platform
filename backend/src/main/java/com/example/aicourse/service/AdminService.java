package com.example.aicourse.service;

import com.example.aicourse.dto.admin.UserCreateByAdminDTO;
import com.example.aicourse.dto.admin.UserStatusUpdateDTO;
import com.example.aicourse.vo.PageVO;
import com.example.aicourse.vo.user.UserVO;

public interface AdminService {
    /**
     * 管理员获取所有用户列表
     * @param page 当前页码
     * @param size 每页数量
     * @param role 按角色过滤 (可选)
     * @param keyword 搜索关键词 (可选)
     * @return 分页的用户列表
     */
    PageVO<UserVO> getUsersByAdmin(Long page, Long size, String role, String keyword);

    /**
     * 管理员修改用户状态
     * @param userId 要修改状态的用户ID
     * @param dto 包含新状态的DTO
     * @return 修改成功返回true，否则抛出异常
     */
    boolean updateUserStatus(Long userId, UserStatusUpdateDTO dto);

    /**
     * 管理员创建新用户
     * @param dto 用户创建请求DTO
     * @return 新创建用户的ID
     */
    Long createUserByAdmin(UserCreateByAdminDTO dto);
}