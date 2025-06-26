package com.example.aicourse.service.impl;

import com.example.aicourse.repository.KnowledgePointMapper;
import com.example.aicourse.repository.KnowledgePointRelationMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KnowledgeGraphServiceImplTest {

    @Mock
    private KnowledgePointMapper knowledgePointMapper;

    @Mock
    private KnowledgePointRelationMapper relationMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private KnowledgeGraphServiceImpl knowledgeGraphService;

    @Test
    void saveGraphFromJson_shouldParseAndSaveNodesAndEdgesCorrectly() throws Exception {
        // 1. 准备
        ReflectionTestUtils.setField(knowledgeGraphService, "objectMapper", objectMapper);
        Long courseId = 1L;
        String llmJsonResponse = """
                {
                  "nodes": [
                    {"name": "Spring Boot", "description": "核心框架"},
                    {"name": "Auto-Configuration", "description": "自动配置"}
                  ],
                  "edges": [
                    {"source": "Auto-Configuration", "target": "Spring Boot", "relation": "PART_OF"}
                  ]
                }
                """;

        // 使用ArgumentCaptor捕获传递给mapper的参数
        ArgumentCaptor<com.example.aicourse.entity.KnowledgePoint> kpCaptor = ArgumentCaptor.forClass(com.example.aicourse.entity.KnowledgePoint.class);
        ArgumentCaptor<com.example.aicourse.entity.KnowledgePointRelation> relCaptor = ArgumentCaptor.forClass(com.example.aicourse.entity.KnowledgePointRelation.class);

        // 模拟mapper.insert的行为，以便我们可以捕获ID
        when(knowledgePointMapper.insert(kpCaptor.capture())).thenAnswer(invocation -> {
            com.example.aicourse.entity.KnowledgePoint kp = invocation.getArgument(0);
            // 模拟数据库生成ID
            if ("Spring Boot".equals(kp.getName())) {
                ReflectionTestUtils.setField(kp, "id", 101L);
            } else {
                ReflectionTestUtils.setField(kp, "id", 102L);
            }
            return 1;
        });

        // 2. 执行
        knowledgeGraphService.saveGraphFromJson(courseId, llmJsonResponse);

        // 3. 断言
        // 验证KnowledgePointMapper.insert被调用了两次
        verify(knowledgePointMapper, times(2)).insert(any());
        // 验证KnowledgePointRelationMapper.insert被调用了一次
        verify(relationMapper, times(1)).insert(relCaptor.capture());

        // 验证捕获到的节点数据
        assertEquals(2, kpCaptor.getAllValues().size());
        assertEquals("Spring Boot", kpCaptor.getAllValues().get(0).getName());
        assertEquals("Auto-Configuration", kpCaptor.getAllValues().get(1).getName());

        // 验证捕获到的关系数据
        com.example.aicourse.entity.KnowledgePointRelation capturedRelation = relCaptor.getValue();
        assertEquals(102L, capturedRelation.getSourceId()); // "Auto-Configuration"的ID
        assertEquals(101L, capturedRelation.getTargetId());  // "Spring Boot"的ID
        assertEquals("PART_OF", capturedRelation.getRelationType());
    }
}