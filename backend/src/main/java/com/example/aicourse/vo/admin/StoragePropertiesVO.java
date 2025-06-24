package com.example.aicourse.vo.admin;

import lombok.Data;

/**
 * API 13.1 存储配置响应
 */
@Data
public class StoragePropertiesVO {
    private String type;
    private String localPath;
    private MinioVO minio;

    @Data
    public static class MinioVO {
        private String endpoint;
        private String bucket;
        private String accessKey;
        private String secretKey;
    }
}