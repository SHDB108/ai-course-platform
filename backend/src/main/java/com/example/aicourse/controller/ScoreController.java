package com.example.aicourse.controller;
import com.example.aicourse.entity.TaskSubmission;
import com.example.aicourse.service.TaskSubmissionService;
import org.springframework.beans.factory.annotation.Autowired; import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/v1/scores")
public class ScoreController{
 @Autowired TaskSubmissionService srv;
 @GetMapping("/{courseId}/trend")
 public List<TaskSubmission> trend(@PathVariable Long courseId){return srv.lambdaQuery().like(TaskSubmission::getTaskId,courseId).list();}
}