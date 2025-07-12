package com.example.aicourse.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.aicourse.entity.CourseEnrollment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CourseEnrollmentMapper extends BaseMapper<CourseEnrollment> {
    
    /**
     * 获取有学生注册的活跃课程数量
     */
    @Select("SELECT COUNT(DISTINCT course_id) FROM course_enrollments")
    long selectActiveCourseCount();
}