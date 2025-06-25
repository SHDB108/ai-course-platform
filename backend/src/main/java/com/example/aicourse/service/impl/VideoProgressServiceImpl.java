package com.example.aicourse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.aicourse.dto.resource.ProgressDTO;
import com.example.aicourse.entity.VideoProgress;
import com.example.aicourse.repository.ResourceMapper;
import com.example.aicourse.repository.VideoProgressMapper;
import com.example.aicourse.service.VideoProgressService;
import com.example.aicourse.vo.resource.VideoProgressVO;
import com.example.aicourse.vo.resource.VideoStudyStatisticsVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class VideoProgressServiceImpl implements VideoProgressService {

    @Autowired
    private VideoProgressMapper mapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ResourceMapper resourceMapper;

    @Override
    public void record(Long resId, Long studentId, ProgressDTO dto) {
        // 查询是否已有记录，有则更新，无则插入
        LambdaQueryWrapper<VideoProgress> query = new LambdaQueryWrapper<VideoProgress>()
                .eq(VideoProgress::getResourceId, resId)
                .eq(VideoProgress::getStudentId, studentId);
        VideoProgress vp = mapper.selectOne(query);

        if (vp == null) {
            vp = new VideoProgress();
            vp.setResourceId(resId);
            vp.setStudentId(studentId);
        }

        try {
            // 只序列化segments和elapsed，而不是整个DTO
            String progressJson = objectMapper.writeValueAsString(dto);
            vp.setProgress(progressJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("视频进度序列化失败", e);
        }
        vp.setCompletion(dto.getCompletion());

        if (vp.getId() == null) {
            mapper.insert(vp);
        } else {
            mapper.updateById(vp);
        }
    }

    @Override
    public VideoProgressVO getStudentProgress(Long resourceId, Long studentId) {
        LambdaQueryWrapper<VideoProgress> query = new LambdaQueryWrapper<VideoProgress>()
                .eq(VideoProgress::getResourceId, resourceId)
                .eq(VideoProgress::getStudentId, studentId);
        VideoProgress vp = mapper.selectOne(query);

        if (vp == null) {
            return null;
        }

        VideoProgressVO vo = new VideoProgressVO();
        BeanUtils.copyProperties(vp, vo);
        return vo;
    }

    @Override
    public List<VideoStudyStatisticsVO> getCourseVideoStatistics(Long courseId, Long studentId) {
        // studentId 参数暂时未使用，当前逻辑为统计整个课程的情况。
        // 如果需要针对单个学生统计，则需编写不同的或更复杂的SQL。

        List<VideoStudyStatisticsVO> stats = mapper.selectCourseVideoStatistics(courseId);

        // 注意：热力图(heatmapData)的实现非常复杂，它需要解析和聚合每个视频进度中的
        // progress JSON字段。这通常在数据库层面（需要较新版本的MySQL或PG）或在
        // 服务层通过大量计算来完成。作为第一步，我们暂时返回一个空列表。
        stats.forEach(s -> s.setHeatmapData(new ArrayList<>()));

        return stats;
    }
}