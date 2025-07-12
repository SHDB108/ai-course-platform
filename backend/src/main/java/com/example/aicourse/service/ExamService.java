package com.example.aicourse.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.aicourse.entity.Exam;
import com.example.aicourse.vo.exam.ExamVO;
import com.example.aicourse.vo.exam.ExamStatisticsVO;

import java.util.Map;

public interface ExamService {
    
    /**
     * 分页查询学生考试列表
     */
    IPage<ExamVO> getStudentExams(Long studentId, Integer pageNum, Integer pageSize, 
                                  String status, String examType, Long courseId);
    
    /**
     * 获取考试详情
     */
    ExamVO getExamDetail(Long examId);
    
    /**
     * 获取学生考试统计
     */
    ExamStatisticsVO getStudentExamStatistics(Long studentId);
    
    /**
     * 开始考试
     */
    Map<String, Object> startExam(Long examId, Long studentId);
    
    /**
     * 获取考试剩余时间
     */
    Map<String, Object> getExamRemainingTime(Long examId, Long submissionId);
}