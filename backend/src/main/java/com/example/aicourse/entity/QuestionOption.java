package com.example.aicourse.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_question_option")
public class QuestionOption {
    @TableId
    private Long id;
    private Long questionId;
    private String content;
    private Boolean isCorrect; //0为正确选项， 1为错误选项
}