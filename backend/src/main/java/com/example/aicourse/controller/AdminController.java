package com.example.aicourse.controller;

import com.example.aicourse.dto.admin.UserCreateByAdminDTO;
import com.example.aicourse.dto.admin.UserStatusUpdateDTO;
import com.example.aicourse.service.AdminService;
import com.example.aicourse.utils.Result;
import com.example.aicourse.vo.PageVO;
import com.example.aicourse.vo.user.UserVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理员相关的API控制器
 */
@RestController
@RequestMapping("/api/v1/admin") // 匹配文档的 /admin 路径部分
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * API 2.9 管理员：获取所有用户列表
     */
    @GetMapping("/users") // 完整路径为 /api/v1/admin/users
    public Result<PageVO<UserVO>> getUsersByAdmin(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String keyword) {
        try {
            PageVO<UserVO> usersPage = adminService.getUsersByAdmin(page, size, role, keyword);
            return Result.ok(usersPage);
        } catch (Exception e) {
            // 生产环境中应记录日志，并返回更友好的错误信息
            return Result.error("获取用户列表失败：" + e.getMessage());
        }
    }

    /**
     * API 2.10 管理员：禁用/启用用户
     */
    @PutMapping("/users/{id}/status") // 完整路径为 /api/v1/admin/users/{id}/status
    public Result<Void> updateUserStatus(
            @PathVariable Long id,
            @Valid @RequestBody UserStatusUpdateDTO request) {
        try {
            adminService.updateUserStatus(id, request);
            return Result.ok(); // 成功响应data为null
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * API 2.11 管理员：创建新用户 (包括管理员、教师、学生)
     * 权限要求：只有拥有 ADMIN 角色的用户才能调用此接口。
     * TODO: 在实际项目中，需要通过 Spring Security 等框架实现基于角色的访问控制（RBAC）。
     */
    @PostMapping("/users") // 完整路径为 /api/v1/admin/users
    public Result<Long> createUserByAdmin(@Valid @RequestBody UserCreateByAdminDTO request) {
        try {
            Long newUserId = adminService.createUserByAdmin(request);
            return Result.ok(newUserId); // 返回新创建的用户ID
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}