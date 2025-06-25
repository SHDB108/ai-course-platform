package com.example.aicourse.controller;

import com.example.aicourse.dto.quiz.QuizGradeDTO;
import com.example.aicourse.dto.quiz.QuizSubmissionDTO;
import com.example.aicourse.service.QuizSubmissionService;
import com.example.aicourse.utils.Result;
import com.example.aicourse.vo.quiz.QuizSubmissionDetailVO;
import com.example.aicourse.vo.quiz.QuizSubmissionVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/quiz-submissions")
@Validated
public class QuizSubmissionController {

    private final QuizSubmissionService quizSubmissionService;

    @Autowired
    public QuizSubmissionController(QuizSubmissionService quizSubmissionService) {
        this.quizSubmissionService = quizSubmissionService;
    }

    /**
     * API 9.1 学生提交测验
     */
    @PostMapping
    public Result<Long> submit(@Valid @RequestBody QuizSubmissionDTO dto) {
        Long submissionId = quizSubmissionService.submit(dto);
        return Result.ok(submissionId);
    }

    /**
     * API 9.2 获取学生测验提交记录
     */
    @GetMapping("/student/{studentId}/paper/{paperId}")
    public Result<List<QuizSubmissionVO>> getStudentSubmissionsForPaper(
            @PathVariable Long studentId,
            @PathVariable Long paperId) {
        List<QuizSubmissionVO> vos = quizSubmissionService.getStudentSubmissionsForPaper(studentId, paperId);
        return Result.ok(vos);
    }

    /**
     * API 9.3 获取测验提交详情
     */
    @GetMapping("/{id}")
    public Result<QuizSubmissionDetailVO> getById(@PathVariable Long id) {
        QuizSubmissionDetailVO vo = quizSubmissionService.getSubmissionDetailsById(id);
        return Result.ok(vo);
    }

    /**
     * API 9.4 批改测验提交
     */
    @PutMapping("/{id}/grade")
    public Result<Void> grade(@PathVariable Long id, @RequestBody QuizGradeDTO dto) {
        quizSubmissionService.gradeSubmission(id, dto);
        return Result.ok();
    }
}