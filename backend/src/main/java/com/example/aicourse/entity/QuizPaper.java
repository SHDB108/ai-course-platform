package com.example.aicourse.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_quiz_paper")
public class QuizPaper {
    @TableId
    private Long id;
    private Long courseId;
    private String title;
    private BigDecimal totalScore;
    private LocalDateTime gmtCreate;

    public QuizPaper(Long courseId, String title, BigDecimal totalScore) {
        this.courseId = courseId;
        this.title = title;
        this.totalScore = totalScore;
        this.gmtCreate = LocalDateTime.now();
    }
}