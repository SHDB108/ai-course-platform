package com.example.aicourse.dto.admin;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * API 2.10 管理员修改用户状态请求
 */
@Data
public class UserStatusUpdateDTO {
    @NotNull
    private Integer status; // 0=禁用, 1=启用
}