package com.example.aicourse.service.impl;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.aicourse.entity.Student;
import com.example.aicourse.repository.StudentMapper;
import com.example.aicourse.service.StudentService;
@Service public class StudentServiceImpl extends ServiceImpl<StudentMapper,Student> implements StudentService{}