package com.example.aicourse.service;

import com.example.aicourse.dto.PaperGenDTO;

public interface PaperService {
    /** 根据策略（知识点/难度/随机）生成试卷，返回 paperId */
    Long generatePaper(PaperGenDTO dto);
}