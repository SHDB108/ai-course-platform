package com.example.aicourse.dto;


import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Data
public class QuestionDTO {
    @NotBlank
    private String stem;

    /** 题型：SC（单选）、MC（多选）、TF（判断）、FILL、SA、CODE */
    @NotBlank
    private String type;

    private Integer difficulty;
    private List<String> knowledge;

    /** 简答/填空可直接存文本；选择题不使用此字段 */
    private String answer;

    /** 如果是 SC/MC，则 options 非空 */
    private List<OptionDTO> options;
}
