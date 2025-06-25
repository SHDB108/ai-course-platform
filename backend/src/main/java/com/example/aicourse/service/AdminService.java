package com.example.aicourse.service;

import com.example.aicourse.dto.admin.StoragePropertiesUpdateDTO;
import com.example.aicourse.vo.admin.StoragePropertiesVO;
import com.example.aicourse.vo.admin.SystemHealthVO;

public interface AdminService {

    /**
     * 获取存储配置 (API 13.1)
     */
    StoragePropertiesVO getStorageProperties();

    /**
     * 更新存储配置 (API 13.2)
     */
    void updateStorageProperties(StoragePropertiesUpdateDTO dto);

    /**
     * 获取系统健康状态 (API 13.3)
     */
    SystemHealthVO getSystemHealth();
}