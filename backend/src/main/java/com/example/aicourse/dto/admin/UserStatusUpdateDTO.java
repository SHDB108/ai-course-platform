package com.example.aicourse.dto.admin;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * API 2.10 管理员修改用户状态请求
 */
@Data
public class UserStatusUpdateDTO {
    @NotNull(message = "用户状态不能为空")
    private String status; // ACTIVE=启用, INACTIVE=禁用, SUSPENDED=暂停, DELETED=删除
}