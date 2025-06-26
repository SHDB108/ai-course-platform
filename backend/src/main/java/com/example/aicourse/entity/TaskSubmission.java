
package com.example.aicourse.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
@TableName("t_task_submission")
public class TaskSubmission {
 @TableId
 private Long id;

 private Long taskId;
 private Long studentId;
 private String status;
 private BigDecimal score;
 private LocalDateTime submittedAt;
 private String answerPath;
 private Long graderId;
 private LocalDateTime gradeTime;
 private String feedback;

 @TableField(fill = FieldFill.INSERT)
 private LocalDateTime gmtCreate;

 @TableField(fill = FieldFill.INSERT_UPDATE)
 private LocalDateTime gmtModified;
}
