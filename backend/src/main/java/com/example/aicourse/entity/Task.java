
package com.example.aicourse.entity;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data @TableName("t_task")
public class Task{
 @TableId
 private Long id;
 private Long courseId;
 private String title,type,description,submitType;
 private LocalDateTime deadline;
 private Long creatorId;
 private BigDecimal maxScore;

 @TableField(fill = FieldFill.INSERT)
 private LocalDateTime gmtCreate;

 @TableField(fill = FieldFill.INSERT_UPDATE)
 private LocalDateTime gmtModified;
}
