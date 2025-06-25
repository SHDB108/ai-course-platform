package com.example.aicourse.service;

import com.example.aicourse.dto.knowledge.RecommendationStatusUpdateDTO;
import com.example.aicourse.vo.knowledge.LearningRecommendationVO;

import java.util.List;

public interface RecommendationService {

    /**
     * 获取学习推荐 (API 11.3)
     */
    List<LearningRecommendationVO> getRecommendations(Long studentId, Long courseId, String recommendationType, Integer count);

    /**
     * 更新推荐状态 (API 11.4)
     */
    void updateRecommendationStatus(Long recommendationId, RecommendationStatusUpdateDTO dto);
}