package com.example.aicourse.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@TableName("t_quiz_submission")
public class QuizSubmission {
    @TableId
    private Long id;
    private Long paperId;
    private Long studentId;
    private LocalDateTime startAt;
    private LocalDateTime submitAt;
    private BigDecimal score;
    private String answers;      // JSON 序列化的答案
    private LocalDateTime gmtCreate;
}