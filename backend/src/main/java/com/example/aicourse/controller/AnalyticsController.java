package com.example.aicourse.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.aicourse.service.AnalyticsService;
import com.example.aicourse.utils.Result;
import com.example.aicourse.vo.PageVO;
import com.example.aicourse.vo.course.TrendPointVO;
import com.example.aicourse.vo.analytics.CourseStudentScoreVO;
import com.example.aicourse.vo.analytics.KnowledgePointPerformanceVO;
import com.example.aicourse.vo.analytics.StudentCoursePerformanceVO;
import com.example.aicourse.vo.analytics.TaskCompletionSummaryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics")
@Validated
public class AnalyticsController {

    private final AnalyticsService service;

    @Autowired
    public AnalyticsController(AnalyticsService service) {
        this.service = service;
    }

    /**
     * API 10.1 获取课程成绩趋势
     */
    @GetMapping("/courses/{courseId}/trend")
    public Result<List<TrendPointVO>> getCourseTrend(
            @PathVariable Long courseId,
            @RequestParam(required = false, defaultValue = "WEEK") String period) {
        List<TrendPointVO> trend = service.getTrend(courseId, period);
        return Result.ok(trend);
    }

    /**
     * API 10.2 导出课程成绩CSV
     */
    @GetMapping("/courses/{courseId}/export")
    public ResponseEntity<Resource> exportCourseScores(
            @PathVariable Long courseId,
            @RequestParam(required = false, defaultValue = "QUIZ") String type) {
        return service.exportCsv(courseId, type);
    }

    /**
     * API 10.3 获取课程所有学生详细成绩
     */
    @GetMapping("/courses/{courseId}/student-scores")
    public Result<PageVO<CourseStudentScoreVO>> getCourseStudentScores(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size) {
        Page<CourseStudentScoreVO> resultPage = service.getCourseStudentScores(courseId, new Page<>(page, size));
        PageVO<CourseStudentScoreVO> pageVO = new PageVO<>(resultPage.getRecords(), resultPage.getTotal(), resultPage.getSize(), resultPage.getCurrent());
        return Result.ok(pageVO);
    }

    /**
     * API 10.4 获取课程任务完成统计
     */
    @GetMapping("/courses/{courseId}/task-completion-summary")
    public Result<List<TaskCompletionSummaryVO>> getTaskCompletionSummary(@PathVariable Long courseId) {
        List<TaskCompletionSummaryVO> summary = service.getTaskCompletionSummary(courseId);
        return Result.ok(summary);
    }

    /**
     * API 10.5 获取学生在特定课程的表现概览
     */
    @GetMapping("/student/{studentId}/course/{courseId}/performance-overview")
    public Result<StudentCoursePerformanceVO> getStudentPerformanceOverview(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {
        StudentCoursePerformanceVO overview = service.getStudentPerformanceOverview(studentId, courseId);
        return Result.ok(overview);
    }

    /**
     * API 10.6 获取知识点表现数据
     */
    @GetMapping("/courses/{courseId}/knowledge-points/performance")
    public Result<List<KnowledgePointPerformanceVO>> getKnowledgePointPerformance(
            @PathVariable Long courseId,
            @RequestParam(required = false) Long studentId) {
        List<KnowledgePointPerformanceVO> performance = service.getKnowledgePointPerformance(courseId, studentId);
        return Result.ok(performance);
    }
}