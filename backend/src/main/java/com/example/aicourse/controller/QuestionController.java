package com.example.aicourse.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.aicourse.dto.paper.QuestionDTO;
import com.example.aicourse.service.QuestionService;
import com.example.aicourse.utils.Result;
import com.example.aicourse.vo.PageVO;
import com.example.aicourse.vo.paper.QuestionVO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/questions")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    /**
     * API 8.1 创建题目
     */
    @PostMapping
    public Result<Long> create(@Valid @RequestBody QuestionDTO dto) {
        Long questionId = questionService.create(dto);
        return Result.ok(questionId);
    }

    /**
     * API 8.2 获取题目列表 (分页)
     */
    @GetMapping
    public Result<PageVO<QuestionVO>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer difficulty,
            @RequestParam(required = false) String knowledge,
            @RequestParam(required = false) String keyword
    ) {
        Page<QuestionVO> resultPage = questionService.list(page, size, courseId, type, difficulty, knowledge, keyword);
        PageVO<QuestionVO> pageVO = new PageVO<>(resultPage.getRecords(), resultPage.getTotal(), resultPage.getSize(), resultPage.getCurrent());
        return Result.ok(pageVO);
    }

    /**
     * API 8.3 获取题目详情
     */
    @GetMapping("/{id}")
    public Result<QuestionVO> getById(@PathVariable Long id) {
        QuestionVO vo = questionService.getVOById(id);
        return Result.ok(vo);
    }

    /**
     * API 8.4 更新题目
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody QuestionDTO dto) {
        questionService.update(id, dto);
        return Result.ok();
    }

    /**
     * API 8.5 删除题目
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        questionService.delete(id);
        return Result.ok();
    }

    @PostMapping("/import")
    public Result<Void> importQuestions(
            @RequestPart("file") MultipartFile file,
            @RequestParam Long courseId) throws IOException {
        // TODO: creatorId 应从安全上下文中获取
        Long creatorId = 1L; // 占位符
        questionService.importQuestions(file, courseId, creatorId);
        return Result.ok();
    }

    /**
     * API 8.7 导出题目
     */
    @GetMapping("/export")
    public void exportQuestions(
            HttpServletResponse response,
            @RequestParam(required = false) Long courseId
    ) throws IOException {
        questionService.exportQuestions(response, courseId);
    }


}