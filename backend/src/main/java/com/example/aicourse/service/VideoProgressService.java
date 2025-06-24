package com.example.aicourse.service;


import com.example.aicourse.dto.resource.ProgressDTO;

public interface VideoProgressService {
    /** 记录视频播放进度 */
    void record(Long resId, Long studentId, ProgressDTO dto);
}