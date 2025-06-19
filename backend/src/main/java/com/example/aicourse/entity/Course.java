
package com.example.aicourse.entity;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
@Data @TableName("t_course")
public class Course{
 @TableId
 private Long id;
 private String courseCode,courseName;
 private Double credit;
 private Integer hours;
 private Long teacherId;
}
