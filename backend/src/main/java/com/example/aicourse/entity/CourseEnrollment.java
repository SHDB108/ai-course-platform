package com.example.aicourse.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("t_course_enrollment")
@NoArgsConstructor
@AllArgsConstructor
public class CourseEnrollment {
    // 复合主键由 courseId 和 studentId 共同构成
    private Long courseId;
    private Long studentId;
}