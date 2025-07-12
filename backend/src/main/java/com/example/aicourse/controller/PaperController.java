package com.example.aicourse.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.aicourse.dto.paper.PaperGenDTO;
import com.example.aicourse.service.PaperService;
import com.example.aicourse.utils.Result;
import com.example.aicourse.vo.PageVO;
import com.example.aicourse.vo.paper.QuizPaperDetailsVO;
import com.example.aicourse.vo.paper.QuizPaperVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/papers")
@Validated
public class PaperController {

    private final PaperService paperService;

    @Autowired
    public PaperController(PaperService paperService) {
        this.paperService = paperService;
    }

    /**
     * API 8.8 智能组卷
     */
    @PostMapping("/generate")
    public Result<Long> generate(@Valid @RequestBody PaperGenDTO dto) {
        Long paperId = paperService.generatePaper(dto);
        return Result.ok(paperId);
    }

    /**
     * API 8.9 获取测验试卷列表 (分页)
     */
    @GetMapping
    public Result<PageVO<QuizPaperVO>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String title
    ) {
        Page<QuizPaperVO> resultPage = paperService.list(page, size, courseId, title);
        PageVO<QuizPaperVO> pageVO = new PageVO<>(resultPage.getRecords(), resultPage.getTotal(), resultPage.getSize(), resultPage.getCurrent(), resultPage.getPages());
        return Result.ok(pageVO);
    }

    /**
     * API 8.10 获取测验试卷详情
     */
    @GetMapping("/{id}")
    public Result<QuizPaperDetailsVO> getById(@PathVariable Long id) {
        QuizPaperDetailsVO vo = paperService.getPaperDetailsById(id);
        return Result.ok(vo);
    }

    /**
     * API 8.11 删除测验试卷
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        paperService.delete(id);
        return Result.ok();
    }
}