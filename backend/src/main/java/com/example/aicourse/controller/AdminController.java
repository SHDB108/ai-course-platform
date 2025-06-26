package com.example.aicourse.controller;

import com.example.aicourse.dto.admin.StoragePropertiesUpdateDTO;
import com.example.aicourse.dto.admin.UserCreateByAdminDTO;
import com.example.aicourse.dto.admin.UserStatusUpdateDTO;
import com.example.aicourse.service.AdminService;
import com.example.aicourse.utils.Result;
import com.example.aicourse.vo.PageVO;
import com.example.aicourse.vo.admin.StoragePropertiesVO;
import com.example.aicourse.vo.admin.SystemHealthVO;
import com.example.aicourse.vo.user.UserVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@Validated
// @PreAuthorize("hasRole('ADMIN')") // 启用Spring Security后，取消此行注释以保护整个控制器
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

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

    /**
     * API 13.1 获取存储配置
     */
    @GetMapping("/config/storage")
    public Result<StoragePropertiesVO> getStorageConfig() {
        return Result.ok(adminService.getStorageProperties());
    }

    /**
     * API 13.2 更新存储配置
     */
    @PutMapping("/config/storage")
    public Result<Void> updateStorageConfig(@RequestBody StoragePropertiesUpdateDTO dto) {
        adminService.updateStorageProperties(dto);
        return Result.ok();
    }

    /**
     * API 13.3 获取系统健康状态
     */
    @GetMapping("/health")
    public Result<SystemHealthVO> getHealth() {
        return Result.ok(adminService.getSystemHealth());
    }
}