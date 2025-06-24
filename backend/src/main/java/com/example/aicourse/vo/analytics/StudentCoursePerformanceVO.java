package com.example.aicourse.vo.analytics;

import com.example.aicourse.vo.course.TrendPointVO;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

/**
 * API 10.5 学生课程表现概览响应
 */
@Data
public class StudentCoursePerformanceVO {
    private Long studentId;
    private String studentName;
    private Long courseId;
    private String courseName;
    private BigDecimal overallCourseScore;
    private List<TrendPointVO> scoreTrend;
    private List<TaskCompletionSummaryVO> taskCompletionSummary;
    // You may need to create a QuizPerformanceSummaryVO as well
    // private List<QuizPerformanceSummaryVO> quizPerformanceSummary;
}