package com.example.aicourse.controller;

import com.example.aicourse.dto.teacher.TeacherCreateDTO;
import com.example.aicourse.dto.teacher.TeacherUpdateDTO;
import com.example.aicourse.service.TeacherService;
import com.example.aicourse.service.CourseService;
import com.example.aicourse.service.TaskService;
import com.example.aicourse.utils.Result;
import com.example.aicourse.vo.PageVO;
import com.example.aicourse.vo.teacher.TeacherVO;
import com.example.aicourse.vo.teacher.TeacherOptionVO;
import com.example.aicourse.vo.course.CourseVO;
import com.example.aicourse.vo.course.SimpleCourseVO;
import com.example.aicourse.vo.student.StudentVO;
import com.example.aicourse.vo.task.TeacherTaskVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 教师管理控制器
 */
@RestController
@RequestMapping("/api/v1/teachers")
public class TeacherController {

    private final TeacherService teacherService;
    private final CourseService courseService;
    private final TaskService taskService;

    @Autowired
    public TeacherController(TeacherService teacherService, CourseService courseService, TaskService taskService) {
        this.teacherService = teacherService;
        this.courseService = courseService;
        this.taskService = taskService;
    }

    /**
     * 获取教师列表 (分页)
     */
    @GetMapping
    public Result<PageVO<TeacherVO>> listTeachers(
            @RequestParam(defaultValue = "1") Long pageNum,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String status) {
        try {
            PageVO<TeacherVO> teachers = teacherService.listTeachers(pageNum, pageSize, keyword, department, status);
            return Result.ok(teachers);
        } catch (Exception e) {
            return Result.error("获取教师列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取教师选项列表（用于下拉选择）
     */
    @GetMapping("/options")
    public Result<List<TeacherOptionVO>> getTeacherOptions(
            @RequestParam(required = false) String keyword) {
        try {
            List<TeacherOptionVO> options = teacherService.getTeacherOptions(keyword);
            return Result.ok(options);
        } catch (Exception e) {
            return Result.error("获取教师选项失败：" + e.getMessage());
        }
    }

    /**
     * 获取教师详情
     */
    @GetMapping("/{id}")
    public Result<TeacherVO> getTeacherDetail(@PathVariable Long id) {
        try {
            TeacherVO teacher = teacherService.getTeacherDetail(id);
            if (teacher == null) {
                return Result.error("教师不存在");
            }
            return Result.ok(teacher);
        } catch (Exception e) {
            return Result.error("获取教师详情失败：" + e.getMessage());
        }
    }

    /**
     * 创建教师
     */
    @PostMapping
    public Result<Long> createTeacher(@Valid @RequestBody TeacherCreateDTO dto) {
        try {
            Long teacherId = teacherService.createTeacher(dto);
            return Result.ok(teacherId);
        } catch (Exception e) {
            return Result.error("创建教师失败：" + e.getMessage());
        }
    }

    /**
     * 更新教师信息
     */
    @PutMapping("/{id}")
    public Result<Void> updateTeacher(@PathVariable Long id, @Valid @RequestBody TeacherUpdateDTO dto) {
        try {
            boolean success = teacherService.updateTeacher(id, dto);
            if (!success) {
                return Result.error("更新教师信息失败或教师不存在");
            }
            return Result.ok();
        } catch (Exception e) {
            return Result.error("更新教师信息失败：" + e.getMessage());
        }
    }

    /**
     * 删除教师
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteTeacher(@PathVariable Long id) {
        try {
            boolean success = teacherService.deleteTeacher(id);
            if (!success) {
                return Result.error("删除教师失败或教师不存在");
            }
            return Result.ok();
        } catch (Exception e) {
            return Result.error("删除教师失败：" + e.getMessage());
        }
    }

    /**
     * 更新教师状态
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateTeacherStatus(@PathVariable Long id, @RequestBody StatusUpdateRequest request) {
        try {
            boolean success = teacherService.updateTeacherStatus(id, request.getStatus());
            if (!success) {
                return Result.error("更新教师状态失败或教师不存在");
            }
            return Result.ok();
        } catch (Exception e) {
            return Result.error("更新教师状态失败：" + e.getMessage());
        }
    }

    /**
     * 获取教师的课程列表
     */
    @GetMapping("/{id}/courses")
    public Result<PageVO<CourseVO>> getTeacherCourses(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Long pageNum,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {
        try {
            PageVO<CourseVO> courses = courseService.listCourses(pageNum, pageSize, id, keyword, null, null, null, status, null);
            return Result.ok(courses);
        } catch (Exception e) {
            return Result.error("获取教师课程失败：" + e.getMessage());
        }
    }

    /**
     * 获取教师的学生列表  
     */
    @GetMapping("/{id}/students")
    public Result<PageVO<StudentVO>> getTeacherStudents(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Long pageNum,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long courseId) {
        try {
            // TODO: 实现获取教师所有学生的逻辑
            // 这里需要根据教师ID查询其所有课程的学生
            return Result.error("功能开发中");
        } catch (Exception e) {
            return Result.error("获取教师学生失败：" + e.getMessage());
        }
    }

    /**
     * 获取教师的任务列表
     */
    @GetMapping("/{id}/tasks")
    public Result<PageVO<TeacherTaskVO>> getTeacherTasks(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Boolean published) {
        try {
            PageVO<TeacherTaskVO> tasks = taskService.getTeacherTasks(id, current, size, courseId, type, published);
            return Result.ok(tasks);
        } catch (Exception e) {
            return Result.error("获取教师任务失败：" + e.getMessage());
        }
    }

    /**
     * 获取教师的课程选项列表（用于下拉选择）
     */
    @GetMapping("/{id}/courses/options")
    public Result<List<SimpleCourseVO>> getTeacherCourseOptions(@PathVariable Long id) {
        try {
            List<SimpleCourseVO> options = courseService.getTeacherCourseOptions(id);
            return Result.ok(options);
        } catch (Exception e) {
            return Result.error("获取教师课程选项失败：" + e.getMessage());
        }
    }

    /**
     * 状态更新请求体
     */
    public static class StatusUpdateRequest {
        private String status;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}