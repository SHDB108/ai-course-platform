package com.example.aicourse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.aicourse.dto.resource.ResourceUpdateDTO;
import com.example.aicourse.entity.ResourceEntity;
import com.example.aicourse.entity.ResourceType;
import com.example.aicourse.repository.ResourceMapper;
import com.example.aicourse.service.ResourceService;
import com.example.aicourse.service.StorageService;
import com.example.aicourse.vo.resource.ResourceEntityVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ResourceServiceImpl implements ResourceService {

    private final StorageService storageService;

    private final ResourceMapper resourceMapper;

    @Autowired
    public ResourceServiceImpl(StorageService storageService, ResourceMapper resourceMapper) {
        this.storageService = storageService;
        this.resourceMapper = resourceMapper;
    }

    @Override
    @Transactional
    public Long uploadResource(MultipartFile file, Long courseId, String description, Long uploaderId) throws IOException {
        // 1. 上传文件到存储服务，获取存储路径
        String path = storageService.upload(file);

        // 2. 创建一个新的 ResourceEntity 对象实例
        ResourceEntity entity = new ResourceEntity();

        // 3. 逐个设置对象的属性
        entity.setFilename(file.getOriginalFilename());
        entity.setPath(path);
        entity.setSize(file.getSize());
        entity.setType(detectType(file));
        entity.setUploaderId(uploaderId);
        entity.setCourseId(courseId);
        entity.setDescription(description);

        // 4. 将完整的实体对象插入数据库
        resourceMapper.insert(entity);
        return entity.getId();
    }


    @Override
    public Page<ResourceEntityVO> listResourcesByCourse(Page<ResourceEntityVO> page, Long courseId, String type, String keyword) {
        LambdaQueryWrapper<ResourceEntity> query = Wrappers.<ResourceEntity>lambdaQuery()
                .eq(ResourceEntity::getCourseId, courseId)
                .like(StringUtils.hasText(keyword), ResourceEntity::getFilename, keyword)
                .eq(StringUtils.hasText(type), ResourceEntity::getType, type);

        Page<ResourceEntity> entityPage = resourceMapper.selectPage(new Page<>(page.getCurrent(), page.getSize()), query);

        Page<ResourceEntityVO> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        voPage.setRecords(entityPage.getRecords().stream().map(entity -> {
            ResourceEntityVO vo = new ResourceEntityVO();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        }).toList());

        return voPage;
    }

    @Override
    public ResourceEntityVO getResourceVOById(Long id) {
        ResourceEntity entity = resourceMapper.selectById(id);
        if (entity == null) {
            return null;
        }
        ResourceEntityVO vo = new ResourceEntityVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    @Override
    public void updateResource(Long id, ResourceUpdateDTO dto) {
        ResourceEntity entity = new ResourceEntity();
        BeanUtils.copyProperties(dto, entity);
        entity.setId(id);
        resourceMapper.updateById(entity);
    }

    @Override
    @Transactional
    public void deleteResource(Long id) {
        // 1. 先从数据库查找资源实体
        ResourceEntity entity = resourceMapper.selectById(id);
        if (entity == null) {
            // 如果记录不存在，直接返回
            return;
        }

        // 2. 调用 StorageService 删除物理文件
        try {
            storageService.delete(entity.getPath());
        } catch (IOException e) {
            // 在生产环境中，这里应该记录错误日志
            System.err.println("删除物理文件失败: " + entity.getPath());
            // 根据业务需求，可以选择抛出异常来回滚事务，或者仅记录日志
            throw new RuntimeException("删除物理文件失败", e);
        }

        // 3. 删除数据库中的记录
        resourceMapper.deleteById(id);
    }

    private ResourceType detectType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null) return ResourceType.OTHER;
        if (contentType.startsWith("video/")) return ResourceType.VIDEO;
        if (contentType.startsWith("image/")) return ResourceType.IMAGE;
        if (contentType.startsWith("application/pdf")) return ResourceType.PDF;
        if (contentType.contains("presentation") || contentType.contains("powerpoint")) return ResourceType.PPT;
        if (contentType.contains("document") || contentType.contains("word")) return ResourceType.DOC;
        return ResourceType.OTHER;
    }
}