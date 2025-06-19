package com.example.aicourse.service.impl;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.aicourse.entity.TaskSubmission;
import com.example.aicourse.repository.TaskSubmissionMapper;
import com.example.aicourse.service.TaskSubmissionService;
@Service public class TaskSubmissionServiceImpl extends ServiceImpl<TaskSubmissionMapper,TaskSubmission> implements TaskSubmissionService{

}