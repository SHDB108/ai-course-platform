package com.example.aicourse.service;

import com.example.aicourse.dto.task.IntelligentGradeRequestDTO;
import com.example.aicourse.vo.task.IntelligentGradeResultVO;

/**
 * 智能批改服务接口
 * 负责调用大语言模型对学生的提交内容进行分析和评分。
 */
public interface IntelligentGradingService {

    /**
     * 对指定的任务提交进行智能批改 (API 6.6 的后台实现)
     * <p>
     * 此方法应为异步执行，以避免长时间阻塞API请求。
     * 批改完成后，结果将被更新回对应的TaskSubmission记录中。
     *
     * @param submissionId 需要被批改的任务提交记录ID (t_task_submission.id)
     * @param request 包含批改规则和LLM模型配置等信息的请求 DTO
     * @see com.example.aicourse.dto.task.IntelligentGradeRequestDTO
     */
    void triggerIntelligentGrade(Long submissionId, IntelligentGradeRequestDTO request);

    /**
     * 同步执行智能批改并立即返回结果 (用于即时演示或调试)
     * <p>
     * 此方法会阻塞线程，直到LLM返回结果。
     *
     * @param submissionId 需要被批改的任务提交记录ID
     * @param request 包含批改规则的请求 DTO
     * @return 包含分数、评语和建议的批改结果VO
     * @see com.example.aicourse.vo.task.IntelligentGradeResultVO
     */
    IntelligentGradeResultVO gradeSubmissionSync(Long submissionId, IntelligentGradeRequestDTO request);

    /**
     * 对简答题等文本内容进行智能批改
     * <p>
     * 此方法会根据参考答案，对学生提交的文本进行打分。
     *
     * @param studentAnswer 学生的回答文本
     * @param referenceAnswer 题目的参考答案
     * @return 包含分数(0-100)和评语的批改结果VO
     */
    IntelligentGradeResultVO gradeShortAnswer(String studentAnswer, String referenceAnswer);
}