package com.example.aicourse.controller;

import com.example.aicourse.dto.ProgressDTO;
import com.example.aicourse.service.VideoProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/video")
public class VideoProgressController {
    @Autowired
    VideoProgressService service;

    @PostMapping("/{resId}/progress")
    public com.example.aicourse.util.Result<Void> report(@PathVariable Long resId, @RequestBody ProgressDTO dto) {
        service.record(resId, currentUserId(), dto);
        return com.example.aicourse.util.Result.ok();
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