package com.example.aicourse.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.aicourse.service.GradeService;
import com.example.aicourse.utils.Result;
import com.example.aicourse.vo.grade.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class GradeController {

    @Autowired
    private GradeService gradeService;

    /**
     * 获取学生所有成绩
     */
    @GetMapping("/students/{studentId}/grades")
    public Result<IPage<GradeVO>> getStudentGrades(
            @PathVariable Long studentId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String gradeType,
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) Long courseId) {
        
        IPage<GradeVO> result = gradeService.getStudentGrades(studentId, pageNum, pageSize, gradeType, semester, courseId);
        return Result.ok(result);
    }

    /**
     * 获取学生课程成绩详情
     */
    @GetMapping("/students/{studentId}/courses/{courseId}/grades")
    public Result<CourseGradeVO> getStudentCourseGrades(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {
        
        CourseGradeVO result = gradeService.getStudentCourseGrades(studentId, courseId);
        return Result.ok(result);
    }

    /**
     * 获取学生成绩统计
     */
    @GetMapping("/students/{studentId}/grade-statistics")
    public Result<GradeStatisticsVO> getStudentGradeStatistics(
            @PathVariable Long studentId,
            @RequestParam(required = false) String semester) {
        
        GradeStatisticsVO statistics = gradeService.getStudentGradeStatistics(studentId, semester);
        return Result.ok(statistics);
    }

    /**
     * 获取学生GPA历史记录
     */
    @GetMapping("/students/{studentId}/gpa-history")
    public Result<List<GPAHistoryVO>> getStudentGPAHistory(@PathVariable Long studentId) {
        List<GPAHistoryVO> history = gradeService.getStudentGPAHistory(studentId);
        return Result.ok(history);
    }

    /**
     * 获取学生成绩趋势
     */
    @GetMapping("/students/{studentId}/grade-trend")
    public Result<List<GradeTrendVO>> getStudentGradeTrend(
            @PathVariable Long studentId,
            @RequestParam(required = false) Integer months) {
        
        List<GradeTrendVO> trend = gradeService.getStudentGradeTrend(studentId, months);
        return Result.ok(trend);
    }

    /**
     * 获取单个成绩详情
     */
    @GetMapping("/grades/{gradeId}")
    public Result<GradeVO> getGradeDetail(@PathVariable Long gradeId) {
        // 这里可以通过直接查询Grade实体来实现
        // 为了简化，暂时返回空实现
        throw new RuntimeException("功能待实现");
    }

    /**
     * 获取学生课程排名
     */
    @GetMapping("/students/{studentId}/courses/{courseId}/ranking")
    public Result<Map<String, Object>> getStudentCourseRanking(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {
        
        Map<String, Object> ranking = gradeService.getStudentCourseRanking(studentId, courseId);
        return Result.ok(ranking);
    }

    /**
     * 获取学生学期成绩汇总
     */
    @GetMapping("/students/{studentId}/semester-summary")
    public Result<Map<String, Object>> getStudentSemesterSummary(
            @PathVariable Long studentId,
            @RequestParam String semester) {
        
        Map<String, Object> summary = gradeService.getStudentSemesterSummary(studentId, semester);
        return Result.ok(summary);
    }

    /**
     * 导出成绩单
     */
    @GetMapping("/students/{studentId}/grade-report/export")
    public ResponseEntity<byte[]> exportGradeReport(
            @PathVariable Long studentId,
            @RequestParam String format,
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) Long courseId) {
        
        byte[] reportData = gradeService.exportGradeReport(studentId, format, semester, courseId);
        
        HttpHeaders headers = new HttpHeaders();
        if ("PDF".equalsIgnoreCase(format)) {
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "grade-report.pdf");
        } else if ("EXCEL".equalsIgnoreCase(format)) {
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", "grade-report.xlsx");
        }
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(reportData);
    }
}