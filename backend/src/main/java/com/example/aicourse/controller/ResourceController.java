package com.example.aicourse.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.aicourse.dto.resource.ResourceUpdateDTO;
import com.example.aicourse.entity.ResourceEntity;
import com.example.aicourse.service.ResourceService;
import com.example.aicourse.service.StorageService;
import com.example.aicourse.utils.Result;
import com.example.aicourse.vo.PageVO;
import com.example.aicourse.vo.resource.ResourceEntityVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/v1/resources") //
@Validated
public class ResourceController {

    private final StorageService storageService;

    private final ResourceService resourceService;

    @Autowired
    public ResourceController(StorageService storageService, ResourceService resourceService) {
        this.storageService = storageService;
        this.resourceService = resourceService;
    }

    /**
     * API 7.1 上传资源文件
     */
    @PostMapping
    public Result<Long> upload(
            @RequestPart("file") MultipartFile file,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String description) throws IOException {

        // TODO: 应该从Spring Security等安全上下文中获取真实用户ID
        Long uploaderId = currentUserId();

        Long resourceId = resourceService.uploadResource(file, courseId, description, uploaderId);
        return Result.ok(resourceId);
    }

    /**
     * API 7.2 下载资源文件
     */
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id) throws IOException {
        ResourceEntityVO res = resourceService.getResourceVOById(id);
        if (res == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + URLEncoder.encode(res.getFilename(), StandardCharsets.UTF_8))
                .body(storageService.load(res.getPath()));
    }

    /**
     * API 7.3 获取课程资源列表
     */
    @GetMapping("/course/{courseId}")
    public Result<PageVO<ResourceEntityVO>> listByCourse(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword) {

        Page<ResourceEntityVO> pageParam = new Page<>(page, size);
        Page<ResourceEntityVO> resultPage = resourceService.listResourcesByCourse(pageParam, courseId, type, keyword);

        PageVO<ResourceEntityVO> pageVO = new PageVO<>(
                resultPage.getRecords(),
                resultPage.getTotal(),
                resultPage.getSize(),
                resultPage.getCurrent()
        );
        return Result.ok(pageVO);
    }

    /**
     * API 7.4 获取资源详情
     */
    @GetMapping("/{id}")
    public Result<ResourceEntityVO> getById(@PathVariable Long id) {
        ResourceEntityVO vo = resourceService.getResourceVOById(id);
        return Result.ok(vo);
    }

    /**
     * API 7.5 更新资源信息
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody ResourceUpdateDTO dto) {
        resourceService.updateResource(id, dto);
        return Result.ok();
    }

    /**
     * API 7.6 删除资源
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        resourceService.deleteResource(id);
        return Result.ok();
    }

    /**
     * 获取当前用户ID的占位实现
     * TODO: 应替换为从Spring Security等安全上下文中获取真实用户ID的逻辑
     * @return 写死的ID 1L
     */
    private Long currentUserId() {
        return 1L; // 占位符
    }
}