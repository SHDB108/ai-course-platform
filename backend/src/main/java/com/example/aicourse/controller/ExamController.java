package com.example.aicourse.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.aicourse.service.ExamService;
import com.example.aicourse.utils.Result;
import com.example.aicourse.utils.CurrentUserUtil;
import com.example.aicourse.vo.exam.ExamVO;
import com.example.aicourse.vo.exam.ExamStatisticsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class ExamController {

    @Autowired
    private ExamService examService;

    /**
     * 获取学生的考试列表
     */
    @GetMapping("/students/{studentId}/exams")
    public Result<IPage<ExamVO>> getStudentExams(
            @PathVariable Long studentId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String examType,
            @RequestParam(required = false) Long courseId) {
        
        IPage<ExamVO> result = examService.getStudentExams(studentId, pageNum, pageSize, status, examType, courseId);
        return Result.ok(result);
    }

    /**
     * 获取考试详情
     */
    @GetMapping("/exams/{examId}")
    public Result<ExamVO> getExamDetail(@PathVariable Long examId) {
        ExamVO exam = examService.getExamDetail(examId);
        return Result.ok(exam);
    }

    /**
     * 开始考试
     */
    @PostMapping("/exams/{examId}/start")
    public Result<Map<String, Object>> startExam(@PathVariable Long examId) {
        // 从当前登录用户获取学生ID
        Long studentId = CurrentUserUtil.getCurrentUser().getId();
        
        Map<String, Object> result = examService.startExam(examId, studentId);
        return Result.ok(result);
    }

    /**
     * 获取学生考试统计
     */
    @GetMapping("/students/{studentId}/exam-statistics")
    public Result<ExamStatisticsVO> getStudentExamStatistics(@PathVariable Long studentId) {
        ExamStatisticsVO statistics = examService.getStudentExamStatistics(studentId);
        return Result.ok(statistics);
    }

    /**
     * 获取考试剩余时间
     */
    @GetMapping("/exams/{examId}/remaining-time")
    public Result<Map<String, Object>> getExamRemainingTime(
            @PathVariable Long examId,
            @RequestParam Long submissionId) {
        
        Map<String, Object> result = examService.getExamRemainingTime(examId, submissionId);
        return Result.ok(result);
    }

    // 注意：以下接口在前端已定义但这里暂不实现，因为需要更复杂的考试提交逻辑
    // - 获取考试题目: GET /exams/{examId}/questions
    // - 提交考试答案: POST /exams/{examId}/submit  
    // - 保存考试进度: POST /exams/{examId}/save-progress
    // - 获取考试提交记录: GET /exams/{examId}/submissions
}