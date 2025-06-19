
package com.example.aicourse.entity;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;
@Data @TableName("t_task")
public class Task{
 @TableId private Long id;
 private Long courseId;
 private String title,type,description,submitType;
 private LocalDateTime deadline;
 private Long creatorId;
}
