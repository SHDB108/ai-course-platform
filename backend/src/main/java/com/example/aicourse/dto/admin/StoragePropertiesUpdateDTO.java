package com.example.aicourse.dto.admin;

import lombok.Data;

/**
 * API 13.2 更新存储配置请求
 */
@Data
public class StoragePropertiesUpdateDTO {
    private String type;
    private MinioDTO minio;

    @Data
    public static class MinioDTO {
        private String endpoint;
        private String bucket;
        private String accessKey;
        private String secretKey;
    }
}