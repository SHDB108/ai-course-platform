package com.example.aicourse.vo.analytics;

import java.math.BigDecimal;
import java.util.Map;
import lombok.Data;

/**
 * API 10.3 课程学生详细成绩响应
 */
@Data
public class CourseStudentScoreVO {
    private Long studentId;
    private String studentName;
    private String stuNo;
    private BigDecimal overallScore;
    private Map<Long, BigDecimal> taskScores;
    private Map<Long, BigDecimal> quizScores;
}