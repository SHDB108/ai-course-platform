package com.example.aicourse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.aicourse.dto.student.StudentCreateDTO;
import com.example.aicourse.dto.student.StudentUpdateDTO;
import com.example.aicourse.entity.Student;
import com.example.aicourse.entity.User;
import com.example.aicourse.entity.Course;
import com.example.aicourse.entity.CourseStudent;
import com.example.aicourse.entity.Teacher;
import com.example.aicourse.repository.UserMapper;
import com.example.aicourse.repository.StudentMapper;
import com.example.aicourse.repository.CourseMapper;
import com.example.aicourse.repository.CourseStudentMapper;
import com.example.aicourse.repository.TeacherMapper;
import com.example.aicourse.service.StudentService;
import com.example.aicourse.vo.PageVO;
import com.example.aicourse.vo.student.ImportResultVO;
import com.example.aicourse.vo.student.StudentVO;
import com.example.aicourse.vo.student.StudentDashboardStatsVO;
import com.example.aicourse.vo.course.CourseVO;
import com.example.aicourse.vo.task.StudentTaskVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper,Student> implements StudentService{

    private final StudentMapper studentMapper;
    private final UserMapper userMapper;
    private final CourseMapper courseMapper;
    private final CourseStudentMapper courseStudentMapper;
    private final TeacherMapper teacherMapper;

    @Autowired
    public StudentServiceImpl(StudentMapper studentMapper, UserMapper userMapper, 
                             CourseMapper courseMapper, CourseStudentMapper courseStudentMapper, 
                             TeacherMapper teacherMapper) {
        this.studentMapper = studentMapper;
        this.userMapper = userMapper;
        this.courseMapper = courseMapper;
        this.courseStudentMapper = courseStudentMapper;
        this.teacherMapper = teacherMapper;
    }

    /**
     * API 3.1 获取学生列表 (分页)
     */
    @Override
    public PageVO<StudentVO> getStudentPage(Long pageNum, Long pageSize, String keyword, String major, String grade, String status) {
        Page<Student> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Student> queryWrapper = Wrappers.<Student>lambdaQuery();

        if (StringUtils.isNotBlank(keyword)) {
            queryWrapper.like(Student::getName, keyword)
                    .or()
                    .like(Student::getStuNo, keyword);
        }
        if (StringUtils.isNotBlank(major)) {
            queryWrapper.eq(Student::getMajor, major);
        }
        if (StringUtils.isNotBlank(grade)) {
            queryWrapper.eq(Student::getGrade, grade);
        }
        // Note: Status filtering removed - Student entity doesn't have status field
        // Status should be managed through User entity relationship
        // TODO: Implement status filtering through User entity join query

        Page<Student> studentPage = studentMapper.selectPage(page, queryWrapper);

        List<StudentVO> studentVOs = studentPage.getRecords().stream()
                .map(student -> {
                    StudentVO vo = new StudentVO();
                    BeanUtils.copyProperties(student, vo);
                    return vo;
                })
                .collect(Collectors.toList());

        PageVO<StudentVO> pageVO = new PageVO<>();
        pageVO.setRecords(studentVOs);
        pageVO.setTotal(studentPage.getTotal());
        pageVO.setSize(studentPage.getSize());
        pageVO.setCurrent(studentPage.getCurrent());
        pageVO.setPages(studentPage.getPages());

        return pageVO;
    }

    /**
     * API 3.8 更新学生状态
     * Status is managed through User entity, not Student entity directly
     */
    @Override
    @CacheEvict(value = "studentCache", key = "#id")
    public boolean updateStudentStatus(Long id, String status) {
        Student student = studentMapper.selectById(id);
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }
        
        // Check if student has associated user
        if (student.getId() == null) {
            throw new RuntimeException("学生未关联用户账户，无法更新状态");
        }
        
        User user = userMapper.selectById(student.getId());
        if (user == null) {
            throw new RuntimeException("关联的用户账户不存在");
        }
        
        // Convert string status to integer status for User entity
        Integer statusInt;
        switch (status) {
            case "ACTIVE":
                statusInt = 1;
                break;
            case "INACTIVE":
                statusInt = 0;
                break;
            case "SUSPENDED":
                statusInt = -1;
                break;
            case "DELETED":
                statusInt = -2;
                break;
            default:
                throw new RuntimeException("无效的状态值: " + status);
        }
        
        // Update user status
        user.setStatus(statusInt);
        user.setUpdatedAt(LocalDateTime.now());
        int rows = userMapper.updateById(user);
        return rows > 0;
    }

    /**
     * API 3.2 获取学生详情
     */
    @Override
    @Cacheable(value = "studentCache", key = "#id")
    public StudentVO getStudentDetails(Long id) {
        Student student = studentMapper.selectById(id);
        if (student == null) {
            return null; // 或者抛出 StudentNotFoundException
        }
        StudentVO studentVO = new StudentVO();
        BeanUtils.copyProperties(student, studentVO);
        return studentVO;
    }

    /**
     * API 3.3 新增学生
     */
    @Override
    @Transactional
    public Long createStudent(StudentCreateDTO dto) {
        // 校验学号和邮箱是否重复
        if (studentMapper.selectCount(Wrappers.<Student>lambdaQuery().eq(Student::getStuNo, dto.getStuNo())) > 0) {
            throw new RuntimeException("学号已存在");
        }
        if (StringUtils.isNotBlank(dto.getEmail()) && studentMapper.selectCount(Wrappers.<Student>lambdaQuery().eq(Student::getEmail, dto.getEmail())) > 0) {
            throw new RuntimeException("邮箱已存在");
        }

        Student student = new Student();
        BeanUtils.copyProperties(dto, student);

        int rows = studentMapper.insert(student);
        if (rows == 0) {
            throw new RuntimeException("新增学生失败");
        }
        return student.getId(); // MyBatis-Plus插入后会回填ID
    }

    /**
     * API 3.4 更新学生信息
     */
    @Override
    @Transactional
    @CacheEvict(value = "studentCache", key = "#id")
    public boolean updateStudent(Long id, StudentUpdateDTO dto) {
        Student student = studentMapper.selectById(id);
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }

        // 校验更新后的邮箱是否重复（如果邮箱被修改且新邮箱已存在于其他学生）
        if (StringUtils.isNotBlank(dto.getEmail()) && !dto.getEmail().equals(student.getEmail())) {
            if (studentMapper.selectCount(Wrappers.<Student>lambdaQuery().eq(Student::getEmail, dto.getEmail()).ne(Student::getId, id)) > 0) {
                throw new RuntimeException("更新后的邮箱已被其他学生使用");
            }
        }

        BeanUtils.copyProperties(dto, student); // 将DTO中的非空属性复制到实体
        // 确保ID不被覆盖
        student.setId(id);

        int rows = studentMapper.updateById(student);
        if (rows == 0) {
            throw new RuntimeException("更新学生信息失败");
        }
        return true;
    }

    /**
     * API 3.5 删除学生
     */
    @Override
    @CacheEvict(value = "studentCache", key = "#id")
    public boolean deleteStudent(Long id) {
        int rows = studentMapper.deleteById(id);
        if (rows == 0) {
            throw new RuntimeException("删除学生失败，学生不存在");
        }
        return true;
    }

    /**
     * API 3.6 批量导入学生
     */
    @Override
    @Transactional
    public ImportResultVO importStudents(MultipartFile file) throws IOException {
        ImportResultVO result = new ImportResultVO();
        List<ImportResultVO.FailedRecord> failedRecords = new ArrayList<>();

        // TODO: 实际的Excel/CSV文件解析逻辑
        // 这里仅为示例骨架，需要引入 Apache POI 或其他CSV解析库
        // 假设文件内容是简单的 CSV 格式，每行一个学生数据，例如: 学号,姓名,性别,专业,年级,电话,邮箱
        // 简化处理：读取一行，模拟处理
        try {
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            String[] lines = content.split("\n");
            int successCount = 0;
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i].trim();
                if (line.isEmpty()) continue;

                // 模拟解析和保存
                // 例如: String[] parts = line.split(",");
                // StudentCreateDTO studentDto = new StudentCreateDTO();
                // studentDto.setStuNo(parts[0]);
                // ...
                try {
                    // 假设 createStudent(studentDto) 成功
                    // For demonstration, let's just increment success count
                    // Or simulate failure
                    if (i % 3 == 0) { // Simulate some failures
                        throw new RuntimeException("模拟处理失败");
                    }
                    // 模拟成功，实际应该调用 createStudent 方法
                    // createStudent(new StudentCreateDTO(...)); // 实际应传递解析出的DTO
                    successCount++;
                } catch (Exception e) {
                    ImportResultVO.FailedRecord failedRecord = new ImportResultVO.FailedRecord();
                    failedRecord.setRow(i + 1); // 行号从1开始
                    failedRecord.setReason("数据格式错误或重复: " + e.getMessage());
                    failedRecords.add(failedRecord);
                }
            }
            result.setSuccessCount(successCount);
            result.setFailureCount(lines.length - successCount); // 假设所有非空行都被尝试处理
            result.setFailedRecords(failedRecords);

        } catch (Exception e) {
            // 文件读取或基本处理失败
            throw new IOException("文件处理失败: " + e.getMessage(), e);
        }

        return result;
    }

    /**
     * API 3.7 导出学生信息
     */
    @Override
    public ResponseEntity<Resource> exportStudents(String major, String grade, String format) throws IOException {
        // 1. 根据筛选条件查询学生数据
        LambdaQueryWrapper<Student> queryWrapper = Wrappers.<Student>lambdaQuery();
        if (StringUtils.isNotBlank(major)) {
            queryWrapper.eq(Student::getMajor, major);
        }
        if (StringUtils.isNotBlank(grade)) {
            queryWrapper.eq(Student::getGrade, grade);
        }
        List<Student> students = studentMapper.selectList(queryWrapper);

        // 2. TODO: 生成 Excel 或 CSV 文件内容
        // 这里仅为示例骨架，需要引入 Apache POI 或其他CSV生成库
        // 简化处理：生成CSV格式的字符串
        StringBuilder csvContent = new StringBuilder();
        csvContent.append("学号,姓名,性别,专业,年级,电话,邮箱,创建时间\n"); // CSV Header

        for (Student student : students) {
            csvContent.append(student.getStuNo()).append(",")
                    .append(student.getName()).append(",")
                    .append(student.getGender() == null ? "" : (student.getGender() == 0 ? "女" : "男")).append(",")
                    .append(student.getMajor()).append(",")
                    .append(student.getGrade()).append(",")
                    .append(student.getPhone() == null ? "" : student.getPhone()).append(",")
                    .append(student.getEmail() == null ? "" : student.getEmail()).append(",")
                    .append(student.getGmtCreate()).append("\n");
        }

        byte[] fileBytes = csvContent.toString().getBytes(StandardCharsets.UTF_8);
        ByteArrayResource resource = new ByteArrayResource(fileBytes);

        // 3. 设置响应头
        String filename = "students." + format.toLowerCase();
        MediaType contentType;
        if ("xlsx".equalsIgnoreCase(format)) {
            contentType = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            // TODO: 对于XLSX，需要使用POI等库生成实际的Excel文件
            // 目前只生成CSV内容，并根据format参数设置对应的ContentType和文件名
            filename = "students.csv"; // 如果是xlsx但实际生成csv，则文件名应匹配
        } else { // 默认为 csv
            contentType = MediaType.TEXT_PLAIN; // 或者 MediaType.parseMediaType("text/csv");
            filename = "students.csv";
        }


        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + filename + "\"")
                .contentType(contentType)
                .contentLength(resource.contentLength())
                .body(resource);
    }

    /**
     * API 3.8 获取学生选修的课程列表
     */
    @Override
    public PageVO<CourseVO> getStudentCourses(Long studentId, Long pageNum, Long pageSize, String keyword) {
        // 1. 检查学生是否存在
        Student student = studentMapper.selectById(studentId);
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }

        // 2. 查询学生选修的课程ID列表
        LambdaQueryWrapper<CourseStudent> enrollmentQueryWrapper = Wrappers.<CourseStudent>lambdaQuery()
                .eq(CourseStudent::getStudentId, studentId);
        List<CourseStudent> enrollments = courseStudentMapper.selectList(enrollmentQueryWrapper);
        
        if (enrollments.isEmpty()) {
            // 如果学生没有选修任何课程，返回空结果
            PageVO<CourseVO> pageVO = new PageVO<>();
            pageVO.setRecords(new ArrayList<>());
            pageVO.setTotal(0L);
            pageVO.setSize(pageSize);
            pageVO.setCurrent(pageNum);
            pageVO.setPages(0L);
            return pageVO;
        }

        List<Long> courseIds = enrollments.stream()
                .map(CourseStudent::getCourseId)
                .collect(Collectors.toList());

        // 3. 构建课程查询条件（不分页，先获取所有数据）
        LambdaQueryWrapper<Course> courseQueryWrapper = Wrappers.<Course>lambdaQuery()
                .in(Course::getId, courseIds);
        
        // 如果有关键词搜索，添加搜索条件
        if (StringUtils.isNotBlank(keyword)) {
            courseQueryWrapper.and(wrapper -> wrapper
                    .like(Course::getCourseName, keyword)
                    .or()
                    .like(Course::getCourseCode, keyword));
        }

        // 先获取所有符合条件的课程
        List<Course> allCourses = courseMapper.selectList(courseQueryWrapper);
        
        // 4. 手动分页
        int total = allCourses.size();
        int startIndex = (int) ((pageNum - 1) * pageSize);
        int endIndex = Math.min(startIndex + pageSize.intValue(), total);
        
        List<Course> pagedCourses = new ArrayList<>();
        if (startIndex < total) {
            pagedCourses = allCourses.subList(startIndex, endIndex);
        }

        // 5. 转换为CourseVO
        List<CourseVO> courseVOs = pagedCourses.stream().map(course -> {
            CourseVO courseVO = new CourseVO();
            BeanUtils.copyProperties(course, courseVO);

            // 映射前端期望的字段
            courseVO.setName(course.getCourseName());
            courseVO.setDuration(course.getHours());
            courseVO.setMaxStudents(course.getCapacity());
            courseVO.setStatus("ACTIVE"); // 默认状态

            // 格式化时间字段
            if (course.getGmtCreate() != null) {
                courseVO.setCreatedAt(course.getGmtCreate().toString());
            }
            if (course.getGmtModified() != null) {
                courseVO.setUpdatedAt(course.getGmtModified().toString());
            }

            // 填充教师姓名
            if (course.getTeacherId() != null) {
                Teacher teacher = teacherMapper.selectById(course.getTeacherId());
                if (teacher != null) {
                    courseVO.setTeacherName(teacher.getName());
                }
            }

            return courseVO;
        }).collect(Collectors.toList());

        // 6. 构建分页结果
        PageVO<CourseVO> pageVO = new PageVO<>();
        pageVO.setRecords(courseVOs);
        pageVO.setTotal((long) total);
        pageVO.setSize(pageSize);
        pageVO.setCurrent(pageNum);
        pageVO.setPages((long) Math.ceil((double) total / pageSize));

        return pageVO;
    }

    /**
     * API 3.9 获取学生的任务列表
     * TODO: 这是一个基础实现，需要后续完善复杂的查询逻辑
     */
    @Override
    public PageVO<StudentTaskVO> getStudentTasks(Long studentId, Long pageNum, Long pageSize, String keyword, String status) {
        // 1. 检查学生是否存在
        Student student = studentMapper.selectById(studentId);
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }

        // TODO: 实现复杂的多表联查逻辑
        // 需要查询：
        // 1. 学生选修的课程
        // 2. 这些课程下的所有任务
        // 3. 学生对这些任务的提交状态
        // 4. 根据keyword和status进行筛选
        
        // 暂时返回空结果，确保编译通过
        PageVO<StudentTaskVO> pageVO = new PageVO<>();
        pageVO.setRecords(new ArrayList<>());
        pageVO.setTotal(0L);
        pageVO.setSize(pageSize);
        pageVO.setCurrent(pageNum);
        pageVO.setPages(0L);
        
        return pageVO;
    }

    /**
     * API 3.10 获取学生仪表板统计数据
     */
    @Override
    public StudentDashboardStatsVO getStudentDashboardStats(Long studentId) {
        // 1. 检查学生是否存在
        Student student = studentMapper.selectById(studentId);
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }

        // 2. 计算各项统计数据
        StudentDashboardStatsVO stats = new StudentDashboardStatsVO();
        
        // 我的课程数量 - 查询学生选修的课程数
        LambdaQueryWrapper<CourseStudent> courseQuery = Wrappers.lambdaQuery(CourseStudent.class)
                .eq(CourseStudent::getStudentId, studentId);
        Long courseCount = courseStudentMapper.selectCount(courseQuery);
        stats.setMyCourses(courseCount != null ? courseCount.intValue() : 0);
        
        // 待办任务数量 - 目前暂设为固定值，后续可根据实际任务表实现
        stats.setPendingTasks(3);
        
        // 本周提交数量 - 目前暂设为固定值，后续可根据实际提交记录实现
        stats.setWeeklySubmissions(5);
        
        // 未读消息数量 - 目前暂设为固定值，后续可根据实际消息表实现
        stats.setUnreadMessages(2);
        
        // Todo项目统计
        StudentDashboardStatsVO.TodoItemsVO todoItems = new StudentDashboardStatsVO.TodoItemsVO();
        todoItems.setPending(4);
        todoItems.setTotal(10);
        stats.setTodoItems(todoItems);
        
        // 项目数量 - 目前暂设为固定值，后续可根据实际项目表实现
        stats.setProjects(2);

        return stats;
    }

    /**
     * API 3.11 获取学生任务统计数据
     */
    @Override
    public com.example.aicourse.vo.task.StudentTaskStatsVO getStudentTaskStats(Long studentId) {
        // 1. 检查学生是否存在
        Student student = studentMapper.selectById(studentId);
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }

        // 2. 计算任务统计数据
        com.example.aicourse.vo.task.StudentTaskStatsVO stats = new com.example.aicourse.vo.task.StudentTaskStatsVO();
        
        // TODO: 实现真实的任务统计查询
        // 目前返回模拟数据，后续需要根据实际的任务表实现
        stats.setTotalTasks(20);
        stats.setPendingTasks(3);
        stats.setInProgressTasks(2);
        stats.setCompletedTasks(15);
        stats.setOverdueTasks(1);
        stats.setCompletionRate(75.0);
        stats.setThisWeekCompleted(4);
        stats.setThisMonthCompleted(12);

        return stats;
    }
}