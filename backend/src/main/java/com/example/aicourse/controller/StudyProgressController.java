package com.example.aicourse.controller;

import com.example.aicourse.utils.Result;
import com.example.aicourse.dto.study.*;
import com.example.aicourse.service.StudyProgressService;
import com.example.aicourse.vo.study.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学习进度控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/study")
public class StudyProgressController {
    
    private final StudyProgressService studyProgressService;
    
    @Autowired
    public StudyProgressController(StudyProgressService studyProgressService) {
        this.studyProgressService = studyProgressService;
    }
    
    @GetMapping("/progress/{studentId}/{courseId}")
    public Result<StudyProgressVO> getStudyProgress(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {
        try {
            StudyProgressVO progress = studyProgressService.getStudyProgress(studentId, courseId);
            return Result.ok(progress);
        } catch (Exception e) {
            log.error("获取学习进度失败", e);
            return Result.error("获取学习进度失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/progress/{studentId}")
    public Result<List<StudyProgressVO>> getAllStudyProgress(
            @PathVariable Long studentId) {
        try {
            List<StudyProgressVO> progressList = studyProgressService.getAllStudyProgress(studentId);
            return Result.ok(progressList);
        } catch (Exception e) {
            log.error("获取所有学习进度失败", e);
            return Result.error("获取学习进度失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/progress/{studentId}/{courseId}")
    public Result<Void> updateStudyProgress(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {
        try {
            studyProgressService.updateStudyProgress(studentId, courseId);
            return Result.ok();
        } catch (Exception e) {
            log.error("更新学习进度失败", e);
            return Result.error("更新学习进度失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/analysis/{studentId}/{courseId}")
    public Result<StudyAnalysisVO> getStudyAnalysis(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {
        try {
            StudyAnalysisVO analysis = studyProgressService.getStudyAnalysis(studentId, courseId);
            return Result.ok(analysis);
        } catch (Exception e) {
            log.error("获取学习分析失败", e);
            return Result.error("获取学习分析失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/analysis/{studentId}")
    public Result<StudyAnalysisVO> getOverallStudyAnalysis(
            @PathVariable Long studentId) {
        try {
            StudyAnalysisVO analysis = studyProgressService.getOverallStudyAnalysis(studentId);
            return Result.ok(analysis);
        } catch (Exception e) {
            log.error("获取整体学习分析失败", e);
            return Result.error("获取学习分析失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/sessions/{studentId}")
    public Result<Long> startStudySession(
            @PathVariable Long studentId,
            @RequestBody StudySessionCreateDTO createDTO) {
        try {
            Long sessionId = studyProgressService.startStudySession(studentId, createDTO);
            return Result.ok(sessionId);
        } catch (Exception e) {
            log.error("开始学习会话失败", e);
            return Result.error("开始学习会话失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/sessions/{sessionId}")
    public Result<Void> endStudySession(
            @PathVariable Long sessionId,
            @RequestBody StudySessionUpdateDTO updateDTO) {
        try {
            studyProgressService.endStudySession(sessionId, updateDTO);
            return Result.ok();
        } catch (Exception e) {
            log.error("结束学习会话失败", e);
            return Result.error("结束学习会话失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/sessions/{studentId}")
    public Result<List<StudySessionVO>> getStudySessionHistory(
            @PathVariable Long studentId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(defaultValue = "20") Integer limit) {
        try {
            List<StudySessionVO> sessions = studyProgressService.getStudySessionHistory(studentId, courseId, limit);
            return Result.ok(sessions);
        } catch (Exception e) {
            log.error("获取学习会话历史失败", e);
            return Result.error("获取学习会话历史失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/plans/{studentId}")
    public Result<Long> createStudyPlan(
            @PathVariable Long studentId,
            @RequestBody StudyPlanCreateDTO createDTO) {
        try {
            Long planId = studyProgressService.createStudyPlan(studentId, createDTO);
            return Result.ok(planId);
        } catch (Exception e) {
            log.error("创建学习计划失败", e);
            return Result.error("创建学习计划失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/plans/{studentId}")
    public Result<List<StudyPlanVO>> getStudyPlans(
            @PathVariable Long studentId,
            @RequestParam(required = false) Long courseId) {
        try {
            List<StudyPlanVO> plans = studyProgressService.getStudyPlans(studentId, courseId);
            return Result.ok(plans);
        } catch (Exception e) {
            log.error("获取学习计划失败", e);
            return Result.error("获取学习计划失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/plans/{planId}/progress")
    public Result<Void> updateStudyPlanProgress(
            @PathVariable Long planId,
            @RequestParam Integer progress) {
        try {
            studyProgressService.updateStudyPlanProgress(planId, progress);
            return Result.ok();
        } catch (Exception e) {
            log.error("更新学习计划进度失败", e);
            return Result.error("更新学习计划进度失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/plans/{studentId}/{courseId}/generate")
    public Result<StudyPlanVO> generateAiStudyPlan(
            @PathVariable Long studentId,
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "WEEKLY") String planType) {
        try {
            StudyPlanVO plan = studyProgressService.generateAiStudyPlan(studentId, courseId, planType);
            return Result.ok(plan);
        } catch (Exception e) {
            log.error("生成AI学习计划失败", e);
            return Result.error("生成学习计划失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/knowledge-points/{studentId}/{knowledgePointId}")
    public Result<Void> updateKnowledgePointProgress(
            @PathVariable Long studentId,
            @PathVariable Long knowledgePointId,
            @RequestBody KnowledgePointProgressUpdateDTO updateDTO) {
        try {
            studyProgressService.updateKnowledgePointProgress(studentId, knowledgePointId, updateDTO);
            return Result.ok();
        } catch (Exception e) {
            log.error("更新知识点掌握进度失败", e);
            return Result.error("更新知识点掌握进度失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/knowledge-points/{studentId}/{courseId}")
    public Result<List<Object>> getKnowledgePointProgress(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {
        try {
            List<Object> progress = studyProgressService.getKnowledgePointProgress(studentId, courseId);
            return Result.ok(progress);
        } catch (Exception e) {
            log.error("获取知识点掌握情况失败", e);
            return Result.error("获取知识点掌握情况失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/knowledge-points/{studentId}/review")
    public Result<List<Object>> getKnowledgePointsNeedReview(
            @PathVariable Long studentId) {
        try {
            List<Object> knowledgePoints = studyProgressService.getKnowledgePointsNeedReview(studentId);
            return Result.ok(knowledgePoints);
        } catch (Exception e) {
            log.error("获取需要复习的知识点失败", e);
            return Result.error("获取需要复习的知识点失败: " + e.getMessage());
        }
    }
}