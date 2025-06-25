package com.example.aicourse.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.aicourse.dto.quiz.QuizGradeDTO;
import com.example.aicourse.dto.quiz.QuizSubmissionDTO;
import com.example.aicourse.entity.*;
import com.example.aicourse.repository.*;
import com.example.aicourse.service.QuizSubmissionService;
import com.example.aicourse.vo.quiz.QuizSubmissionDetailVO;
import com.example.aicourse.vo.quiz.QuizSubmissionVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class QuizSubmissionServiceImpl implements QuizSubmissionService {

    @Autowired
    private QuizSubmissionMapper submissionMapper;
    @Autowired
    private QuizPaperMapper paperMapper;
    @Autowired
    private StudentMapper studentMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private QuizPaperQuestionMapper paperQuestionMapper;
    @Autowired
    private QuestionOptionMapper optionMapper;
    @Autowired
    private ObjectMapper objectMapper;

    // 使用 @Lazy 注解注入服务自身的代理，以解决AOP自调用问题
    private final QuizSubmissionService self;

    @Autowired
    public QuizSubmissionServiceImpl(@Lazy QuizSubmissionService self) {
        this.self = self;
    }


    @Override
    @Transactional
    public Long submit(QuizSubmissionDTO dto) {
        QuizSubmission submission = new QuizSubmission();
        BeanUtils.copyProperties(dto, submission);

        try {
            String answersJson = objectMapper.writeValueAsString(dto.getAnswers());
            submission.setAnswers(answersJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("答案序列化失败", e);
        }

        submission.setStatus("SUBMITTED"); // 状态：已提交

        submissionMapper.insert(submission);

        // 通过自身代理调用异步方法，触发自动批改
        self.gradeSubmission(submission.getId(), new QuizGradeDTO());

        return submission.getId();
    }

    @Override
    public List<QuizSubmissionVO> getStudentSubmissionsForPaper(Long studentId, Long paperId) {
        List<QuizSubmission> submissions = submissionMapper.selectList(
                Wrappers.<QuizSubmission>lambdaQuery()
                        .eq(QuizSubmission::getStudentId, studentId)
                        .eq(QuizSubmission::getPaperId, paperId)
        );

        return submissions.stream().map(s -> {
            QuizSubmissionVO vo = new QuizSubmissionVO();
            BeanUtils.copyProperties(s, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public QuizSubmissionDetailVO getSubmissionDetailsById(Long id) {
        QuizSubmission submission = submissionMapper.selectById(id);
        if (submission == null) return null;

        QuizPaper paper = paperMapper.selectById(submission.getPaperId());
        Student student = studentMapper.selectById(submission.getStudentId());

        QuizSubmissionDetailVO vo = new QuizSubmissionDetailVO();
        BeanUtils.copyProperties(submission, vo);

        if (paper != null) {
            vo.setPaperTitle(paper.getTitle());
        }
        if (student != null) {
            vo.setStudentName(student.getName());
        }

        try {
            Map<Long, String> answers = objectMapper.readValue(submission.getAnswers(), new TypeReference<>() {});
            vo.setAnswers(answers);
        } catch (JsonProcessingException e) {
            System.err.println("答案反序列化失败: " + e.getMessage());
        }

        return vo;
    }

    @Async // 将此方法标记为异步执行
    @Override
    @Transactional
    public void gradeSubmission(Long id, QuizGradeDTO dto) {
        // 在异步方法中，最好重新获取实体以确保事务边界和数据一致性
        QuizSubmission submission = submissionMapper.selectById(id);
        if (submission == null) {
            System.err.println("异步批改失败：提交记录不存在: " + id);
            return;
        }

        // 如果是异步触发的自动批改，dto.getScore() 通常为 null
        if (dto.getScore() != null) {
            submission.setScore(dto.getScore());
        } else {
            BigDecimal autoGradedScore = performAutoGrading(submission);
            submission.setScore(autoGradedScore);
        }

        submission.setStatus("GRADED");

        submissionMapper.updateById(submission);
        System.out.println("异步批改完成，提交ID: " + id + ", 分数: " + submission.getScore());
    }

    /**
     * 执行自动批改的核心逻辑
     * @param submission 学生的提交记录
     * @return 自动计算出的分数
     */
    private BigDecimal performAutoGrading(QuizSubmission submission) {
        // ... (此方法内容与上一轮回复中提供的完全相同，此处省略以保持简洁)
        // 1. 获取学生答案
        Map<Long, String> studentAnswers;
        try {
            studentAnswers = objectMapper.readValue(submission.getAnswers(), new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("反序列化学生答案失败", e);
        }

        if(CollectionUtils.isEmpty(studentAnswers)){
            return BigDecimal.ZERO;
        }

        // 2. 预加载所有需要的数据
        List<QuizPaperQuestion> paperQuestions = paperQuestionMapper.selectList(
                Wrappers.<QuizPaperQuestion>lambdaQuery().eq(QuizPaperQuestion::getPaperId, submission.getPaperId())
        );
        if (CollectionUtils.isEmpty(paperQuestions)) {
            return BigDecimal.ZERO;
        }

        Set<Long> questionIds = paperQuestions.stream().map(QuizPaperQuestion::getQuestionId).collect(Collectors.toSet());

        Map<Long, Question> questionMap = questionMapper.selectBatchIds(questionIds).stream()
                .collect(Collectors.toMap(Question::getId, Function.identity()));

        Map<Long, List<QuestionOption>> optionsMap = optionMapper.selectList(
                Wrappers.<QuestionOption>lambdaQuery().in(QuestionOption::getQuestionId, questionIds)
        ).stream().collect(Collectors.groupingBy(QuestionOption::getQuestionId));

        // 3. 开始计分
        double totalScore = 0.0;

        for (QuizPaperQuestion pq : paperQuestions) {
            Long questionId = pq.getQuestionId();
            Question question = questionMap.get(questionId);
            String studentAnswerStr = studentAnswers.get(questionId);

            if (question == null || studentAnswerStr == null || studentAnswerStr.isBlank()) {
                continue;
            }

            String type = question.getType();
            if ("SC".equals(type)) {
                List<QuestionOption> options = optionsMap.get(questionId);
                if (options == null) continue;

                Long correctOptionId = options.stream()
                        .filter(QuestionOption::getIsCorrect)
                        .map(QuestionOption::getId)
                        .findFirst()
                        .orElse(null);

                if (correctOptionId != null && studentAnswerStr.equals(String.valueOf(correctOptionId))) {
                    totalScore += pq.getScore().doubleValue();
                }
            } else if ("MC".equals(type)) {
                List<QuestionOption> options = optionsMap.get(questionId);
                if (options == null) continue;

                Set<String> correctOptionIds = options.stream()
                        .filter(QuestionOption::getIsCorrect)
                        .map(o -> String.valueOf(o.getId()))
                        .collect(Collectors.toSet());

                Set<String> studentOptionIds = Arrays.stream(studentAnswerStr.split(","))
                        .collect(Collectors.toSet());

                if (correctOptionIds.equals(studentOptionIds)) {
                    totalScore += pq.getScore().doubleValue();
                }
            }
        }

        return BigDecimal.valueOf(totalScore);
    }
}