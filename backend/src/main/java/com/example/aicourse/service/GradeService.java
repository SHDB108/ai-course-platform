package com.example.aicourse.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.aicourse.entity.SemesterGPA;
import com.example.aicourse.vo.grade.GradeVO;
import com.example.aicourse.vo.grade.GradeStatisticsVO;
import com.example.aicourse.vo.grade.CourseGradeVO;
import com.example.aicourse.vo.grade.GPAHistoryVO;
import com.example.aicourse.vo.grade.GradeTrendVO;

import java.util.List;
import java.util.Map;

public interface GradeService {
    
    /**
     * 分页查询学生成绩列表
     */
    IPage<GradeVO> getStudentGrades(Long studentId, Integer pageNum, Integer pageSize, 
                                    String gradeType, String semester, Long courseId);
    
    /**
     * 获取学生课程成绩详情
     */
    CourseGradeVO getStudentCourseGrades(Long studentId, Long courseId);
    
    /**
     * 获取学生成绩统计
     */
    GradeStatisticsVO getStudentGradeStatistics(Long studentId, String semester);
    
    /**
     * 获取学生GPA历史记录
     */
    List<GPAHistoryVO> getStudentGPAHistory(Long studentId);
    
    /**
     * 获取学生成绩趋势
     */
    List<GradeTrendVO> getStudentGradeTrend(Long studentId, Integer months);
    
    /**
     * 获取学生课程排名
     */
    Map<String, Object> getStudentCourseRanking(Long studentId, Long courseId);
    
    /**
     * 获取学生学期成绩汇总
     */
    Map<String, Object> getStudentSemesterSummary(Long studentId, String semester);
    
    /**
     * 导出成绩报告
     */
    byte[] exportGradeReport(Long studentId, String format, String semester, Long courseId);
}