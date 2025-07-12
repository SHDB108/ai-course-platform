package com.example.aicourse.vo.exam;

import lombok.Data;

@Data
public class ExamAnswerVO {
    private Long questionId;
    private String questionType; // SINGLE_CHOICE, MULTIPLE_CHOICE, TRUE_FALSE, SHORT_ANSWER, ESSAY
    private Object studentAnswer; // 学生答案，可以是字符串或字符串数组
    private Object correctAnswer; // 正确答案，可以是字符串或字符串数组
    private Boolean isCorrect;
    private Integer score;
    private Integer maxScore;
}