package com.example.aicourse.repository;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.aicourse.entity.Course;
import org.apache.ibatis.annotations.Mapper;
@Mapper
public interface CourseMapper extends BaseMapper<Course>{}