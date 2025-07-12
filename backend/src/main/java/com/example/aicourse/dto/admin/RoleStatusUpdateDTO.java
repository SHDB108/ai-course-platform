package com.example.aicourse.dto.admin;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

/**
 * 角色状态更新DTO
 */
@Data
public class RoleStatusUpdateDTO {
    
    /**
     * 角色状态：1-启用，0-禁用
     */
    private Integer status;
}