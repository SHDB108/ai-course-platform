package com.example.aicourse.service.impl;

import com.example.aicourse.config.StorageProperties;
import com.example.aicourse.dto.task.IntelligentGradeRequestDTO;
import com.example.aicourse.entity.TaskSubmission;
import com.example.aicourse.repository.TaskSubmissionMapper;
import com.example.aicourse.service.IntelligentGradingService;
import com.example.aicourse.utils.TextExtractor;
import com.example.aicourse.vo.task.IntelligentGradeResultVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class IntelligentGradingServiceImpl implements IntelligentGradingService {

    private final TaskSubmissionMapper submissionMapper;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final StorageProperties storageProperties;

    @Value("${llm.dify.api-url}")
    private String difyApiUrl;

    @Value("${llm.dify.model}")
    private String difyModel;

    @Autowired
    public IntelligentGradingServiceImpl(TaskSubmissionMapper submissionMapper, RestTemplate restTemplate, ObjectMapper objectMapper, StorageProperties storageProperties) {
        this.submissionMapper = submissionMapper;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.storageProperties = storageProperties;
    }

    @Async
    @Override
    @Transactional
    public void triggerIntelligentGrade(Long submissionId, IntelligentGradeRequestDTO request) {
        log.info("开始对提交ID: {} 进行异步智能批改...", submissionId);
        try {
            IntelligentGradeResultVO resultVO = gradeSubmissionSync(submissionId, request);
            log.info("异步智能批改完成，提交ID: {}, 分数: {}", submissionId, resultVO.getScore());
        } catch (Exception e) {
            log.error("异步智能批改失败，提交ID: {}", submissionId, e);
            // 异常处理：可以更新任务状态为“批改失败”等
            TaskSubmission submission = submissionMapper.selectById(submissionId);
            if (submission != null) {
                submission.setFeedback("AI批改时发生内部错误：" + e.getMessage());
                submission.setStatus("Grading Failed");
                submissionMapper.updateById(submission);
            }
        }
    }

    @Override
    @Transactional
    public IntelligentGradeResultVO gradeSubmissionSync(Long submissionId, IntelligentGradeRequestDTO request) {
        // 1. 获取提交记录
        TaskSubmission submission = submissionMapper.selectById(submissionId);
        if (submission == null || submission.getAnswerPath() == null || submission.getAnswerPath().isBlank()) {
            throw new IllegalArgumentException("未找到提交记录或提交的不是文件，无法进行智能批改。");
        }

        // 2. 提取学生报告的文本内容
        Path filePath = storageProperties.getLocalPath().resolve(submission.getAnswerPath());
        String studentReportText = TextExtractor.extract(filePath);
        if (studentReportText.isBlank()) {
            throw new RuntimeException("无法从报告文件 " + filePath + " 中提取文本内容。");
        }

        // 3. 构建 Prompt
        String prompt = buildGradingPrompt(studentReportText, request.getGradingRules());

        // 4. 调用 LLM
        String llmResponseJson = callLlmApi(prompt);

        // 5. 解析 LLM 的响应
        IntelligentGradeResultVO resultVO = parseLlmResponse(llmResponseJson);

        // 6. 将结果保存回数据库
        submission.setScore(resultVO.getScore());
        submission.setFeedback(resultVO.getFeedback()); // 将AI的综合评语作为反馈
        // 假设graderId为0代表AI批改
        submission.setGraderId(0L);
        submission.setGradeTime(java.time.LocalDateTime.now());
        submission.setStatus("GRADED"); // 更新状态为“已批改”
        submissionMapper.updateById(submission);

        return resultVO;
    }

    @Override
    public IntelligentGradeResultVO gradeShortAnswer(String studentAnswer, String referenceAnswer) {
        if(studentAnswer == null || studentAnswer.isBlank()){
            IntelligentGradeResultVO resultVO = new IntelligentGradeResultVO();
            resultVO.setScore(BigDecimal.ZERO);
            resultVO.setFeedback("学生未作答。");
            return resultVO;
        }
        // 1. 构建新的Prompt
        String prompt = buildShortAnswerGradingPrompt(studentAnswer, referenceAnswer);
        // 2. 调用LLM
        String llmResponseJson = callLlmApi(prompt);
        // 3. 解析结果并返回
        return parseLlmResponse(llmResponseJson);
    }


    private String buildGradingPrompt(String reportText, String gradingRules) {
        // 提供一个默认的评分规则，如果请求中没有提供
        if (gradingRules == null || gradingRules.isBlank()) {
            gradingRules = """
            - 内容完整性 (40%)
            - 逻辑与结构 (30%)
            - 语言表达 (20%)
            - 格式规范 (10%)
            """;
        }
        return String.format("""
            你是一位严格而公正的大学教授，正在批改一份学生报告。请根据以下评分标准和学生提交的内容，给出你的评分和评语。

            # 评分标准:
            %s

            # 学生报告内容:
            ---
            %s
            ---

            # 要求:
            请严格按照以下JSON格式返回你的批改结果，不要添加任何解释性文字或代码块标记。你的整个输出必须是一个完整的、可以被直接解析的JSON对象。
            {
              "score": "你的总评分 (0-100的数字)",
              "feedback": "综合评语，以第二人称'你'开头，总结报告的优点和主要不足之处。",
              "suggestions": [
                "具体的改进建议1",
                "具体的改进建议2"
              ]
            }
            """, gradingRules, reportText);
    }

    private String buildShortAnswerGradingPrompt(String studentAnswer, String referenceAnswer) {
        return String.format("""
            你是一位严谨的AI助教，请根据参考答案，对学生的回答进行评分。

            # 参考答案:
            ---
            %s
            ---

            # 学生的回答:
            ---
            %s
            ---

            # 评分要求:
            请判断学生的回答是否覆盖了参考答案中的要点，并评估其准确性。
            严格按照以下JSON格式返回你的批改结果，score字段为0到100之间的数字，代表回答的得分率。
            {
              "score": "你的评分 (0-100)",
              "feedback": "对学生回答的简短评语"
            }
            """, referenceAnswer, studentAnswer);
    }

    private String callLlmApi(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("model", difyModel);
        body.put("prompt", prompt);
        body.put("stream", false);
        body.put("format", "json");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            log.info("向Dify发送请求: {}", difyApiUrl);
            ResponseEntity<String> response = restTemplate.postForEntity(difyApiUrl, entity, String.class);
            Map<String, Object> DifyResponse = objectMapper.readValue(response.getBody(), new TypeReference<>() {});
            return (String) DifyResponse.get("response");
        } catch (Exception e) {
            log.error("调用LLM服务失败", e);
            throw new RuntimeException("调用LLM服务失败: " + e.getMessage(), e);
        }
    }

    private IntelligentGradeResultVO parseLlmResponse(String llmResponseJson) {
        try {
            return objectMapper.readValue(llmResponseJson, IntelligentGradeResultVO.class);
        } catch (Exception e) {
            log.error("解析LLM响应失败: {}", llmResponseJson, e);
            throw new RuntimeException("解析LLM的JSON响应失败，原始响应: " + llmResponseJson, e);
        }
    }
}