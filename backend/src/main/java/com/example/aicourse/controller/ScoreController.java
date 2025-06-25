package com.example.aicourse.controller;
import com.example.aicourse.entity.TaskSubmission;
import com.example.aicourse.service.TaskSubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/scores")
@Validated
public class ScoreController{
 final TaskSubmissionService srv;

 @Autowired
 public ScoreController(TaskSubmissionService srv) {
  this.srv = srv;
 }

 @GetMapping("/{courseId}/trend")

 public List<TaskSubmission> trend(
         @PathVariable Long courseId){return srv.lambdaQuery().like(TaskSubmission::getTaskId,courseId).list();
  }
}