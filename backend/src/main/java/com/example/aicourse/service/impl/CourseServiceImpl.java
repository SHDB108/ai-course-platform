package com.example.aicourse.service.impl;
import com.example.aicourse.repository.CourseMapper;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.aicourse.entity.Course;
import com.example.aicourse.service.CourseService;
@Service public class CourseServiceImpl extends ServiceImpl<CourseMapper,Course> implements CourseService{}