package com.example.aicourse.service;

import com.example.aicourse.dto.quiz.QuizGradeDTO;
import com.example.aicourse.dto.quiz.QuizSubmissionDTO;
import com.example.aicourse.vo.quiz.QuizSubmissionDetailVO;
import com.example.aicourse.vo.quiz.QuizSubmissionVO;

import java.util.List;

public interface QuizSubmissionService {

    /**
     * 学生提交测验 (API 9.1)
     * @param dto 提交的数据
     * @return 新创建的提交记录ID
     */
    Long submit(QuizSubmissionDTO dto);

    /**
     * 获取学生在特定试卷的提交记录 (API 9.2)
     * @param studentId 学生ID
     * @param paperId 试卷ID
     * @return 提交记录VO列表
     */
    List<QuizSubmissionVO> getStudentSubmissionsForPaper(Long studentId, Long paperId);

    /**
     * 获取单次测验提交详情 (API 9.3)
     * @param id 提交记录ID
     * @return 提交详情VO
     */
    QuizSubmissionDetailVO getSubmissionDetailsById(Long id);

    /**
     * 批改测验提交 (API 9.4)
     * @param id 提交记录ID
     * @param dto 批改数据
     */
    void gradeSubmission(Long id, QuizGradeDTO dto);
}