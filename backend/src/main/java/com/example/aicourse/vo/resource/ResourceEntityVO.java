package com.example.aicourse.vo.resource;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * API 7.3 & 7.4 资源信息响应
 */
@Data
public class ResourceEntityVO {
    private Long id;
    private String filename;
    private String path;
    private String type;
    private Long size;
    private Long uploaderId;
    private Long courseId;
    private String description;
    private LocalDateTime gmtCreate;
}