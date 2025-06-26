package com.example.aicourse.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.aicourse.dto.knowledge.RecommendationStatusUpdateDTO;
import com.example.aicourse.entity.LearningRecommendation;
import com.example.aicourse.repository.LearningRecommendationMapper;
import com.example.aicourse.service.AnalyticsService;
import com.example.aicourse.service.LlmService;
import com.example.aicourse.service.RecommendationService;
import com.example.aicourse.vo.analytics.KnowledgePointPerformanceVO;
import com.example.aicourse.vo.knowledge.LearningRecommendationVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class RecommendationServiceImpl implements RecommendationService {

    private final LearningRecommendationMapper recommendationMapper;
    private final AnalyticsService analyticsService;
    private final LlmService llmService; // 【优化】依赖抽象的LlmService

    @Autowired
    public RecommendationServiceImpl(LearningRecommendationMapper recommendationMapper, AnalyticsService analyticsService, LlmService llmService) {
        this.recommendationMapper = recommendationMapper;
        this.analyticsService = analyticsService;
        this.llmService = llmService;
    }

    @Override
    @Transactional
    public int generateRecommendationsForStudent(Long studentId, Long courseId) {
        log.info("开始为学生ID: {} 在课程ID: {} 中生成学习推荐。", studentId, courseId);

        // 1. 获取学生表现，找出薄弱点
        List<KnowledgePointPerformanceVO> performanceList = analyticsService.getKnowledgePointPerformance(courseId, studentId);
        List<KnowledgePointPerformanceVO> weakPoints = performanceList.stream()
                .filter(p -> "待加强".equals(p.getMasteryLevel()))
                .toList();

        if (weakPoints.isEmpty()) {
            log.info("学生ID: {} 表现良好，无需生成新的推荐。", studentId);
            return 0;
        }

        // 【优化】不再直接删除旧推荐，而是将未处理的旧推荐标记为“过时”状态 (isDismissed = 2)
        // 0=活跃, 1=用户已忽略, 2=系统判定为过时
        LambdaUpdateWrapper<LearningRecommendation> updateWrapper = Wrappers.<LearningRecommendation>lambdaUpdate()
                .eq(LearningRecommendation::getStudentId, studentId)
                .eq(LearningRecommendation::getCourseId, courseId)
                .eq(LearningRecommendation::getIsDismissed, 0) // 只处理活跃的推荐
                .set(LearningRecommendation::getIsDismissed, 2);
        recommendationMapper.update(null, updateWrapper);

        int count = 0;
        for (KnowledgePointPerformanceVO weakPoint : weakPoints) {
            try {
                // 2. 【优化】通过LlmService生成个性化建议
                String prompt = buildRecommendationPrompt(weakPoint.getKnowledgePointName());
                String recommendationText = llmService.generateText(prompt);

                if (recommendationText == null || recommendationText.isBlank()) {
                    // LLM调用失败时的降级策略
                    recommendationText = "在学习“" + weakPoint.getKnowledgePointName() + "”时遇到困难了吗？别担心，回顾一下相关的课程材料，你一定能掌握它！";
                }

                // 3. 创建并存储新的推荐记录
                LearningRecommendation recommendation = new LearningRecommendation();
                recommendation.setStudentId(studentId);
                recommendation.setCourseId(courseId);
                recommendation.setRecommendationType("KNOWLEDGE_POINT");
                recommendation.setTargetId(weakPoint.getKnowledgePointId());
                recommendation.setReason(recommendationText);
                recommendation.setIsDismissed(0); // 新推荐为活跃状态
                recommendationMapper.insert(recommendation);
                count++;
            } catch (Exception e) {
                log.error("为知识点 '{}' 生成推荐时失败", weakPoint.getKnowledgePointName(), e);
            }
        }
        log.info("成功为学生ID: {} 生成了 {} 条新推荐。", studentId, count);
        return count;
    }

    private String buildRecommendationPrompt(String knowledgePointName) {
        return String.format("""
            你是一个循循善诱的AI助教。请为在知识点 "%s" 上遇到困难的学生，生成一句简短、友好且鼓励性的学习建议。直接返回建议文本即可，不要包含其它任何内容。
            """, knowledgePointName);
    }

    @Override
    public List<LearningRecommendationVO> getRecommendations(Long studentId, Long courseId, String recommendationType, Integer count) {
        log.info("为学生ID: {} 在课程ID: {} 获取最多 {} 条学习推荐。", studentId, courseId, count);

        int limit = (count == null || count <= 0) ? 5 : count;
        List<LearningRecommendationVO> recommendations = recommendationMapper.selectEnrichedRecommendations(studentId, courseId, limit);

        return CollectionUtils.isEmpty(recommendations) ? Collections.emptyList() : recommendations;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRecommendationStatus(Long recommendationId, RecommendationStatusUpdateDTO dto) {
        LearningRecommendation recommendation = recommendationMapper.selectById(recommendationId);
        if (recommendation != null) {
            if ("DISMISSED".equalsIgnoreCase(dto.getStatus())) {
                recommendation.setIsDismissed(1); // 1 = 用户已忽略
                recommendationMapper.updateById(recommendation);
            }
        }
    }
}