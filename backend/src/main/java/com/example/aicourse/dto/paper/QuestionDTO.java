package com.example.aicourse.dto.paper;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Data;

/**
 * API 8.1 创建题目请求 (结构确认)
 */
@Data
public class QuestionDTO {
    private Long courseId;
    @NotBlank
    private String stem;
    @NotBlank
    private String type;
    private Integer difficulty;
    private List<String> knowledge;
    private String answer;
    private Long creatorId;

    // 如果是 SC/MC，则 options 非空
    private List<OptionDTO> options;
}