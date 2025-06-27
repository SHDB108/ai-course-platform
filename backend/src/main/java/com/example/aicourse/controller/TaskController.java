package com.example.aicourse.controller;

import com.example.aicourse.dto.task.TaskCreateDTO;
import com.example.aicourse.dto.task.TaskUpdateDTO;
import com.example.aicourse.service.TaskService;
import com.example.aicourse.utils.Result;
import com.example.aicourse.vo.PageVO;
import com.example.aicourse.vo.task.TaskVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 任务管理相关的API控制器
 */
@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * API 5.1 获取课程任务列表
     * @param courseId 课程ID
     * @param type 任务类型过滤
     * @param status 任务状态过滤
     * @param page 当前页码
     * @param size 每页数量
     * @return 分页的任务信息列表
     */
    @GetMapping("/course/{courseId}")
    public Result<PageVO<TaskVO>> getCourseTasks(
            @PathVariable Long courseId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size) {
        try {
            PageVO<TaskVO> taskPage = taskService.getCourseTasks(courseId, type, status, page, size);
            return Result.ok(taskPage);
        } catch (Exception e) {
            return Result.error("获取课程任务列表失败: " + e.getMessage());
        }
    }

    /**
     * API 5.2 获取任务详情
     * @param id 任务ID
     * @return 任务详细信息
     */
    @GetMapping("/{id}")
    public Result<TaskVO> getTaskDetails(@PathVariable Long id) {
        try {
            TaskVO taskVO = taskService.getTaskDetails(id);
            if (taskVO == null) {
                return Result.error("任务不存在");
            }
            return Result.ok(taskVO);
        } catch (Exception e) {
            return Result.error("获取任务详情失败: " + e.getMessage());
        }
    }

    /**
     * API 5.3 创建新任务
     * @param dto 任务创建请求DTO
     * @return 新创建的任务ID
     */
    @PostMapping
    public Result<Long> createTask(@Valid @RequestBody TaskCreateDTO dto) {
        try {
            Long newTaskId = taskService.createTask(dto);
            return Result.ok(newTaskId);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * API 5.4 更新任务信息
     * @param id 任务ID
     * @param dto 任务更新请求DTO
     * @return null
     */
    @PutMapping("/{id}")
    public Result<Void> updateTask(@PathVariable Long id, @Valid @RequestBody TaskUpdateDTO dto) {
        try {
            taskService.updateTask(id, dto);
            return Result.ok();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * API 5.5 删除任务
     * @param id 任务ID
     * @return null
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteTask(@PathVariable Long id) {
        try {
            taskService.deleteTask(id);
            return Result.ok();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * API 5.6 发布任务
     * @param id 任务ID
     * @return null
     */
    @PutMapping("/{id}/publish")
    public Result<Void> publishTask(@PathVariable Long id) {
        try {
            taskService.updateTaskStatus(id, "PUBLISHED"); // 设置状态为 PUBLISHED
            return Result.ok();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * API 5.7 撤销发布任务
     * @param id 任务ID
     * @return null
     */
    @PutMapping("/{id}/unpublish")
    public Result<Void> unpublishTask(@PathVariable Long id) {
        try {
            taskService.updateTaskStatus(id, "DRAFT"); // 设置状态为 DRAFT 或其他非发布状态
            return Result.ok();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}