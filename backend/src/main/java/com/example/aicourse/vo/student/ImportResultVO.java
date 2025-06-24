package com.example.aicourse.vo.student;

import java.util.List;
import lombok.Data;

/**
 * API 3.6 批量导入结果响应
 */
@Data
public class ImportResultVO {
    private int successCount;
    private int failureCount;
    private List<FailedRecord> failedRecords;

    @Data
    public static class FailedRecord {
        private int row;
        private String reason;
    }
}