package com.example.aicourse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("exam_question")
public class ExamQuestion {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long examId;
    private String questionType; // SINGLE_CHOICE, MULTIPLE_CHOICE, TRUE_FALSE, SHORT_ANSWER, ESSAY
    private String questionText;
    private String options; // JSON格式存储选择题选项
    private String correctAnswer; // JSON格式存储正确答案
    private Integer score;
    private String explanation;
    private Integer orderNum;
    private LocalDateTime createdAt;
}