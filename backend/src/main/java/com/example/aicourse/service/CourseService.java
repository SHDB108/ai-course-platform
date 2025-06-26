package com.example.aicourse.service;

import com.example.aicourse.dto.course.CourseCreateDTO;
import com.example.aicourse.dto.course.CourseScheduleDTO;
import com.example.aicourse.dto.course.CourseUpdateDTO;
import com.example.aicourse.vo.PageVO;
import com.example.aicourse.vo.course.CourseVO;
import com.example.aicourse.vo.student.StudentVO; // 导入 StudentVO

import java.util.List;

public interface CourseService {

    // 修改 listCourses 方法签名，新增 teacherId 和 keyword 参数，并保留其他参数
    PageVO<CourseVO> listCourses(Long pageNum, Long pageSize, Long teacherId, String keyword, String semester, Integer credits, String department);

    // 保持原有的方法
    CourseVO getCourseDetail(Long id);

    Long createCourse(CourseCreateDTO dto);

    boolean updateCourse(Long id, CourseUpdateDTO dto);

    boolean deleteCourse(Long id);

    boolean scheduleCourse(Long id, CourseScheduleDTO dto);

    // 新增：API 4.6 学生选课
    boolean enrollStudent(Long courseId, Long studentId);

    // 新增：API 4.7 学生退课
    boolean unenrollStudent(Long courseId, Long studentId);

    // 新增：API 4.8 获取课程的学生列表
    PageVO<StudentVO> getEnrolledStudents(Long courseId, Long pageNum, Long pageSize, String keyword);
}