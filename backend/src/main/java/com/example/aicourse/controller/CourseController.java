package com.example.aicourse.controller;
import jakarta.validation.Valid;
import com.example.aicourse.dto.course.CourseCreateDTO;
import com.example.aicourse.dto.course.CourseScheduleDTO;
import com.example.aicourse.dto.course.CourseUpdateDTO;
import com.example.aicourse.service.CourseService;
import com.example.aicourse.utils.Result;
import com.example.aicourse.vo.PageVO;
import com.example.aicourse.vo.course.CourseVO;
import com.example.aicourse.vo.student.StudentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@RestController
@RequestMapping("/api/v1/courses")
public class CourseController {

    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    /**
     * API 4.1 获取课程列表 (分页)
     * 功能描述: 分页查询课程信息，支持按课程名称、编码、教师ID和教师姓名进行模糊搜索。
     * @param pageNum 当前页码
     * @param pageSize 每页数量
     * @param teacherId 按授课教师ID筛选 (新增)
     * @param keyword 搜索关键词（匹配课程名称、编码、教师姓名） (修改)
     * @param semester 学期筛选 (保留现有功能)
     * @param credits 学分筛选 (保留现有功能)
     * @param department 学院筛选 (保留现有功能)
     * @return 分页课程列表
     */
    @GetMapping
    public Result<PageVO<CourseVO>> listCourses(
            @RequestParam(defaultValue = "1") Long pageNum,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) Long teacherId, // 新增参数
            @RequestParam(required = false) String keyword, // 新增参数
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) Integer credits,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String status, // 新增状态筛选
            @RequestParam(required = false) Long categoryId) { // 新增分类筛选
        try {
            PageVO<CourseVO> courses = courseService.listCourses(pageNum, pageSize, teacherId, keyword, semester, credits, department, status, categoryId);
            return Result.ok(courses);
        } catch (RuntimeException e) {
            return Result.error("获取课程列表失败: " + e.getMessage());
        }
    }

    /**
     * API 4.2 获取课程详情
     * @param id 课程ID
     * @return 课程详细信息
     */
    @GetMapping("/{id}")
    public Result<CourseVO> getCourseDetail(@PathVariable Long id) {
        try {
            CourseVO course = courseService.getCourseDetail(id);
            if (course == null) {
                return Result.error("课程不存在");
            }
            return Result.ok(course);
        } catch (RuntimeException e) {
            return Result.error("获取课程详情失败: " + e.getMessage());
        }
    }

    /**
     * API 4.3 创建新课程
     * @param dto 课程创建请求DTO
     * @return 新创建的课程ID
     */
    @PostMapping
    public Result<Long> createCourse(@RequestBody @Valid CourseCreateDTO dto) {
        try {
            Long courseId = courseService.createCourse(dto);
            return Result.ok(courseId);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * API 4.4 更新课程信息
     * @param id 课程ID
     * @param dto 课程更新请求DTO
     * @return null
     */
    @PutMapping("/{id}")
    public Result<Void> updateCourse(@PathVariable Long id, @RequestBody @Valid CourseUpdateDTO dto) {
        try {
            boolean success = courseService.updateCourse(id, dto);
            if (!success) {
                return Result.error("更新课程失败或课程不存在");
            }
            return Result.ok();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * API 4.5 删除课程
     * @param id 课程ID
     * @return null
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteCourse(@PathVariable Long id) {
        try {
            boolean success = courseService.deleteCourse(id);
            if (!success) {
                return Result.error("删除课程失败或课程不存在");
            }
            return Result.ok();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * API 4.6 课程排课 (保留原有功能)
     * @param id 课程ID
     * @param dto 排课信息DTO
     * @return null
     */
    @PutMapping("/{id}/schedule") // 注意：原始代码中使用的是PUT方法，这里保持一致
    public Result<Void> scheduleCourse(@PathVariable Long id, @RequestBody @Valid CourseScheduleDTO dto) {
        try {
            boolean success = courseService.scheduleCourse(id, dto);
            if (!success) {
                return Result.error("排课失败或课程不存在");
            }
            return Result.ok();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * API 4.9 更新课程状态
     * @param id 课程ID
     * @param status 新状态
     * @return null
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateCourseStatus(@PathVariable Long id, @RequestBody StatusUpdateRequest request) {
        try {
            boolean success = courseService.updateCourseStatus(id, request.getStatus());
            if (!success) {
                return Result.error("更新课程状态失败或课程不存在");
            }
            return Result.ok();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
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

    /**
     * 新增：API 4.6 学生选课 (与文档API序号冲突，但功能对应)
     * 功能描述: 学生选择一门课程。
     * HTTP 方法: POST
     * URL 路径: /courses/{courseId}/students/{studentId}
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @return null
     */
    @PostMapping("/{courseId}/students/{studentId}")
    public Result<Void> enrollStudent(@PathVariable Long courseId, @PathVariable Long studentId) {
        try {
            boolean success = courseService.enrollStudent(courseId, studentId);
            if (!success) {
                return Result.error("学生选课失败");
            }
            return Result.ok();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 新增：API 4.7 学生退课
     * 功能描述: 学生退出一门课程。
     * HTTP 方法: DELETE
     * URL 路径: /courses/{courseId}/students/{studentId}
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @return null
     */
    @DeleteMapping("/{courseId}/students/{studentId}")
    public Result<Void> unenrollStudent(@PathVariable Long courseId, @PathVariable Long studentId) {
        try {
            boolean success = courseService.unenrollStudent(courseId, studentId);
            if (!success) {
                return Result.error("学生退课失败");
            }
            return Result.ok();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 新增：API 4.8 获取课程的学生列表
     * 功能描述: 获取选修某门课程的所有学生列表，支持按关键词（姓名或学号）搜索。
     * HTTP 方法: GET
     * URL 路径: /courses/{courseId}/students
     * @param courseId 课程ID
     * @param pageNum 当前页码
     * @param pageSize 每页数量
     * @param keyword 搜索关键词（匹配学生姓名或学号）
     * @return 分页学生列表
     */
    @GetMapping("/{courseId}/students")
    public Result<PageVO<StudentVO>> getEnrolledStudents(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "1") Long pageNum,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) String keyword) { // 关键词可以匹配学生姓名或学号
        try {
            PageVO<StudentVO> students = courseService.getEnrolledStudents(courseId, pageNum, pageSize, keyword);
            return Result.ok(students);
        } catch (RuntimeException e) {
            return Result.error("获取课程学生列表失败: " + e.getMessage());
        }
    }
}