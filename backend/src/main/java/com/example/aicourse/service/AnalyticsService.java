package com.example.aicourse.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.aicourse.vo.course.TrendPointVO;
import com.example.aicourse.vo.analytics.AnalyticsOverviewVO;
import com.example.aicourse.vo.analytics.CourseStudentScoreVO;
import com.example.aicourse.vo.analytics.KnowledgePointPerformanceVO;
import com.example.aicourse.vo.analytics.StudentCoursePerformanceVO;
import com.example.aicourse.vo.analytics.TaskCompletionSummaryVO;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AnalyticsService {
    
    /**
     * 获取分析概览数据
     */
    AnalyticsOverviewVO getAnalyticsOverview();
    
    /**
     * 获取课程成绩趋势 (API 10.1)
     * @param courseId 课程ID
     * @param period 时间周期 (暂未实现)
     */
    List<TrendPointVO> getTrend(Long courseId, String period);

    /**
     * 导出课程成绩CSV (API 10.2)
     * @param courseId 课程ID
     * @param type 成绩类型 ("QUIZ" 或 "TASK")
     */
    ResponseEntity<Resource> exportCsv(Long courseId, String type);

    /**
     * 获取课程所有学生详细成绩 (API 10.3)
     * @param courseId 课程ID
     * @param page 分页对象
     * @return 分页后的学生成绩VO
     */
    Page<CourseStudentScoreVO> getCourseStudentScores(Long courseId, Page<CourseStudentScoreVO> page);

    /**
     * 获取课程任务完成统计 (API 10.4)
     * @param courseId 课程ID
     * @return 任务完成统计VO列表
     */
    List<TaskCompletionSummaryVO> getTaskCompletionSummary(Long courseId);

    /**
     * 获取学生在特定课程的表现概览 (API 10.5)
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 学生课程表现概览VO
     */
    StudentCoursePerformanceVO getStudentPerformanceOverview(Long studentId, Long courseId);

    /**
     * 获取知识点表现数据 (API 10.6)
     * @param courseId 课程ID
     * @param studentId 可选的学生ID
     * @return 知识点表现VO列表
     */
    List<KnowledgePointPerformanceVO> getKnowledgePointPerformance(Long courseId, Long studentId);
}