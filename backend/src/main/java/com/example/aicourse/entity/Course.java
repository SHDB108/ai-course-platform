
package com.example.aicourse.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data @TableName("t_course")
public class Course{

 @TableId(type = IdType.AUTO)
 private Long id;

 private String courseCode,courseName;
 private Double credit;
 private Integer hours;
 private Long teacherId;

 @TableField(fill = FieldFill.INSERT)
 private LocalDateTime gmtCreate;

 @TableField(fill = FieldFill.INSERT_UPDATE)
 private LocalDateTime gmtModified;
}
