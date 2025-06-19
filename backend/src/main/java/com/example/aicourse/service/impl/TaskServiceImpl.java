package com.example.aicourse.service.impl;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.aicourse.entity.Task;
import com.example.aicourse.repository.TaskMapper;
import com.example.aicourse.service.TaskService;
@Service public class TaskServiceImpl extends ServiceImpl<TaskMapper,Task> implements TaskService{}