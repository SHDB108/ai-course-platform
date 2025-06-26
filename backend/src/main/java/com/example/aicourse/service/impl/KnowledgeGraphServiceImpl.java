package com.example.aicourse.service.impl;

import com.example.aicourse.utils.TextExtractor;
import org.springframework.http.*;
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
import com.example.aicourse.vo.knowledge.KnowledgeGraphVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
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
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${llm.dify.api-url}")
    private String DifyApiUrl;

    @Value("${llm.dify.model}")
    private String DifyModel;


    @Autowired
    public KnowledgeGraphServiceImpl(KnowledgePointMapper knowledgePointMapper, KnowledgePointRelationMapper relationMapper, ResourceMapper resourceMapper, StorageProperties storageProperties, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.knowledgePointMapper = knowledgePointMapper;
        this.relationMapper = relationMapper;
        this.resourceMapper = resourceMapper;
        this.storageProperties = storageProperties;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public KnowledgeGraphVO getKnowledgeGraph(Long courseId) {
        // 1. 查询所有属于该课程的知识点作为Nodes
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

        // 2. 查询这些知识点之间的关系作为Edges
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
            // 1. 根据resourceIds获取课程材料文本
            List<ResourceEntity> resources = resourceMapper.selectBatchIds(dto.getResourceIds());
            if (CollectionUtils.isEmpty(resources)) {
                log.warn("未找到任何资源，无法生成知识图谱。");
                return;
            }

            StringBuilder combinedText = new StringBuilder();
            for (ResourceEntity resource : resources) {
                Path filePath = storageProperties.getLocalPath().resolve(resource.getPath());
                combinedText.append(TextExtractor.extract(filePath));
                combinedText.append("\n\n"); // 添加分隔符
            }

            if (combinedText.toString().isBlank()) {
                log.warn("从资源文件中提取的文本内容为空。");
                return;
            }

            // 2. 构建Prompt
            String prompt = buildKgPrompt(combinedText.toString());

            // 3. 调用LLM
            String llmResponseJson = callLlmApi(prompt);

            // 4. 解析并存储
            saveGraphFromJson(courseId, llmResponseJson);

            log.info("课程 {} 的知识图谱已成功生成并存储。", courseId);

        } catch (Exception e) {
            log.error("为课程 {} 生成知识图谱时发生错误", courseId, e);
            // 生产环境中可能需要更复杂的错误处理，如发送通知
        }
    }

    private String buildKgPrompt(String courseMaterial) {
        // 限制材料长度，避免超出LLM的上下文限制
        int maxLength = 8000; // 根据你的LLM能力调整
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

    private String callLlmApi(String prompt) {
        // (此方法与IntelligentGradingServiceImpl中的相同，可以提取到公共服务中)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> body = new HashMap<>();
        body.put("model", DifyModel);
        body.put("prompt", prompt);
        body.put("stream", false);
        body.put("format", "json");
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(DifyApiUrl, entity, String.class);
            Map<String, Object> DifyResponse = objectMapper.readValue(response.getBody(), new TypeReference<>() {});
            return (String) DifyResponse.get("response");
        } catch (Exception e) {
            log.error("调用LLM服务失败", e);
            throw new RuntimeException("调用LLM服务失败: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void saveGraphFromJson(Long courseId, String json) throws Exception {
        // 定义一个内部记录类来匹配JSON结构
        TypeReference<Map<String, List<Map<String, String>>>> typeRef = new TypeReference<>() {};
        Map<String, List<Map<String, String>>> graphData = objectMapper.readValue(json, typeRef);

        List<Map<String, String>> nodes = graphData.get("nodes");
        List<Map<String, String>> edges = graphData.get("edges");

        // 如果dto中要求更新，则先删除旧图谱
        // if(dto.getAutoUpdate()){ ... }
        knowledgePointMapper.delete(Wrappers.<KnowledgePoint>lambdaQuery().eq(KnowledgePoint::getCourseId, courseId));
        // 注意：删除关系更复杂，需要先找到所有节点的ID

        // 存储节点并建立名称到ID的映射
        Map<String, Long> nameToIdMap = new HashMap<>();
        for (Map<String, String> nodeData : nodes) {
            KnowledgePoint kp = new KnowledgePoint();
            kp.setCourseId(courseId);
            kp.setName(nodeData.get("name"));
            kp.setDescription(nodeData.get("description"));
            knowledgePointMapper.insert(kp);
            nameToIdMap.put(kp.getName(), kp.getId());
        }

        // 存储关系
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