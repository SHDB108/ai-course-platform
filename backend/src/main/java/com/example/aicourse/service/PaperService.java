package com.example.aicourse.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.aicourse.dto.paper.PaperGenDTO;
import com.example.aicourse.vo.paper.QuizPaperDetailsVO;
import com.example.aicourse.vo.paper.QuizPaperVO;

public interface PaperService {
    /**
     * 根据策略生成试卷 (API 8.8)
     */
    Long generatePaper(PaperGenDTO dto);

    /**
     * 获取测验试卷列表 (API 8.9)
     */
    Page<QuizPaperVO> list(long page, long size, Long courseId, String title);

    /**
     * 获取测验试卷详情 (API 8.10)
     */
    QuizPaperDetailsVO getPaperDetailsById(Long id);

    /**
     * 删除测验试卷 (API 8.11)
     */
    void delete(Long id);
}