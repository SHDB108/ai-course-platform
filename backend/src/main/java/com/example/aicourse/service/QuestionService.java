package com.example.aicourse.service;

import com.example.aicourse.dto.QuestionDTO;
import com.example.aicourse.vo.QuestionVO;
import java.util.List;

public interface QuestionService {
    /** 创建一道题目（支持 SC/MC/TF 等） */
    Long create(QuestionDTO dto);
    /** 列出某课程下的所有题目 */
    List<QuestionVO> listByCourse(Long courseId);
}