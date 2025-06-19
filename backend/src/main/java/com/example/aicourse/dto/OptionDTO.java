package com.example.aicourse.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

/**
 * 选项 DTO，用于单/多选题的选项列表
 */
@Data
public class OptionDTO {
    @NotBlank
    private String content;

    private Boolean isCorrect = false;
}