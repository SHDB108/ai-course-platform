package com.example.aicourse.service.impl;

import com.example.aicourse.dto.resource.ProgressDTO;
import com.example.aicourse.entity.VideoProgress;
import com.example.aicourse.repository.VideoProgressMapper;
import com.example.aicourse.service.VideoProgressService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class VideoProgressServiceImpl implements VideoProgressService {

    @Autowired
    private VideoProgressMapper mapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void record(Long resId, Long studentId, ProgressDTO dto) {
        VideoProgress vp = new VideoProgress();
        vp.setResourceId(resId);
        vp.setStudentId(studentId);
        try {
            String progressJson = objectMapper.writeValueAsString(dto.getSegments());
            vp.setProgress(progressJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("视频进度序列化失败", e);
        }
        vp.setCompletion(dto.getCompletion());
        vp.setGmtCreate(LocalDateTime.now());
        vp.setGmtModified(LocalDateTime.now());
        mapper.insert(vp);
    }
}