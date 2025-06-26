package com.example.aicourse.service.impl;

import com.example.aicourse.utils.TextExtractor;
import org.springframework.util.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.aicourse.config.StorageProperties;
import com.example.aicourse.dto.knowledge.KnowledgeGraphGenerationDTO;
import com.example.aicourse.entity.KnowledgePoint;
import com.example.aicourse.entity.KnowledgePointRelation;
import com.example.aicourse.entity.ResourceEntity;
import com.example.aicourse.repository.KnowledgePointMapper;
import com.example.aicourse.repository.KnowledgePointRelationMapper;
import com.example.aicourse.repository.ResourceMapper;
import com.example.aicourse.service.KnowledgeGraphService;
import com.example.aicourse.service.LlmService;
import com.example.aicourse.vo.knowledge.KnowledgeGraphVO;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class KnowledgeGraphServiceImpl implements KnowledgeGraphService {

    private final KnowledgePointMapper knowledgePointMapper;
    private final KnowledgePointRelationMapper relationMapper;
    private final ResourceMapper resourceMapper;
    private final StorageProperties storageProperties;
    private final LlmService llmService; // 【优化】依赖抽象的LlmService

    @Autowired
    public KnowledgeGraphServiceImpl(KnowledgePointMapper knowledgePointMapper, KnowledgePointRelationMapper relationMapper, ResourceMapper resourceMapper, StorageProperties storageProperties, LlmService llmService) {
        this.knowledgePointMapper = knowledgePointMapper;
        this.relationMapper = relationMapper;
        this.resourceMapper = resourceMapper;
        this.storageProperties = storageProperties;
        this.llmService = llmService;
    }

    @Override
    public KnowledgeGraphVO getKnowledgeGraph(Long courseId) {
        List<KnowledgePoint> points = knowledgePointMapper.selectList(
                Wrappers.<KnowledgePoint>lambdaQuery().eq(KnowledgePoint::getCourseId, courseId)
        );
        List<KnowledgeGraphVO.Node> nodes = points.stream().map(p -> {
            KnowledgeGraphVO.Node node = new KnowledgeGraphVO.Node();
            node.setId(p.getId());
            node.setName(p.getName());
            node.setDescription(p.getDescription());
            node.setType("knowledgePoint");
            return node;
        }).collect(Collectors.toList());

        List<Long> pointIds = points.stream().map(KnowledgePoint::getId).collect(Collectors.toList());
        List<KnowledgePointRelation> relations = relationMapper.selectList(
                Wrappers.<KnowledgePointRelation>lambdaQuery().in(KnowledgePointRelation::getSourceId, pointIds)
        );
        List<KnowledgeGraphVO.Edge> edges = relations.stream().map(r -> {
            KnowledgeGraphVO.Edge edge = new KnowledgeGraphVO.Edge();
            edge.setSource(r.getSourceId());
            edge.setTarget(r.getTargetId());
            edge.setRelation(r.getRelationType());
            return edge;
        }).collect(Collectors.toList());

        KnowledgeGraphVO vo = new KnowledgeGraphVO();
        vo.setNodes(nodes);
        vo.setEdges(edges);
        return vo;
    }

    @Async
    @Override
    @Transactional
    public void generateKnowledgeGraph(Long courseId, KnowledgeGraphGenerationDTO dto) {
        log.info("开始为课程 {} 异步生成知识图谱...", courseId);
        try {
            List<ResourceEntity> resources = resourceMapper.selectBatchIds(dto.getResourceIds());
            if (CollectionUtils.isEmpty(resources)) {
                log.warn("未找到任何资源，无法生成知识图谱。");
                return;
            }

            StringBuilder combinedText = new StringBuilder();
            for (ResourceEntity resource : resources) {
                Path filePath = storageProperties.getLocalPath().resolve(resource.getPath());
                combinedText.append(TextExtractor.extract(filePath));
                combinedText.append("\n\n");
            }

            if (combinedText.toString().isBlank()) {
                log.warn("从资源文件中提取的文本内容为空。");
                return;
            }

            String prompt = buildKgPrompt(combinedText.toString());

            // 【优化】调用LlmService处理泛型JSON
            Map<String, List<Map<String, String>>> graphData = llmService.generateJson(prompt, new TypeReference<>() {});

            saveGraphFromMap(courseId, graphData);

            log.info("课程 {} 的知识图谱已成功生成并存储。", courseId);

        } catch (Exception e) {
            log.error("为课程 {} 生成知识图谱时发生错误", courseId, e);
        }
    }

    private String buildKgPrompt(String courseMaterial) {
        int maxLength = 8000;
        String truncatedMaterial = courseMaterial.length() > maxLength ? courseMaterial.substring(0, maxLength) : courseMaterial;

        return String.format("""
            你是一个专业的学科专家，请根据以下课程材料，抽取出核心知识点以及它们之间的关系。

            # 关系类型说明:
            - "PREREQUISITE": B是A的前置知识。
            - "RELATED": A和B是相关概念。
            - "PART_OF": A是B的一部分。
            
            # 课程材料:
            ---
            %s
            ---

            # 要求:
            请严格按照以下JSON格式返回，不要添加任何额外的解释。知识点名称必须唯一。
            {
              "nodes": [
                {"name": "知识点名称1", "description": "一句话核心描述"},
                {"name": "知识点名称2", "description": "一句话核心描述"}
              ],
              "edges": [
                {"source": "知识点名称1", "target": "知识点名称2", "relation": "PREREQUISITE"}
              ]
            }
            """, truncatedMaterial);
    }

    @Transactional
    public void saveGraphFromMap(Long courseId, Map<String, List<Map<String, String>>> graphData) {
        List<Map<String, String>> nodes = graphData.get("nodes");
        List<Map<String, String>> edges = graphData.get("edges");

        knowledgePointMapper.delete(Wrappers.<KnowledgePoint>lambdaQuery().eq(KnowledgePoint::getCourseId, courseId));

        Map<String, Long> nameToIdMap = new HashMap<>();
        if (nodes != null) {
            for (Map<String, String> nodeData : nodes) {
                KnowledgePoint kp = new KnowledgePoint();
                kp.setCourseId(courseId);
                kp.setName(nodeData.get("name"));
                kp.setDescription(nodeData.get("description"));
                knowledgePointMapper.insert(kp);
                nameToIdMap.put(kp.getName(), kp.getId());
            }
        }

        if (edges != null) {
            for (Map<String, String> edgeData : edges) {
                Long sourceId = nameToIdMap.get(edgeData.get("source"));
                Long targetId = nameToIdMap.get(edgeData.get("target"));
                if (sourceId != null && targetId != null) {
                    KnowledgePointRelation relation = new KnowledgePointRelation();
                    relation.setSourceId(sourceId);
                    relation.setTargetId(targetId);
                    relation.setRelationType(edgeData.get("relation"));
                    relationMapper.insert(relation);
                }
            }
        }
    }
}