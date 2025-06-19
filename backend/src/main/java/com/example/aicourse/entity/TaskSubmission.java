
package com.example.aicourse.entity;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;
@Data @TableName("t_task_submission")
public class TaskSubmission{
 @TableId private Long id;
 private Long taskId,studentId; private String status;
 private Double score; private LocalDateTime submittedAt;
 private String answerPath;
}
