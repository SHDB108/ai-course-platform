package com.example.aicourse.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_quiz_paper_question")
public class QuizPaperQuestion {
    @TableId
    private Long id;

    @TableField(value = "paper_id")
    private Long paperId;

    @TableField(value = "question_id")
    private Long questionId;

    private BigDecimal score;
}