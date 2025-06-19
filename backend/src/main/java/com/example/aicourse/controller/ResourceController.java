package com.example.aicourse.controller;

import com.example.aicourse.entity.ResourceEntity;
import com.example.aicourse.repository.ResourceMapper;
import com.example.aicourse.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/v1/resources")
public class ResourceController {
    @Autowired
    StorageService storageService;
    @Autowired
    ResourceMapper mapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public com.example.aicourse.util.Result<Long> upload(@RequestPart MultipartFile file) throws IOException {
        String path = storageService.upload(file);
        ResourceEntity entity = new ResourceEntity(file.getOriginalFilename(), path, detectType(file), file.getSize(), currentUserId());
        mapper.insert(entity);
        return com.example.aicourse.util.Result.ok(entity.getId());
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id) throws IOException {
        ResourceEntity res = mapper.selectById(id);
        if (res == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + URLEncoder.encode(res.getFilename(), StandardCharsets.UTF_8))
                .body(storageService.load(res.getPath()));
    }

    /**
     * [新增] 根据文件MIME类型判断资源类型
     * @param file 上传的文件
     * @return "video", "image", "doc", or "other"
     */
    private String detectType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null) {
            return "other";
        }
        if (contentType.startsWith("video/")) {
            return "video";
        }
        if (contentType.startsWith("image/")) {
            return "image";
        }
        if (contentType.startsWith("application/pdf") || contentType.contains("document")) {
            return "doc";
        }
        return "other";
    }

    /**
     * [新增] 获取当前用户ID的占位实现
     * TODO: 应替换为从Spring Security等安全上下文中获取真实用户ID的逻辑
     * @return 写死的ID 1L
     */
    private Long currentUserId() {
        return 1L; // 占位符
    }
}