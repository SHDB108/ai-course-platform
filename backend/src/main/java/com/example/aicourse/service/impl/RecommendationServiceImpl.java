package com.example.aicourse.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.aicourse.dto.knowledge.RecommendationStatusUpdateDTO;
import com.example.aicourse.entity.LearningRecommendation;
import com.example.aicourse.repository.LearningRecommendationMapper;
import com.example.aicourse.service.AnalyticsService;
import com.example.aicourse.service.RecommendationService;
import com.example.aicourse.vo.analytics.KnowledgePointPerformanceVO;
import com.example.aicourse.vo.knowledge.LearningRecommendationVO;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.CollectionUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;

import java.util.*;

@Slf4j
@Service
public class RecommendationServiceImpl implements RecommendationService {

    private final LearningRecommendationMapper recommendationMapper;

    private final AnalyticsService analyticsService; // 注入分析服务来获取学生表现

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    @Value("${llm.dify.api-url}")
    private String DifyApiUrl;

    @Value("${llm.dify.model}")
    private String DifyModel;


    @Autowired
    public RecommendationServiceImpl(LearningRecommendationMapper recommendationMapper, AnalyticsService analyticsService, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.recommendationMapper = recommendationMapper;
        this.analyticsService = analyticsService;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
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

        // 先删除该学生在该课程的旧推荐
        recommendationMapper.delete(
                Wrappers.<LearningRecommendation>lambdaQuery()
                        .eq(LearningRecommendation::getStudentId, studentId)
                        .eq(LearningRecommendation::getCourseId, courseId)
        );

        int count = 0;
        for (KnowledgePointPerformanceVO weakPoint : weakPoints) {
            try {
                // 2. 为每个薄弱点生成个性化建议
                String prompt = buildRecommendationPrompt(weakPoint.getKnowledgePointName());
                String recommendationText = callLlmToGetReason(prompt);

                // 3. 创建并存储推荐记录
                LearningRecommendation recommendation = new LearningRecommendation();
                recommendation.setStudentId(studentId);
                recommendation.setCourseId(courseId);
                recommendation.setRecommendationType("KNOWLEDGE_POINT"); // 类型为知识点
                recommendation.setTargetId(weakPoint.getKnowledgePointId()); // 关联知识点ID
                recommendation.setReason(recommendationText);
                recommendation.setIsDismissed(0); // 默认未忽略
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

    private String callLlmToGetReason(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> body = new HashMap<>();
        body.put("model", DifyModel);
        body.put("prompt", prompt);
        body.put("stream", false);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        try {
            // 【修复一 & 二】: 正确使用 this.restTemplate，并接收String以避免原始类型警告
            ResponseEntity<String> response = this.restTemplate.postForEntity(DifyApiUrl, entity, String.class);

            // 【修复三 & 四】: 增加健壮的非空检查，避免 NullPointerException
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = objectMapper.readValue(response.getBody(), new TypeReference<>() {});
                // 使用 Objects.toString() 来安全地处理可能为null的内部字段
                String reason = Objects.toString(responseBody.get("response"), null);
                if (reason != null && !reason.isBlank()) {
                    return reason;
                }
            }
        } catch (Exception e) {
            log.error("调用LLM为知识点生成推荐理由时失败", e);
        }
        return "在学习“" + prompt + "”时遇到困难了吗？别担心，回顾一下相关的课程材料，你一定能掌握它！";
    }


    @Override
    public List<LearningRecommendationVO> getRecommendations(Long studentId, Long courseId, String recommendationType, Integer count) {
        log.info("为学生ID: {} 在课程ID: {} 获取最多 {} 条学习推荐。", studentId, courseId, count);

        // 如果未指定数量，默认返回5条
        int limit = (count == null || count <= 0) ? 5 : count;

        // 调用我们新创建的、强大的Mapper方法
        List<LearningRecommendationVO> recommendations = recommendationMapper.selectEnrichedRecommendations(studentId, courseId, limit);

        if (CollectionUtils.isEmpty(recommendations)) {
            return Collections.emptyList();
        }

        return recommendations;
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