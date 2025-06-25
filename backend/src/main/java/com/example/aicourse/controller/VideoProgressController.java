package com.example.aicourse.controller;

import com.example.aicourse.dto.resource.ProgressDTO;
import com.example.aicourse.service.VideoProgressService;
import com.example.aicourse.utils.Result;
import com.example.aicourse.vo.resource.VideoProgressVO;
import com.example.aicourse.vo.resource.VideoStudyStatisticsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/videos") //  路径根据API文档建议修改
@Validated
public class VideoProgressController {

    private final VideoProgressService service;

    @Autowired
    public VideoProgressController(VideoProgressService service) {
        this.service = service;
    }

    /**
     * API 7.7 上报视频学习进度
     */
    @PostMapping("/{resourceId}/progress")
    public Result<Void> report(@PathVariable Long resourceId, @RequestBody ProgressDTO dto) {
        // TODO: studentId 应从安全上下文中获取
        Long studentId = currentUserId();
        service.record(resourceId, studentId, dto);
        return Result.ok();
    }

    /**
     * API 7.8 获取学生视频学习进度
     */
    @GetMapping("/{resourceId}/progress/student/{studentId}")
    public Result<VideoProgressVO> getProgress(@PathVariable Long resourceId, @PathVariable Long studentId) {
        VideoProgressVO vo = service.getStudentProgress(resourceId, studentId);
        return Result.ok(vo);
    }

    /**
     * API 7.9 获取课程视频学习统计
     */
    @GetMapping("/course/{courseId}/statistics")
    public Result<List<VideoStudyStatisticsVO>> getStatistics(
            @PathVariable Long courseId,
            @RequestParam(required = false) Long studentId) {
        List<VideoStudyStatisticsVO> list = service.getCourseVideoStatistics(courseId, studentId);
        return Result.ok(list);
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