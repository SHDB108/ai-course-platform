package com.example.aicourse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.aicourse.dto.course.CourseCreateDTO;
import com.example.aicourse.dto.course.CourseScheduleDTO;
import com.example.aicourse.dto.course.CourseUpdateDTO;
import com.example.aicourse.entity.Course;
import com.example.aicourse.entity.CourseStudent; // 导入 CourseStudent
import com.example.aicourse.entity.Student;     // 导入 Student
import com.example.aicourse.entity.Teacher;
import com.example.aicourse.entity.User;         // 导入 User
import com.example.aicourse.repository.CourseMapper;
import com.example.aicourse.repository.CourseStudentMapper; // 导入 CourseStudentMapper
import com.example.aicourse.repository.StudentMapper;     // 导入 StudentMapper
import com.example.aicourse.repository.TeacherMapper;
import com.example.aicourse.repository.UserMapper;         // 导入 UserMapper
import com.example.aicourse.service.CourseService;
import com.example.aicourse.vo.PageVO;
import com.example.aicourse.vo.course.CourseVO;
import com.example.aicourse.vo.course.SimpleCourseVO;
import com.example.aicourse.vo.student.StudentVO; // 导入 StudentVO
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime; // 导入 LocalDateTime
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseMapper courseMapper;
    private final TeacherMapper teacherMapper;
    private final CourseStudentMapper courseStudentMapper;
    private final StudentMapper studentMapper;
    private final UserMapper userMapper;

    @Autowired
    public CourseServiceImpl(CourseMapper courseMapper, TeacherMapper teacherMapper, CourseStudentMapper courseStudentMapper, StudentMapper studentMapper, UserMapper userMapper) {
        this.courseMapper = courseMapper;
        this.teacherMapper = teacherMapper;
        this.courseStudentMapper = courseStudentMapper;
        this.studentMapper = studentMapper;
        this.userMapper = userMapper;
    }

    @Override
    public PageVO<CourseVO> listCourses(Long pageNum, Long pageSize, Long teacherId, String keyword, String semester, Integer credits, String department, String status, Long categoryId) {
        Page<Course> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Course> queryWrapper = Wrappers.<Course>lambdaQuery();

        // 根据 teacherId 筛选
        if (teacherId != null) {
            queryWrapper.eq(Course::getTeacherId, teacherId);
        }

        // 根据 keyword 筛选 (课程名称、课程编码、教师姓名)
        if (StringUtils.isNotBlank(keyword)) {
            queryWrapper.and(wrapper -> {
                wrapper.like(Course::getCourseName, keyword)
                        .or()
                        .like(Course::getCourseCode, keyword);

                // 模糊搜索教师姓名
                LambdaQueryWrapper<Teacher> teacherQuery = Wrappers.<Teacher>lambdaQuery()
                        .like(Teacher::getName, keyword);
                List<Teacher> matchedTeachers = teacherMapper.selectList(teacherQuery);
                if (!matchedTeachers.isEmpty()) {
                    List<Long> matchedTeacherIds = matchedTeachers.stream()
                            .map(Teacher::getId)
                            .collect(Collectors.toList());
                    wrapper.or().in(Course::getTeacherId, matchedTeacherIds);
                }
            });
        }

        // 根据学期筛选 (原API 4.1文档中未提及，但保留了代码中的现有功能)
        if (StringUtils.isNotBlank(semester)) {
            queryWrapper.eq(Course::getSemester, semester);
        }
        // 根据学分筛选 (原API 4.1文档中未提及，但保留了代码中的现有功能)
        if (credits != null) {
            queryWrapper.eq(Course::getCredits, credits);
        }
        // 根据院系筛选 (原API 4.1文档中未提及，但保留了代码中的现有功能)
        if (StringUtils.isNotBlank(department)) {
            queryWrapper.like(Course::getDepartment, department);
        }
        // 状态筛选已移除 - Course实体不再包含status字段
        // 根据分类筛选
        if (categoryId != null) {
            queryWrapper.eq(Course::getCategoryId, categoryId);
        }

        IPage<Course> coursePage = courseMapper.selectPage(page, queryWrapper);

        List<CourseVO> courseVOs = coursePage.getRecords().stream().map(course -> {
            CourseVO courseVO = new CourseVO();
            BeanUtils.copyProperties(course, courseVO);

            // 映射前端期望的字段
            courseVO.setName(course.getCourseName()); // courseName -> name
            courseVO.setDuration(course.getHours()); // hours -> duration
            courseVO.setMaxStudents(course.getCapacity()); // capacity -> maxStudents
            
            // 设置默认状态 - Course实体不再包含status字段
            courseVO.setStatus("ACTIVE");
            
            // 格式化时间字段（如果需要）
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

        PageVO<CourseVO> pageVO = new PageVO<>();
        pageVO.setRecords(courseVOs);
        pageVO.setTotal(coursePage.getTotal());
        pageVO.setSize(coursePage.getSize());
        pageVO.setCurrent(coursePage.getCurrent());

        return pageVO;
    }

    @Override
    @Cacheable(value = "courseCache", key = "#id")
    public CourseVO getCourseDetail(Long id) {
        Course course = courseMapper.selectById(id);
        if (course == null) {
            return null;
        }
        CourseVO courseVO = new CourseVO();
        BeanUtils.copyProperties(course, courseVO);

        // 映射前端期望的字段
        courseVO.setName(course.getCourseName()); // courseName -> name
        courseVO.setDuration(course.getHours()); // hours -> duration
        courseVO.setMaxStudents(course.getCapacity()); // capacity -> maxStudents
        
        // 设置默认状态 - Course实体不再包含status字段
        courseVO.setStatus("ACTIVE");
        
        // 格式化时间字段（如果需要）
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
    }

    @Override
    @Transactional
    public Long createCourse(CourseCreateDTO dto) {
        // 检查教师是否存在（如果指定了教师ID）
        if (dto.getTeacherId() != null) {
            Teacher teacher = teacherMapper.selectById(dto.getTeacherId());
            if (teacher == null) {
                throw new RuntimeException("指定的教师不存在");
            }
        }

        // 检查课程名称是否重复
        if (courseMapper.selectCount(Wrappers.<Course>lambdaQuery().eq(Course::getCourseName, dto.getName())) > 0) {
            throw new RuntimeException("课程名称已存在");
        }

        Course course = new Course();
        // 生成课程编码：使用时间戳
        String courseCode = "COURSE_" + System.currentTimeMillis();
        course.setCourseCode(courseCode);
        course.setCourseName(dto.getName());
        course.setDescription(dto.getDescription());
        course.setCredits(dto.getCredits());
        course.setHours(dto.getDuration());
        course.setTeacherId(dto.getTeacherId());
        course.setSemester(dto.getSemester());
        course.setDepartment(dto.getDepartment());
        course.setCapacity(dto.getMaxStudents());
        course.setEnrolledStudents(0);
        // 设置分类 - status字段已移除
        course.setCategoryId(dto.getCategoryId());
        courseMapper.insert(course);
        return course.getId();
    }

    @Override
    @Transactional
    @CacheEvict(value = "courseCache", key = "#id")
    public boolean updateCourse(Long id, CourseUpdateDTO dto) {
        Course course = courseMapper.selectById(id);
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }

        // 检查教师是否存在（如果更新了教师ID）
        if (dto.getTeacherId() != null && !dto.getTeacherId().equals(course.getTeacherId())) {
            Teacher teacher = teacherMapper.selectById(dto.getTeacherId());
            if (teacher == null) {
                throw new RuntimeException("指定的教师不存在");
            }
        }

        // 检查课程编码是否重复（如果更新了课程编码且与现有不同）
        if (StringUtils.isNotBlank(dto.getCourseCode()) && !dto.getCourseCode().equals(course.getCourseCode())) {
            if (courseMapper.selectCount(Wrappers.<Course>lambdaQuery().eq(Course::getCourseCode, dto.getCourseCode())) > 0) {
                throw new RuntimeException("课程编码已存在");
            }
        }

        // 手动映射字段以匹配前端DTO字段名
        if (dto.getName() != null) {
            course.setCourseName(dto.getName());
        }
        if (dto.getDuration() != null) {
            course.setHours(dto.getDuration());
        }
        if (dto.getMaxStudents() != null) {
            course.setCapacity(dto.getMaxStudents());
        }
        
        // 复制其他匹配的字段
        if (dto.getCourseCode() != null) {
            course.setCourseCode(dto.getCourseCode());
        }
        if (dto.getDescription() != null) {
            course.setDescription(dto.getDescription());
        }
        if (dto.getCredits() != null) {
            course.setCredits(dto.getCredits());
        }
        if (dto.getSemester() != null) {
            course.setSemester(dto.getSemester());
        }
        if (dto.getDepartment() != null) {
            course.setDepartment(dto.getDepartment());
        }
        if (dto.getTeacherId() != null) {
            course.setTeacherId(dto.getTeacherId());
        }
        if (dto.getClassroom() != null) {
            course.setClassroom(dto.getClassroom());
        }
        if (dto.getScheduleTime() != null) {
            course.setScheduleTime(dto.getScheduleTime());
        }
        // status字段更新已移除 - Course实体不再包含此字段
        if (dto.getCategoryId() != null) {
            course.setCategoryId(dto.getCategoryId());
        }
        int rows = courseMapper.updateById(course);
        return rows > 0;
    }

    @Override
    @Transactional
    @CacheEvict(value = "courseCache", key = "#id")
    public boolean deleteCourse(Long id) {
        Course course = courseMapper.selectById(id);
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }

        // 删除课程时，同时删除所有相关的学生选课记录
        courseStudentMapper.delete(Wrappers.<CourseStudent>lambdaQuery().eq(CourseStudent::getCourseId, id));

        int rows = courseMapper.deleteById(id);
        return rows > 0;
    }

    @Override
    @Transactional
    @CacheEvict(value = "courseCache", key = "#id")
    public boolean scheduleCourse(Long id, CourseScheduleDTO dto) {
        Course course = courseMapper.selectById(id);
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }
        BeanUtils.copyProperties(dto, course);
        int rows = courseMapper.updateById(course);
        return rows > 0;
    }

    @Override
    @Transactional
    @CacheEvict(value = "courseCache", key = "#id")
    public boolean updateCourseStatus(Long id, String status) {
        // 状态更新功能已禁用 - Course实体不再包含status字段
        // 返回true表示操作"成功"，但实际上不执行任何数据库操作
        Course course = courseMapper.selectById(id);
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }
        // 仅更新修改时间以保持接口兼容性
        course.setGmtModified(LocalDateTime.now());
        int rows = courseMapper.updateById(course);
        return rows > 0;
    }

    /**
     * 新增：API 4.6 学生选课
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @return 选课成功返回 true
     */
    @Override
    @Transactional
    public boolean enrollStudent(Long courseId, Long studentId) {
        // 1. 检查课程是否存在
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }

        // 2. 检查学生是否存在且角色为STUDENT
        // 这里的 studentId 应该同时对应 User 和 Student 实体的主键ID
        User studentUser = userMapper.selectById(studentId);
        if (studentUser == null || !"STUDENT".equals(studentUser.getRole())) {
            throw new RuntimeException("学生不存在或ID不是学生角色");
        }
        // 确保Student表中也有记录
        Student studentDetail = studentMapper.selectById(studentId);
        if (studentDetail == null) {
            throw new RuntimeException("学生详细信息不存在");
        }


        // 3. 检查是否已经选过该课程，避免重复选课
        LambdaQueryWrapper<CourseStudent> queryWrapper = Wrappers.<CourseStudent>lambdaQuery()
                .eq(CourseStudent::getCourseId, courseId)
                .eq(CourseStudent::getStudentId, studentId);
        if (courseStudentMapper.selectCount(queryWrapper) > 0) {
            throw new RuntimeException("学生已选过该课程，请勿重复选课");
        }

        // 4. 创建选课记录
        CourseStudent courseStudent = new CourseStudent();
        courseStudent.setCourseId(courseId);
        courseStudent.setStudentId(studentId);
        courseStudent.setEnrollmentDate(LocalDateTime.now()); // 记录选课时间

        int rows = courseStudentMapper.insert(courseStudent);
        return rows > 0;
    }

    /**
     * 新增：API 4.7 学生退课
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @return 退课成功返回 true
     */
    @Override
    @Transactional
    public boolean unenrollStudent(Long courseId, Long studentId) {
        // 1. 检查课程是否存在
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }

        // 2. 检查学生是否存在且角色为STUDENT
        User studentUser = userMapper.selectById(studentId);
        if (studentUser == null || !"STUDENT".equals(studentUser.getRole())) {
            throw new RuntimeException("学生不存在或ID不是学生角色");
        }

        // 3. 检查选课记录是否存在
        LambdaQueryWrapper<CourseStudent> queryWrapper = Wrappers.<CourseStudent>lambdaQuery()
                .eq(CourseStudent::getCourseId, courseId)
                .eq(CourseStudent::getStudentId, studentId);
        Long count = courseStudentMapper.selectCount(queryWrapper);
        if (count == 0) {
            throw new RuntimeException("学生未选该课程，无法退课");
        }

        // 4. 删除选课记录
        int rows = courseStudentMapper.delete(queryWrapper);
        return rows > 0;
    }

    /**
     * 新增：API 4.8 获取课程的学生列表
     * @param courseId 课程ID
     * @param pageNum 当前页码
     * @param pageSize 每页数量
     * @param keyword 搜索关键词（匹配学生姓名或学号）
     * @return 分页的学生信息列表
     */
    @Override
    public PageVO<StudentVO> getEnrolledStudents(Long courseId, Long pageNum, Long pageSize, String keyword) {
        // 1. 检查课程是否存在
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }

        Page<CourseStudent> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<CourseStudent> courseStudentQueryWrapper = Wrappers.<CourseStudent>lambdaQuery()
                .eq(CourseStudent::getCourseId, courseId);

        // 先查询出该课程所有选课记录，获得 studentId 列表
        IPage<CourseStudent> courseStudentPage = courseStudentMapper.selectPage(page, courseStudentQueryWrapper);
        List<Long> studentIds = courseStudentPage.getRecords().stream()
                .map(CourseStudent::getStudentId)
                .collect(Collectors.toList());

        if (studentIds.isEmpty()) {
            // 如果没有学生选课，直接返回空分页结果
            PageVO<StudentVO> pageVO = new PageVO<>();
            pageVO.setRecords(List.of());
            pageVO.setTotal(0L);
            pageVO.setSize(pageSize);
            pageVO.setCurrent(pageNum);
            pageVO.setPages(0L);
            return pageVO;
        }

        // 根据 studentIds 和 keyword 进一步筛选学生详细信息
        LambdaQueryWrapper<Student> studentDetailQueryWrapper = Wrappers.<Student>lambdaQuery()
                .in(Student::getId, studentIds); // 限制在选课学生范围内

        if (StringUtils.isNotBlank(keyword)) {
            studentDetailQueryWrapper.and(wrapper -> {
                wrapper.like(Student::getName, keyword)   // 匹配学生姓名
                        .or()
                        .like(Student::getStuNo, keyword); // 匹配学号
            });
        }

        // 从 Student 表中获取符合条件的学生详情
        // 注意：这里没有直接对 Student 表进行分页，而是获取所有符合ID和keyword的学生，
        // 然后在内存中进行手动分页。对于非常大的数据集，这可能不是最高效的，
        // 更好的方案是自定义 Mapper XML 来实现 JOIN 和分页。
        List<Student> filteredStudents = studentMapper.selectList(studentDetailQueryWrapper);

        // 将 Student 实体转换为 StudentVO，并补充 User 表中的信息
        List<StudentVO> studentVOs = filteredStudents.stream()
                .map(student -> {
                    StudentVO studentVO = new StudentVO();
                    BeanUtils.copyProperties(student, studentVO);
                    // 补充 User 表中的信息，如 username, email, role, status
                    // 注意：这里每次循环都会查询 User 表，存在 N+1 问题。
                    // 更优化的方式是在循环前批量查询所有相关 User 信息并存入 Map，然后根据 ID 获取。
                    User user = userMapper.selectById(student.getId());
                    if (user != null) {
                        studentVO.setUsername(user.getUsername());
                        studentVO.setEmail(user.getEmail());
                        studentVO.setRole(user.getRole());
                        studentVO.setStatus(user.getStatus());
                    }
                    return studentVO;
                })
                .collect(Collectors.toList());

        // 在内存中对 studentVOs 进行手动分页
        long totalRecords = studentVOs.size();
        int startIndex = (int) ((pageNum - 1) * pageSize);
        int endIndex = (int) Math.min(startIndex + pageSize, totalRecords);
        List<StudentVO> paginatedRecords = studentVOs.subList(startIndex, endIndex);

        PageVO<StudentVO> pageVO = new PageVO<>();
        pageVO.setRecords(paginatedRecords);
        pageVO.setTotal(totalRecords); // 这里的 total 是经过 courseId 和 keyword 过滤后的总数
        pageVO.setSize(pageSize);
        pageVO.setCurrent(pageNum);
        pageVO.setPages((long) Math.ceil((double) totalRecords / pageSize));

        return pageVO;
    }

    /**
     * 获取教师的课程选项列表（用于下拉选择）
     */
    @Override
    public List<SimpleCourseVO> getTeacherCourseOptions(Long teacherId) {
        LambdaQueryWrapper<Course> queryWrapper = Wrappers.<Course>lambdaQuery()
                .eq(Course::getTeacherId, teacherId)
                .select(Course::getId, Course::getCourseName);
        
        List<Course> courses = courseMapper.selectList(queryWrapper);
        
        return courses.stream()
                .map(course -> {
                    SimpleCourseVO vo = new SimpleCourseVO();
                    vo.setId(course.getId());
                    vo.setName(course.getCourseName());
                    return vo;
                })
                .collect(Collectors.toList());
    }
}