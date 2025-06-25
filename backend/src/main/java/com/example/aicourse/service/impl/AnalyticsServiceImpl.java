package com.example.aicourse.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.aicourse.entity.*;
import com.example.aicourse.repository.*;
import com.example.aicourse.service.AnalyticsService;
import com.example.aicourse.vo.analytics.CourseStudentScoreVO;
import com.example.aicourse.vo.analytics.KnowledgePointPerformanceVO;
import com.example.aicourse.vo.analytics.StudentCoursePerformanceVO;
import com.example.aicourse.vo.analytics.TaskCompletionSummaryVO;
import com.example.aicourse.vo.course.TrendPointVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private final TaskSubmissionMapper tsMapper;
    private final QuizSubmissionMapper qsMapper;
    private final TaskMapper taskMapper;
    private final StudentMapper studentMapper;
    private final QuizPaperMapper paperMapper;
    private final CourseMapper courseMapper;
    private final KnowledgePointMapper knowledgePointMapper;
    private final QuestionMapper questionMapper;
    private final QuestionOptionMapper optionMapper;
    private final ObjectMapper objectMapper;
    private final CourseEnrollmentMapper courseEnrollmentMapper;

    @Autowired
    public AnalyticsServiceImpl(TaskSubmissionMapper tsMapper, QuizSubmissionMapper qsMapper, TaskMapper taskMapper, StudentMapper studentMapper, QuizPaperMapper paperMapper, CourseMapper courseMapper, KnowledgePointMapper knowledgePointMapper, QuestionMapper questionMapper, QuestionOptionMapper optionMapper, ObjectMapper objectMapper, CourseEnrollmentMapper courseEnrollmentMapper) {
        this.tsMapper = tsMapper;
        this.qsMapper = qsMapper;
        this.taskMapper = taskMapper;
        this.studentMapper = studentMapper;
        this.paperMapper = paperMapper;
        this.courseMapper = courseMapper;
        this.knowledgePointMapper = knowledgePointMapper;
        this.questionMapper = questionMapper;
        this.optionMapper = optionMapper;
        this.objectMapper = objectMapper;
        this.courseEnrollmentMapper = courseEnrollmentMapper;
    }


    @Override
    public List<TrendPointVO> getTrend(Long courseId, String period) {
        LocalDateTime startDate = null;
        if (period != null) {
            // 可以根据需要添加更多 case，如 "QUARTER"
            startDate = switch (period.toUpperCase()) {
                case "WEEK" -> LocalDateTime.now().minusWeeks(1);
                case "MONTH" -> LocalDateTime.now().minusMonths(1);
                case "YEAR" -> LocalDateTime.now().minusYears(1);
                default -> startDate;
            };
        }
        return tsMapper.selectByCourse(courseId, startDate);
    }

    @Override
    public ResponseEntity<Resource> exportCsv(Long courseId, String type) {
        List<String> lines;
        String csvHeader;

        // 根据类型决定导出的数据源
        if ("TASK".equalsIgnoreCase(type)) {
            csvHeader = "studentId,task_score\n";
            lines = tsMapper.selectTaskRowsForExport(courseId).stream()
                    .map(r -> r.getStudentId() + "," + r.getScore())
                    .collect(Collectors.toList());
        } else { // 默认为 "QUIZ"
            csvHeader = "studentId,quiz_score\n";
            lines = qsMapper.selectRows(courseId).stream()
                    .map(r -> r.getStudentId() + "," + r.getScore())
                    .collect(Collectors.toList());
        }

        String csvContent = csvHeader + String.join("\n", lines);
        ByteArrayResource body = new ByteArrayResource(csvContent.getBytes(StandardCharsets.UTF_8));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + type.toLowerCase() + "_scores.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(body);
    }

    @Override
    public Page<CourseStudentScoreVO> getCourseStudentScores(Long courseId, Page<CourseStudentScoreVO> page) {
        // 1. 分页查询已选修该课程的学生
        Page<Student> studentPage = studentMapper.selectStudentsByCourse(new Page<>(page.getCurrent(), page.getSize()), courseId);
        List<Student> studentsOnPage = studentPage.getRecords();

        if (CollectionUtils.isEmpty(studentsOnPage)) {
            return new Page<>(page.getCurrent(), page.getSize(), 0);
        }

        List<Long> studentIds = studentsOnPage.stream().map(Student::getId).collect(Collectors.toList());

        // 2. 批量获取本课程所有任务和测验的ID (逻辑无变化)
        List<Long> taskIds = taskMapper.selectList(Wrappers.<Task>lambdaQuery().eq(Task::getCourseId, courseId))
                .stream().map(Task::getId).toList();
        List<Long> paperIds = paperMapper.selectList(Wrappers.<QuizPaper>lambdaQuery().eq(QuizPaper::getCourseId, courseId))
                .stream().map(QuizPaper::getId).toList();

        // 3. 批量获取分页学生在这些任务和测验中的所有提交记录 (逻辑无变化)
        Map<Long, List<TaskSubmission>> taskSubmissionsMap = Collections.emptyMap();
        if (!CollectionUtils.isEmpty(taskIds)) {
            taskSubmissionsMap = tsMapper.selectList(Wrappers.<TaskSubmission>lambdaQuery()
                    .in(TaskSubmission::getTaskId, taskIds)
                    .in(TaskSubmission::getStudentId, studentIds)
            ).stream().collect(Collectors.groupingBy(TaskSubmission::getStudentId));
        }

        Map<Long, List<QuizSubmission>> quizSubmissionsMap = Collections.emptyMap();
        if (!CollectionUtils.isEmpty(paperIds)) {
            quizSubmissionsMap = qsMapper.selectList(Wrappers.<QuizSubmission>lambdaQuery()
                    .in(QuizSubmission::getPaperId, paperIds)
                    .in(QuizSubmission::getStudentId, studentIds)
            ).stream().collect(Collectors.groupingBy(QuizSubmission::getStudentId));
        }

        // 4. 组装VO
        final Map<Long, List<TaskSubmission>> finalTaskSubmissionsMap = taskSubmissionsMap;
        final Map<Long, List<QuizSubmission>> finalQuizSubmissionsMap = quizSubmissionsMap;

        List<CourseStudentScoreVO> voRecords = studentsOnPage.stream().map(student -> {
            CourseStudentScoreVO vo = new CourseStudentScoreVO();
            vo.setStudentId(student.getId());
            vo.setStudentName(student.getName());
            vo.setStuNo(student.getStuNo());

            // 提取任务成绩
            List<TaskSubmission> studentTaskSubs = finalTaskSubmissionsMap.getOrDefault(student.getId(), Collections.emptyList());
            Map<Long, BigDecimal> taskScores = studentTaskSubs.stream()
                    .filter(sub -> sub.getScore() != null)
                    .collect(Collectors.toMap(TaskSubmission::getTaskId, TaskSubmission::getScore));
            vo.setTaskScores(taskScores);

            // 提取测验成绩
            List<QuizSubmission> studentQuizSubs = finalQuizSubmissionsMap.getOrDefault(student.getId(), Collections.emptyList());
            Map<Long, BigDecimal> quizScores = studentQuizSubs.stream()
                    .filter(sub -> sub.getScore() != null)
                    .collect(Collectors.toMap(QuizSubmission::getPaperId, QuizSubmission::getScore));
            vo.setQuizScores(quizScores);

            // 5. 计算综合分 (简单平均)
            List<BigDecimal> allScores = Stream.concat(taskScores.values().stream(), quizScores.values().stream()).toList();
            if (!allScores.isEmpty()) {
                BigDecimal sum = allScores.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal average = sum.divide(BigDecimal.valueOf(allScores.size()), 2, RoundingMode.HALF_UP);
                vo.setOverallScore(average);
            } else {
                vo.setOverallScore(BigDecimal.ZERO);
            }

            return vo;
        }).collect(Collectors.toList());

        // 6. 返回分页结果
        Page<CourseStudentScoreVO> resultPage = new Page<>(studentPage.getCurrent(), studentPage.getSize(), studentPage.getTotal());
        resultPage.setRecords(voRecords);
        return resultPage;
    }

    @Override
    public List<TaskCompletionSummaryVO> getTaskCompletionSummary(Long courseId) {
        // 1. 调用自定义SQL查询，获取基础的统计数据 (逻辑无变化)
        List<TaskCompletionSummaryVO> summaryList = taskMapper.selectTaskCompletionSummary(courseId);

        // 2. 获取课程总人数，用于计算比率
        long totalStudents = courseEnrollmentMapper.selectCount(
                Wrappers.<CourseEnrollment>lambdaQuery().eq(CourseEnrollment::getCourseId, courseId)
        );

        if (totalStudents == 0) {
            // 如果没有学生，直接返回空列表，避免除零错误
            return Collections.emptyList();
        }

        // 3. 遍历列表，计算并填充完成率和准时率 (逻辑无变化)
        for (TaskCompletionSummaryVO summary : summaryList) {
            summary.setTotalStudents((int) totalStudents);

            int submittedCount = summary.getSubmittedCount();
            int onTimeCount = summary.getOnTimeCount();

            // 计算完成率
            double completionRate = (double) submittedCount / totalStudents;
            summary.setCompletionRate(completionRate);

            // 计算准时率（分母为已提交人数）
            if (submittedCount > 0) {
                double onTimeRate = (double) onTimeCount / submittedCount;
                summary.setOnTimeRate(onTimeRate);
            } else {
                summary.setOnTimeRate(0.0);
            }
        }

        return summaryList;
    }

    @Override
    public StudentCoursePerformanceVO getStudentPerformanceOverview(Long studentId, Long courseId) {
        StudentCoursePerformanceVO overviewVO = new StudentCoursePerformanceVO();

        // 1. 获取基本信息
        Student student = studentMapper.selectById(studentId);
        Course course = courseMapper.selectById(courseId);
        if (student == null || course == null) {
            // 如果学生或课程不存在，可以返回null或抛出异常
            return null;
        }
        overviewVO.setStudentId(studentId);
        overviewVO.setStudentName(student.getName());
        overviewVO.setCourseId(courseId);
        overviewVO.setCourseName(course.getCourseName());

        // 2. 获取个人成绩趋势
        List<TrendPointVO> scoreTrend = tsMapper.selectScoreTrendForStudent(courseId, studentId);
        overviewVO.setScoreTrend(scoreTrend);

        // 3. 获取个人任务完成概览 (复用已实现的 getTaskCompletionSummary)
        // 注意：这里的实现是获取整个课程的统计，然后可以由前端过滤出与当前学生相关的数据
        // 更精确的做法是编写一个只针对该学生的任务完成概览查询
        List<TaskCompletionSummaryVO> taskSummaries = this.getTaskCompletionSummary(courseId);
        overviewVO.setTaskCompletionSummary(taskSummaries);

        // 4. 计算综合分 (复用 getCourseStudentScores 的部分逻辑)
        // 批量获取本课程所有任务和测验的ID
        List<Long> taskIds = taskMapper.selectList(Wrappers.<Task>lambdaQuery().eq(Task::getCourseId, courseId))
                .stream().map(Task::getId).toList();
        List<Long> paperIds = paperMapper.selectList(Wrappers.<QuizPaper>lambdaQuery().eq(QuizPaper::getCourseId, courseId))
                .stream().map(QuizPaper::getId).toList();

        // 获取该学生的所有提交成绩
        Stream<BigDecimal> taskScoresStream = Stream.empty();
        if(!taskIds.isEmpty()){
            taskScoresStream = tsMapper.selectList(Wrappers.<TaskSubmission>lambdaQuery()
                            .in(TaskSubmission::getTaskId, taskIds)
                            .eq(TaskSubmission::getStudentId, studentId)
                            .isNotNull(TaskSubmission::getScore))
                    .stream()
                    .map(TaskSubmission::getScore);
        }

        Stream<BigDecimal> quizScoresStream = Stream.empty();
        if(!paperIds.isEmpty()){
            quizScoresStream = qsMapper.selectList(Wrappers.<QuizSubmission>lambdaQuery()
                            .in(QuizSubmission::getPaperId, paperIds)
                            .eq(QuizSubmission::getStudentId, studentId)
                            .isNotNull(QuizSubmission::getScore))
                    .stream()
                    .map(QuizSubmission::getScore);
        }

        List<BigDecimal> allScores = Stream.concat(taskScoresStream, quizScoresStream).toList();
        if (!allScores.isEmpty()) {
            BigDecimal sum = allScores.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal average = sum.divide(BigDecimal.valueOf(allScores.size()), 2, RoundingMode.HALF_UP);
            overviewVO.setOverallCourseScore(average);
        } else {
            overviewVO.setOverallCourseScore(BigDecimal.ZERO);
        }

        return overviewVO;
    }

    @Override
    public List<KnowledgePointPerformanceVO> getKnowledgePointPerformance(Long courseId, Long studentId) {
        // Step 1: Pre-load all necessary data
        // 1.1 获取课程下的所有知识点
        List<KnowledgePoint> knowledgePoints = knowledgePointMapper.selectList(
                Wrappers.<KnowledgePoint>lambdaQuery().eq(KnowledgePoint::getCourseId, courseId)
        );
        if (CollectionUtils.isEmpty(knowledgePoints)) {
            return Collections.emptyList();
        }
        Map<String, Long> kpNameToIdMap = knowledgePoints.stream()
                .collect(Collectors.toMap(KnowledgePoint::getName, KnowledgePoint::getId, (a, b) -> a));
        Map<Long, KnowledgePoint> kpIdToEntityMap = knowledgePoints.stream()
                .collect(Collectors.toMap(KnowledgePoint::getId, Function.identity()));

        // 1.2 获取课程下的所有问题及其选项
        List<Question> questions = questionMapper.selectList(
                Wrappers.<Question>lambdaQuery().eq(Question::getCourseId, courseId)
        );
        if (CollectionUtils.isEmpty(questions)) {
            return Collections.emptyList();
        }
        Map<Long, Question> questionMap = questions.stream()
                .collect(Collectors.toMap(Question::getId, Function.identity()));

        List<Long> questionIds = questions.stream().map(Question::getId).toList();
        Map<Long, List<QuestionOption>> optionsMap = optionMapper.selectList(
                Wrappers.<QuestionOption>lambdaQuery().in(QuestionOption::getQuestionId, questionIds)
        ).stream().collect(Collectors.groupingBy(QuestionOption::getQuestionId));

        // 1.3 获取所有相关的测验提交
        List<Long> paperIds = paperMapper.selectList(
                Wrappers.<QuizPaper>lambdaQuery().eq(QuizPaper::getCourseId, courseId)
        ).stream().map(QuizPaper::getId).toList();

        if (CollectionUtils.isEmpty(paperIds)) {
            return Collections.emptyList();
        }

        var submissionQuery = Wrappers.<QuizSubmission>lambdaQuery().in(QuizSubmission::getPaperId, paperIds);
        if (studentId != null) {
            submissionQuery.eq(QuizSubmission::getStudentId, studentId);
        }
        List<QuizSubmission> submissions = qsMapper.selectList(submissionQuery);

        // Step 2: Process submissions to gather correctness statistics for each knowledge point
        // 统计每个知识点的 (总作答数, 答对数)
        Map<Long, CorrectnessStats> kpStats = new HashMap<>();

        for (QuizSubmission submission : submissions) {
            Map<Long, String> studentAnswers;
            try {
                studentAnswers = objectMapper.readValue(submission.getAnswers(), new TypeReference<>() {});
            } catch (JsonProcessingException e) {
                continue; // or log error
            }

            for (Map.Entry<Long, String> answerEntry : studentAnswers.entrySet()) {
                Long qId = answerEntry.getKey();
                String studentAnswerStr = answerEntry.getValue();
                Question question = questionMap.get(qId);

                if (question == null || studentAnswerStr == null || studentAnswerStr.isBlank()) continue;

                boolean isCorrect = isAnswerCorrect(question, studentAnswerStr, optionsMap.get(qId));

                // 将本次作答结果计入相关的所有知识点
                String[] kpNames = question.getKnowledge().split("[,，]");
                for (String kpName : kpNames) {
                    Long kpId = kpNameToIdMap.get(kpName.trim());
                    if (kpId != null) {
                        CorrectnessStats stats = kpStats.computeIfAbsent(kpId, k -> new CorrectnessStats());
                        stats.incrementTotal();
                        if (isCorrect) {
                            stats.incrementCorrect();
                        }
                    }
                }
            }
        }

        // Step 3: Calculate final rates and create VOs
        return kpStats.entrySet().stream().map(entry -> {
            Long kpId = entry.getKey();
            CorrectnessStats stats = entry.getValue();
            KnowledgePoint kp = kpIdToEntityMap.get(kpId);

            KnowledgePointPerformanceVO vo = new KnowledgePointPerformanceVO();
            vo.setKnowledgePointId(kpId);
            vo.setKnowledgePointName(kp.getName());

            double rate = (stats.getTotal() == 0) ? 0 : ((double) stats.getCorrect() / stats.getTotal()) * 100;
            vo.setAverageScore(BigDecimal.valueOf(rate).setScale(2, RoundingMode.HALF_UP));
            vo.setMasteryLevel(determineMasteryLevel(rate));

            return vo;
        }).toList();
    }

    /** Helper class for statistics counting */
    @Getter
    private static class CorrectnessStats {
        private int total = 0;
        private int correct = 0;
        public void incrementTotal() { total++; }
        public void incrementCorrect() { correct++; }
    }

    /** Helper method to check if an answer is correct */
    private boolean isAnswerCorrect(Question question, String studentAnswerStr, List<QuestionOption> options) {
        if (CollectionUtils.isEmpty(options)) return false;

        String type = question.getType();
        if ("SC".equals(type)) {
            Long correctOptionId = options.stream().filter(QuestionOption::getIsCorrect).map(QuestionOption::getId).findFirst().orElse(null);
            return correctOptionId != null && studentAnswerStr.equals(String.valueOf(correctOptionId));
        } else if ("MC".equals(type)) {
            Set<String> correctIds = options.stream().filter(QuestionOption::getIsCorrect).map(o -> String.valueOf(o.getId())).collect(Collectors.toSet());
            Set<String> studentIds = Arrays.stream(studentAnswerStr.split("[,，]")).map(String::trim).collect(Collectors.toSet());
            return correctIds.equals(studentIds);
        } else if ("TF".equals(type)) {
            // 假设 "T" 或 "true" 为正确
            return studentAnswerStr.equalsIgnoreCase(question.getAnswer());
        }
        return false;
    }

    /** Helper method to determine mastery level */
    private String determineMasteryLevel(double rate) {
        if (rate >= 90) return "优秀";
        if (rate >= 75) return "良好";
        if (rate >= 60) return "及格";
        return "待加强";
    }
}