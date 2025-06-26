package com.example.aicourse.controller;

import com.example.aicourse.dto.task.IntelligentGradeRequestDTO;
import com.example.aicourse.dto.task.TaskSubmissionCreateDTO;
import com.example.aicourse.dto.task.TaskSubmissionGradeDTO;
import com.example.aicourse.service.TaskSubmissionService;
import com.example.aicourse.utils.Result;
import com.example.aicourse.vo.PageVO;
import com.example.aicourse.vo.task.IntelligentGradeResultVO;
import com.example.aicourse.vo.task.TaskSubmissionVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 任务提交管理相关的API控制器
 */
@RestController
@RequestMapping("/api/v1/task-submissions")
public class TaskSubmissionController {

    @Autowired
    private TaskSubmissionService taskSubmissionService;

    /**
     * API 6.1 学生提交任务 (在线/链接提交)
     * consumes = MediaType.APPLICATION_JSON_VALUE
     * @param dto 任务提交DTO
     * @return 新创建的任务提交ID
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Result<Long> createSubmissionJson(@Valid @RequestBody TaskSubmissionCreateDTO dto) {
        try {
            Long newId = taskSubmissionService.createSubmissionOnlineOrLink(dto);
            return Result.ok(newId);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * API 6.1 学生提交任务 (文件提交)
     * consumes = MediaType.MULTIPART_FORM_DATA_VALUE
     * @param taskId 任务ID
     * @param studentId 学生ID
     * @param file 提交的文件
     * @return 新创建的任务提交ID
     */
    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<Long> createSubmissionFile(
            @RequestPart("taskId") Long taskId,
            @RequestPart("studentId") Long studentId,
            @RequestPart("file") MultipartFile file) {
        try {
            Long newId = taskSubmissionService.createSubmissionFile(taskId, studentId, file);
            return Result.ok(newId);
        } catch (IOException e) {
            return Result.error("文件上传失败: " + e.getMessage());
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * API 6.2 获取学生在某课程中的所有任务提交
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @param status 提交状态过滤 (可选)
     * @return 任务提交信息列表
     */
    @GetMapping("/student/{studentId}/course/{courseId}")
    public Result<List<TaskSubmissionVO>> getSubmissionsByStudentAndCourse(
            @PathVariable Long studentId,
            @PathVariable Long courseId,
            @RequestParam(required = false) String status) {
        try {
            List<TaskSubmissionVO> submissions = taskSubmissionService.getSubmissionsByStudentAndCourse(studentId, courseId, status);
            return Result.ok(submissions);
        } catch (Exception e) {
            return Result.error("获取学生任务提交列表失败: " + e.getMessage());
        }
    }

    /**
     * API 6.3 获取特定任务的所有提交
     * @param taskId 任务ID
     * @param status 提交状态过滤 (可选)
     * @param page 当前页码
     * @param size 每页数量
     * @return 分页的任务提交信息列表
     */
    @GetMapping("/task/{taskId}")
    public Result<PageVO<TaskSubmissionVO>> getSubmissionsByTask(
            @PathVariable Long taskId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size) {
        try {
            PageVO<TaskSubmissionVO> submissionsPage = taskSubmissionService.getSubmissionsByTask(taskId, status, page, size);
            return Result.ok(submissionsPage);
        } catch (Exception e) {
            return Result.error("获取任务所有提交列表失败: " + e.getMessage());
        }
    }

    /**
     * API 6.4 获取单次任务提交详情
     * @param id 任务提交ID
     * @return 任务提交详细信息
     */
    @GetMapping("/{id}")
    public Result<TaskSubmissionVO> getSubmissionDetails(@PathVariable Long id) {
        try {
            TaskSubmissionVO submissionVO = taskSubmissionService.getSubmissionDetails(id);
            if (submissionVO == null) {
                return Result.error("任务提交记录不存在");
            }
            return Result.ok(submissionVO);
        } catch (Exception e) {
            return Result.error("获取任务提交详情失败: " + e.getMessage());
        }
    }

    /**
     * API 6.5 更新特定任务提交的得分和反馈 (教师批改)
     * @param id 任务提交ID
     * @param dto 批改请求DTO
     * @return null
     */
    @PutMapping("/{id}/grade")
    public Result<Void> gradeTaskSubmission(@PathVariable Long id, @Valid @RequestBody TaskSubmissionGradeDTO dto) {
        try {
            taskSubmissionService.gradeTaskSubmission(id, dto);
            return Result.ok();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * API 6.6 智能批改学生报告/简答题
     * @param id 任务提交ID
     * @param dto 智能批改请求DTO
     * @return 智能批改结果
     */
    @PostMapping("/{id}/intelligent-grade")
    public Result<IntelligentGradeResultVO> intelligentGrade(@PathVariable Long id, @RequestBody IntelligentGradeRequestDTO dto) {
        try {
            IntelligentGradeResultVO result = taskSubmissionService.intelligentGrade(id, dto);
            return Result.ok(result);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}