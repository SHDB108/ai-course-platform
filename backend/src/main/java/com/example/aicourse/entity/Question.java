package com.example.aicourse.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("t_question")
public class Question {
    @TableId
    private Long id;
    private Long courseId;
    private String stem;
    private String type; // SC|MC|TF|FILL|SA|CODE
    private Integer difficulty; // 1~5
    private String knowledge;
    private String answer;
    private Long creatorId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime gmtCreate;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime gmtModified;

    @TableField(exist = false)
    private List<QuestionOption> options;
}