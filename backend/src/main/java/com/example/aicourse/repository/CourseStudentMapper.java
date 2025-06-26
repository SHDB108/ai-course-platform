package com.example.aicourse.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.aicourse.entity.CourseStudent;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CourseStudentMapper extends BaseMapper<CourseStudent> {
    // 可以在这里添加自定义的查询方法，如果BaseMapper提供的不足以满足需求
}