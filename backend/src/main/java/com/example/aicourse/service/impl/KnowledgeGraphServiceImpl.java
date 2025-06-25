package com.example.aicourse.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.aicourse.dto.knowledge.KnowledgeGraphGenerationDTO;
import com.example.aicourse.entity.KnowledgePoint;
import com.example.aicourse.entity.KnowledgePointRelation;
import com.example.aicourse.repository.KnowledgePointMapper;
import com.example.aicourse.repository.KnowledgePointRelationMapper;
import com.example.aicourse.service.KnowledgeGraphService;
import com.example.aicourse.vo.knowledge.KnowledgeGraphVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KnowledgeGraphServiceImpl implements KnowledgeGraphService {

    @Autowired
    private KnowledgePointMapper knowledgePointMapper;
    @Autowired
    private KnowledgePointRelationMapper relationMapper;

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

    @Override
    @Async // 使用异步执行，避免长时间阻塞API请求
    public void generateKnowledgeGraph(Long courseId, KnowledgeGraphGenerationDTO dto) {
        System.out.println("开始为课程 " + courseId + " 生成知识图谱...");
        // TODO: 实现与大语言模型(LLM)的交互
        // 1. 根据 dto.getResourceIds() 从数据库或文件系统中获取课程材料（如PDF、PPT文本内容）。
        // 2. 构建一个详细的Prompt，要求LLM根据提供的文本，抽取出核心知识点以及它们之间的层级或关联关系，并以JSON格式返回。
        //    例如: "请从以下文本中抽取出知识点，并以{nodes:[{name, desc}], edges:[{source, target, relation}]}的JSON格式返回..."
        // 3. 使用 RestTemplate 或 HttpClient 调用LLM的API接口。
        //    - 需要处理API Key、网络请求、超时等问题。
        // 4. 解析LLM返回的JSON数据。
        // 5. 将解析出的知识点和关系数据，保存到 t_knowledge_point 和 t_knowledge_point_relation 表中。
        //    - 在保存前可能需要进行去重或与现有数据进行比对。
        // 6. （可选）任务完成后，通过WebSocket或其他方式通知前端。
        System.out.println("知识图谱生成任务已在后台处理。");
    }
}