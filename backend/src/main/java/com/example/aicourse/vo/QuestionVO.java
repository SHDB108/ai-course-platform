package com.example.aicourse.vo;

import com.example.aicourse.entity.QuestionOption;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


@Data
public class QuestionVO {
    private Long id;
    private String stem;
    private String type;
    private Integer difficulty;
    private String knowledge;
    private String answer;
    private Long creatorId;
    private LocalDateTime gmtCreate;

    private List<QuestionOption> options;
}