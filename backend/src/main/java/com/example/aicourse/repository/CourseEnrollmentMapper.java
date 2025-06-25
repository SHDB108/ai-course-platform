package com.example.aicourse.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.aicourse.entity.CourseEnrollment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CourseEnrollmentMapper extends BaseMapper<CourseEnrollment> {
}