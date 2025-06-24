package com.example.aicourse.dto.resource;

import lombok.Data;

/**
 * API 7.5 更新资源信息请求
 */
@Data
public class ResourceUpdateDTO {
    private String filename;
    private String description;
}