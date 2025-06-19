
package com.example.aicourse.entity;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
@Data @TableName("t_student")
public class Student{
 @TableId
 private Long id;
 private String stuNo,name;
 private Integer gender;
 private String major,grade,phone,email;
}
