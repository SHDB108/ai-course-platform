package com.example.aicourse.service.impl;

import com.example.aicourse.config.StorageProperties;
import com.example.aicourse.dto.admin.StoragePropertiesUpdateDTO;
import com.example.aicourse.service.AdminService;
import com.example.aicourse.vo.admin.StoragePropertiesVO;
import com.example.aicourse.vo.admin.SystemHealthVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {
    private final StorageProperties storageProperties;
    private final HealthEndpoint healthEndpoint;

    @Autowired
    public AdminServiceImpl(StorageProperties storageProperties, HealthEndpoint healthEndpoint) {
        this.storageProperties = storageProperties;
        this.healthEndpoint = healthEndpoint;
    }
    @Override
    public StoragePropertiesVO getStorageProperties() {
        StoragePropertiesVO vo = new StoragePropertiesVO();
        BeanUtils.copyProperties(storageProperties, vo);
        if (storageProperties.getMinio() != null) {
            StoragePropertiesVO.MinioVO minioVO = new StoragePropertiesVO.MinioVO();
            BeanUtils.copyProperties(storageProperties.getMinio(), minioVO);
            vo.setMinio(minioVO);
        }
        return vo;
    }

    @Override
    public void updateStorageProperties(StoragePropertiesUpdateDTO dto) {
        // 警告：在生产环境中，动态修改配置是复杂且危险的操作。
        // 简单的实现只是修改内存中的Bean属性，但这不会持久化，且可能不会被所有组件重新加载。
        // 生产级实现需要借助 Spring Cloud Config + Spring Cloud Bus，或通过重启应用来生效。
        System.out.println("收到更新存储配置的请求（仅演示，未实际生效）: " + dto);
        // storageProperties.setType(dto.getType());
        // ...
    }

    @Override
    public SystemHealthVO getSystemHealth() {
        if (healthEndpoint == null) {
            SystemHealthVO vo = new SystemHealthVO();
            vo.setStatus("UNKNOWN");
            vo.setComponents(Map.of("actuator", new SystemHealthVO.ComponentHealth()));
            return vo;
        }

        HealthComponent healthComponent = healthEndpoint.health();
        Health health = (Health) healthComponent;
        SystemHealthVO vo = new SystemHealthVO();
        vo.setStatus(health.getStatus().getCode());

        Map<String, SystemHealthVO.ComponentHealth> componentHealthMap = health.getDetails().entrySet().stream()
                .filter(entry -> entry.getValue() instanceof HealthComponent)
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                    HealthComponent component = (HealthComponent) entry.getValue();
                    SystemHealthVO.ComponentHealth ch = new SystemHealthVO.ComponentHealth();
                    ch.setStatus(component.getStatus().getCode());

                    // HealthComponent 接口没有 getDetails()，所以这里也需要转换
                    if (component instanceof Health) {
                        ch.setDetails(((Health) component).getDetails());
                    } else {
                        ch.setDetails(Collections.emptyMap());
                    }
                    return ch;
                }));
        vo.setComponents(componentHealthMap);

        return vo;
    }
}