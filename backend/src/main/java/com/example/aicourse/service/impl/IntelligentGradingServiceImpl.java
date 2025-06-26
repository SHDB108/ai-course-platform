package com.example.aicourse.service.impl;

import com.example.aicourse.config.StorageProperties;
import com.example.aicourse.dto.task.IntelligentGradeRequestDTO;
import com.example.aicourse.entity.TaskSubmission;
import com.example.aicourse.repository.TaskSubmissionMapper;
import com.example.aicourse.service.IntelligentGradingService;
import com.example.aicourse.service.LlmService;
import com.example.aicourse.utils.TextExtractor;
import com.example.aicourse.vo.task.IntelligentGradeResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;

@Slf4j
@Service
public class IntelligentGradingServiceImpl implements IntelligentGradingService {

    private final TaskSubmissionMapper submissionMapper;
    private final StorageProperties storageProperties;
    private final LlmService llmService; // 【优化】依赖抽象的LlmService

    @Autowired
    public IntelligentGradingServiceImpl(TaskSubmissionMapper submissionMapper, StorageProperties storageProperties, LlmService llmService) {
        this.submissionMapper = submissionMapper;
        this.storageProperties = storageProperties;
        this.llmService = llmService;
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
            TaskSubmission submission = submissionMapper.selectById(submissionId);
            if (submission != null) {
                submission.setFeedback("AI批改时发生内部错误: " + e.getMessage());
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

        // 4. 【优化】调用LlmService并解析响应
        IntelligentGradeResultVO resultVO = llmService.generateJson(prompt, IntelligentGradeResultVO.class);

        // 5. 将结果保存回数据库
        submission.setScore(resultVO.getScore());
        submission.setFeedback(resultVO.getFeedback());
        submission.setGraderId(0L); // 0 代表AI批改
        submission.setGradeTime(java.time.LocalDateTime.now());
        submission.setStatus("GRADED");
        submissionMapper.updateById(submission);

        return resultVO;
    }

    @Override
    public IntelligentGradeResultVO gradeShortAnswer(String studentAnswer, String referenceAnswer) {
        // 【优化】将逻辑完全委托给LlmService
        return llmService.gradeShortAnswer(studentAnswer, referenceAnswer);
    }

    private String buildGradingPrompt(String reportText, String gradingRules) {
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
}