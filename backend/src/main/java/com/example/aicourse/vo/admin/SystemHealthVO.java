package com.example.aicourse.vo.admin;

import java.util.Map;
import lombok.Data;

/**
 * API 13.3 系统健康状态响应
 */
@Data
public class SystemHealthVO {
    private String status;
    private Map<String, ComponentHealth> components;

    @Data
    public static class ComponentHealth {
        private String status;
        private Map<String, Object> details;
    }
}