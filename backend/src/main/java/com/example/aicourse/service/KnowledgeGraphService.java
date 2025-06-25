package com.example.aicourse.service;

import com.example.aicourse.dto.knowledge.KnowledgeGraphGenerationDTO;
import com.example.aicourse.vo.knowledge.KnowledgeGraphVO;

public interface KnowledgeGraphService {

    /**
     * 获取课程知识图谱数据 (API 11.1)
     */
    KnowledgeGraphVO getKnowledgeGraph(Long courseId);

    /**
     * 智能化提取课程知识点 (API 11.2)
     */
    void generateKnowledgeGraph(Long courseId, KnowledgeGraphGenerationDTO dto);
}