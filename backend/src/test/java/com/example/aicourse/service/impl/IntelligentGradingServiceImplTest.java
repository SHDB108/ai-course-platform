package com.example.aicourse.service.impl;

import com.example.aicourse.vo.task.IntelligentGradeResultVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq; // 导入 eq
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IntelligentGradingServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    // 注入一个真实的ObjectMapper实例用于测试JSON解析
    private final ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private IntelligentGradingServiceImpl intelligentGradingService;

    @BeforeEach
    void setUp() {
        // 使用Spring的工具类来注入在`@Value`中使用的配置属性
        ReflectionTestUtils.setField(intelligentGradingService, "difyApiUrl", "https://fake-dify-api.com/v1");
        ReflectionTestUtils.setField(intelligentGradingService, "difyModel", "dify-llm-model");
        // 将真实的ObjectMapper注入到Service中
        ReflectionTestUtils.setField(intelligentGradingService, "objectMapper", objectMapper);
    }

    @Test
    void gradeShortAnswer_shouldReturnCorrectScoreAndFeedback_whenLlmApiSucceeds() throws Exception {
        // 1. 准备 (Arrange)
        String studentAnswer = "Spring Boot是一个用于快速开发Java应用的框架。";
        String referenceAnswer = "Spring Boot是一个开源的、基于Spring的框架，用于创建独立的、生产级的应用程序。";

        // 模拟LLM返回的JSON响应
        String mockLlmJsonResponse = "{\"score\": \"85.5\", \"feedback\": \"回答基本正确，但可以更详细。\"}";
        String mockDifyResponse = "{\"response\": " + objectMapper.writeValueAsString(mockLlmJsonResponse) + "}";

        // 当RestTemplate的postForEntity方法被调用时，返回我们模拟的响应
        // 使用 eq(String.class) 来精确匹配，消除警告
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(mockDifyResponse, org.springframework.http.HttpStatus.OK));

        // 2. 执行 (Act)
        IntelligentGradeResultVO result = intelligentGradingService.gradeShortAnswer(studentAnswer, referenceAnswer);

        // 3. 断言 (Assert)
        assertNotNull(result);
        assertEquals(0, new BigDecimal("85.5").compareTo(result.getScore()));
        assertEquals("回答基本正确，但可以更详细。", result.getFeedback());
    }

    @Test
    void gradeShortAnswer_shouldReturnZeroScore_whenStudentAnswerIsBlank() {
        // 1. 准备
        String studentAnswer = "";
        String referenceAnswer = "任何参考答案";

        // 2. 执行
        IntelligentGradeResultVO result = intelligentGradingService.gradeShortAnswer(studentAnswer, referenceAnswer);

        // 3. 断言
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getScore());
        assertEquals("学生未作答。", result.getFeedback());
    }
}