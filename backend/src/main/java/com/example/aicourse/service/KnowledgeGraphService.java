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
     * <p>
     * 此方法应为异步执行。它会启动一个后台任务，该任务:
     * 1. 根据 DTO 中的 resourceIds 获取课程材料文本。
     * 2. 调用 LLM 服务进行知识点和关系的提取。
     * 3. 将提取的结果存入 t_knowledge_point 和 t_knowledge_point_relation 表。
     *
     * @param courseId 目标课程ID
     * @param dto 包含资源ID列表和生成策略的请求
     */
    void generateKnowledgeGraph(Long courseId, KnowledgeGraphGenerationDTO dto);
}