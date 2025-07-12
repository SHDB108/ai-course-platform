package com.example.aicourse.vo.exam;

import lombok.Data;

import java.util.List;

@Data
public class ExamQuestionVO {
    private Long id;
    private Long examId;
    private String questionType; // SINGLE_CHOICE, MULTIPLE_CHOICE, TRUE_FALSE, SHORT_ANSWER, ESSAY
    private String questionText;
    private List<String> options; // 选择题选项
    private Object correctAnswer; // 正确答案
    private Integer score;
    private String explanation;
    private Integer order;
}