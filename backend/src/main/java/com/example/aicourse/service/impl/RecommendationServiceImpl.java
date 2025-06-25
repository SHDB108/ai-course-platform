package com.example.aicourse.service.impl;

import com.example.aicourse.dto.knowledge.RecommendationStatusUpdateDTO;
import com.example.aicourse.entity.LearningRecommendation;
import com.example.aicourse.repository.LearningRecommendationMapper;
import com.example.aicourse.service.AnalyticsService;
import com.example.aicourse.service.RecommendationService;
import com.example.aicourse.vo.analytics.KnowledgePointPerformanceVO;
import com.example.aicourse.vo.knowledge.LearningRecommendationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    private final LearningRecommendationMapper recommendationMapper;

    private final AnalyticsService analyticsService; // 注入分析服务来获取学生表现

    @Autowired
    public RecommendationServiceImpl(LearningRecommendationMapper recommendationMapper, AnalyticsService analyticsService) {
        this.recommendationMapper = recommendationMapper;
        this.analyticsService = analyticsService;
    }

    @Override
    public List<LearningRecommendationVO> getRecommendations(Long studentId, Long courseId, String recommendationType, Integer count) {
        // TODO: 实现复杂的推荐生成逻辑
        // 1. 调用分析服务，获取学生在知识点上的表现
        List<KnowledgePointPerformanceVO> performanceList = analyticsService.getKnowledgePointPerformance(courseId, studentId);

        // 2. 找出表现不佳的知识点 (例如，平均分低于某个阈值)
        List<KnowledgePointPerformanceVO> weakPoints = performanceList.stream()
                .filter(p -> p.getAverageScore() != null && p.getAverageScore().doubleValue() < 70)
                .toList();

        // 3. 根据薄弱点生成推荐VO
        //    - 可以在 t_learning_recommendation 表中查找已有的推荐
        //    - 或者动态生成
        //    - 还需要查询与知识点关联的资源(ResourceEntity)
        System.out.println("为学生 " + studentId + " 在课程 " + courseId + " 中发现 " + weakPoints.size() + " 个薄弱知识点。");

        // 返回一个空列表作为占位符
        return Collections.emptyList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRecommendationStatus(Long recommendationId, RecommendationStatusUpdateDTO dto) {
        LearningRecommendation recommendation = recommendationMapper.selectById(recommendationId);
        if (recommendation != null) {
            // "DISMISSED" -> isDismissed = 1
            if ("DISMISSED".equalsIgnoreCase(dto.getStatus())) {
                recommendation.setIsDismissed(1);
                recommendationMapper.updateById(recommendation);
            }
            // 可以扩展其他状态
        }
    }
}