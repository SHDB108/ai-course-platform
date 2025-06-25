package com.example.aicourse.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.aicourse.dto.resource.ResourceUpdateDTO;
import com.example.aicourse.entity.ResourceEntity;
import com.example.aicourse.vo.resource.ResourceEntityVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ResourceService {

    /**
     * 上传资源文件 (对应 API 7.1)
     * @param file 上传的文件
     * @param courseId 关联的课程ID
     * @param description 资源描述
     * @param uploaderId 上传者ID
     * @return 新资源的ID
     */
    Long uploadResource(MultipartFile file, Long courseId, String description, Long uploaderId) throws IOException;

    /**
     * 获取课程资源列表 (对应 API 7.3)
     * @param page 分页对象
     * @param courseId 课程ID
     * @param type 资源类型
     * @param keyword 文件名关键词
     * @return 分页的资源VO
     */
    Page<ResourceEntityVO> listResourcesByCourse(Page<ResourceEntityVO> page, Long courseId, String type, String keyword);

    /**
     * 获取资源详情 (对应 API 7.4)
     * @param id 资源ID
     * @return 资源VO
     */
    ResourceEntityVO getResourceVOById(Long id);

    /**
     * 更新资源信息 (对应 API 7.5)
     * @param id 资源ID
     * @param dto 更新数据
     */
    void updateResource(Long id, ResourceUpdateDTO dto);

    /**
     * 删除资源 (对应 API 7.6)
     * @param id 资源ID
     */
    void deleteResource(Long id);
}