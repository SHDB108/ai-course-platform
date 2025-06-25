package com.example.aicourse.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.aicourse.dto.paper.QuestionDTO;
import com.example.aicourse.vo.paper.QuestionVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface QuestionService {
    /**
     * 创建一道题目 (API 8.1)
     */
    Long create(QuestionDTO dto);

    /**
     * 列出题目 (API 8.2)
     */
    Page<QuestionVO> list(long page, long size, Long courseId, String type, Integer difficulty, String knowledge, String keyword);

    /**
     * 获取题目详情VO (API 8.3)
     */
    QuestionVO getVOById(Long id);

    /**
     * 更新题目 (API 8.4)
     */
    void update(Long id, QuestionDTO dto);

    /**
     * 删除题目 (API 8.5)
     */
    void delete(Long id);

    /**
     * 从Excel文件批量导入题目 (API 8.6)
     */
    void importQuestions(MultipartFile file, Long courseId, Long creatorId) throws IOException;

    /**
     * 导出题目到Excel (API 8.7)
     */
    void exportQuestions(HttpServletResponse response, Long courseId) throws IOException;
}