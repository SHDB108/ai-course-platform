package com.example.aicourse.controller;

import com.example.aicourse.dto.knowledge.KnowledgeGraphGenerationDTO;
import com.example.aicourse.service.KnowledgeGraphService;
import com.example.aicourse.utils.Result;
import com.example.aicourse.vo.knowledge.KnowledgeGraphVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 知识图谱相关API
 */
@RestController
@RequestMapping("/api/v1/knowledge-graphs")
@Validated
public class KnowledgeGraphController {

    private final KnowledgeGraphService knowledgeGraphService;

    @Autowired
    public KnowledgeGraphController(KnowledgeGraphService knowledgeGraphService) {
        this.knowledgeGraphService = knowledgeGraphService;
    }

    /**
     * API 11.1: 获取课程知识图谱数据
     *
     * @param courseId 课程的ID
     * @return 包含节点和边的知识图谱VO
     */
    @GetMapping("/course/{courseId}")
    public Result<KnowledgeGraphVO> getKnowledgeGraph(@PathVariable Long courseId) {
        KnowledgeGraphVO graphVO = knowledgeGraphService.getKnowledgeGraph(courseId);
        return Result.ok(graphVO);
    }

    /**
     * API 11.2: 触发智能化知识图谱生成
     * <p>
     * 这是一个异步接口，调用后会立即返回，后台将开始执行知识图谱的生成任务。
     *
     * @param courseId 目标课程的ID
     * @param dto      包含资源ID列表和生成策略等信息的请求体
     * @return 操作成功响应
     */
    @PostMapping("/course/{courseId}/generate")
    public Result<Void> generateKnowledgeGraph(
            @PathVariable Long courseId,
            @Valid @RequestBody KnowledgeGraphGenerationDTO dto) {
        knowledgeGraphService.generateKnowledgeGraph(courseId, dto);
        return Result.ok();
    }
}