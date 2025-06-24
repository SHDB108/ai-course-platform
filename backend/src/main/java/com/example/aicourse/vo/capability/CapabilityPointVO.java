package com.example.aicourse.vo.capability;

import lombok.Data;

/**
 * API 12.1 能力点列表响应
 */
@Data
public class CapabilityPointVO {
    private Long id;
    private String name;
    private Long parentId;
}