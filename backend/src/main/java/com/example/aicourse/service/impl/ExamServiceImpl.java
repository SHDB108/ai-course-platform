package com.example.aicourse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.aicourse.entity.Exam;
import com.example.aicourse.entity.ExamSubmission;
import com.example.aicourse.entity.Course;
import com.example.aicourse.entity.User;
import com.example.aicourse.repository.ExamMapper;
import com.example.aicourse.repository.ExamSubmissionMapper;
import com.example.aicourse.repository.CourseMapper;
import com.example.aicourse.repository.UserMapper;
import com.example.aicourse.service.ExamService;
import com.example.aicourse.vo.exam.ExamVO;
import com.example.aicourse.vo.exam.ExamStatisticsVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class ExamServiceImpl implements ExamService {

    @Autowired
    private ExamMapper examMapper;
    
    @Autowired
    private ExamSubmissionMapper examSubmissionMapper;
    
    @Autowired
    private CourseMapper courseMapper;
    
    @Autowired
    private UserMapper userMapper;

    @Override
    public IPage<ExamVO> getStudentExams(Long studentId, Integer pageNum, Integer pageSize,
                                         String status, String examType, Long courseId) {
        Page<Exam> page = new Page<>(pageNum, pageSize);
        
        IPage<Exam> examPage = examMapper.selectStudentExams(page, studentId, status, examType, courseId);
        
        // 转换为VO
        IPage<ExamVO> voPage = new Page<>();
        BeanUtils.copyProperties(examPage, voPage);
        
        List<ExamVO> examVOs = new ArrayList<>();
        for (Exam exam : examPage.getRecords()) {
            ExamVO vo = convertToVO(exam, studentId);
            examVOs.add(vo);
        }
        voPage.setRecords(examVOs);
        
        return voPage;
    }

    @Override
    public ExamVO getExamDetail(Long examId) {
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            throw new RuntimeException("考试不存在");
        }
        return convertToVO(exam, null);
    }

    @Override
    public ExamStatisticsVO getStudentExamStatistics(Long studentId) {
        // 获取考试基本统计
        Map<String, Object> examStats = examMapper.getStudentExamStatistics(studentId);
        Map<String, Object> scoreStats = examMapper.getStudentExamScoreStatistics(studentId);
        
        ExamStatisticsVO statistics = new ExamStatisticsVO();
        
        // 处理基本统计数据
        if (examStats != null) {
            statistics.setTotalExams(((Number) examStats.getOrDefault("total_exams", 0)).intValue());
            statistics.setCompletedExams(((Number) examStats.getOrDefault("completed_exams", 0)).intValue());
            statistics.setPendingExams(((Number) examStats.getOrDefault("pending_exams", 0)).intValue());
            statistics.setExpiredExams(((Number) examStats.getOrDefault("expired_exams", 0)).intValue());
        }
        
        // 处理成绩统计数据
        if (scoreStats != null) {
            statistics.setAverageScore(((Number) scoreStats.getOrDefault("average_score", 0)).doubleValue());
            statistics.setTotalScore(((Number) scoreStats.getOrDefault("total_score", 0)).intValue());
            statistics.setPassedExams(((Number) scoreStats.getOrDefault("passed_exams", 0)).intValue());
            statistics.setFailedExams(((Number) scoreStats.getOrDefault("failed_exams", 0)).intValue());
        }
        
        return statistics;
    }

    @Override
    public Map<String, Object> startExam(Long examId, Long studentId) {
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            throw new RuntimeException("考试不存在");
        }
        
        // 检查考试状态和时间
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(exam.getStartTime())) {
            throw new RuntimeException("考试尚未开始");
        }
        if (now.isAfter(exam.getEndTime())) {
            throw new RuntimeException("考试已结束");
        }
        
        // 检查是否已有提交记录
        QueryWrapper<ExamSubmission> wrapper = new QueryWrapper<>();
        wrapper.eq("exam_id", examId)
               .eq("student_id", studentId)
               .eq("status", "IN_PROGRESS");
        
        ExamSubmission existingSubmission = examSubmissionMapper.selectOne(wrapper);
        
        if (existingSubmission != null) {
            // 返回现有提交ID
            Map<String, Object> result = new HashMap<>();
            result.put("submissionId", existingSubmission.getId());
            return result;
        }
        
        // 检查尝试次数
        QueryWrapper<ExamSubmission> countWrapper = new QueryWrapper<>();
        countWrapper.eq("exam_id", examId).eq("student_id", studentId);
        Long attemptCount = examSubmissionMapper.selectCount(countWrapper);
        
        if (attemptCount >= exam.getMaxAttempts()) {
            throw new RuntimeException("已达到最大尝试次数");
        }
        
        // 创建新的提交记录
        ExamSubmission submission = new ExamSubmission();
        submission.setExamId(examId);
        submission.setStudentId(studentId);
        submission.setStatus("IN_PROGRESS");
        submission.setAttemptNumber(attemptCount.intValue() + 1);
        submission.setCreatedAt(now);
        
        examSubmissionMapper.insert(submission);
        
        Map<String, Object> result = new HashMap<>();
        result.put("submissionId", submission.getId());
        return result;
    }

    @Override
    public Map<String, Object> getExamRemainingTime(Long examId, Long submissionId) {
        Exam exam = examMapper.selectById(examId);
        ExamSubmission submission = examSubmissionMapper.selectById(submissionId);
        
        if (exam == null || submission == null) {
            throw new RuntimeException("考试或提交记录不存在");
        }
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime examEndTime = exam.getEndTime();
        LocalDateTime submissionStartTime = submission.getCreatedAt();
        LocalDateTime durationEndTime = submissionStartTime.plusMinutes(exam.getDuration());
        
        // 取考试结束时间和作答时长中的较早者
        LocalDateTime effectiveEndTime = examEndTime.isBefore(durationEndTime) ? examEndTime : durationEndTime;
        
        long remainingSeconds = ChronoUnit.SECONDS.between(now, effectiveEndTime);
        remainingSeconds = Math.max(0, remainingSeconds); // 确保不为负数
        
        Map<String, Object> result = new HashMap<>();
        result.put("remainingTime", remainingSeconds);
        return result;
    }

    private ExamVO convertToVO(Exam exam, Long studentId) {
        ExamVO vo = new ExamVO();
        BeanUtils.copyProperties(exam, vo);
        
        // 获取课程信息
        Course course = courseMapper.selectById(exam.getCourseId());
        if (course != null) {
            vo.setCourseName(course.getCourseName());
            
            // 获取教师信息
            User teacher = userMapper.selectById(course.getTeacherId());
            if (teacher != null) {
                vo.setTeacherName(teacher.getUsername());
            }
        }
        
        // 如果提供了学生ID，获取学生相关的考试信息
        if (studentId != null) {
            QueryWrapper<ExamSubmission> wrapper = new QueryWrapper<>();
            wrapper.eq("exam_id", exam.getId())
                   .eq("student_id", studentId)
                   .orderByDesc("created_at");
            
            List<ExamSubmission> submissions = examSubmissionMapper.selectList(wrapper);
            vo.setCurrentAttempts(submissions.size());
            
            if (!submissions.isEmpty()) {
                // 获取最后一次提交的成绩
                ExamSubmission lastSubmission = submissions.get(0);
                if (lastSubmission.getScore() != null) {
                    vo.setLastAttemptScore(lastSubmission.getScore());
                }
                
                // 获取最好成绩
                Integer bestScore = submissions.stream()
                    .filter(s -> s.getScore() != null)
                    .mapToInt(ExamSubmission::getScore)
                    .max()
                    .orElse(0);
                vo.setBestScore(bestScore);
                
                // 如果有进行中的考试，计算剩余时间
                ExamSubmission inProgressSubmission = submissions.stream()
                    .filter(s -> "IN_PROGRESS".equals(s.getStatus()))
                    .findFirst()
                    .orElse(null);
                
                if (inProgressSubmission != null) {
                    Map<String, Object> timeResult = getExamRemainingTime(exam.getId(), inProgressSubmission.getId());
                    vo.setRemainingTime(((Number) timeResult.get("remainingTime")).intValue());
                }
            }
        }
        
        return vo;
    }
}