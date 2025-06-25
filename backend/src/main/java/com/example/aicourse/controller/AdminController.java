package com.example.aicourse.controller;

import com.example.aicourse.dto.admin.StoragePropertiesUpdateDTO;
import com.example.aicourse.service.AdminService;
import com.example.aicourse.utils.Result;
import com.example.aicourse.vo.admin.StoragePropertiesVO;
import com.example.aicourse.vo.admin.SystemHealthVO;
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