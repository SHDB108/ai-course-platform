package com.example.aicourse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.aicourse.config.StorageProperties;
import com.example.aicourse.dto.admin.StoragePropertiesUpdateDTO;
import com.example.aicourse.dto.admin.UserCreateByAdminDTO;
import com.example.aicourse.dto.admin.UserStatusUpdateDTO;
import com.example.aicourse.entity.Student;
import com.example.aicourse.entity.Teacher;
import com.example.aicourse.entity.User;
import com.example.aicourse.repository.StudentMapper;
import com.example.aicourse.repository.TeacherMapper;
import com.example.aicourse.repository.UserMapper;
import com.example.aicourse.service.AdminService;
import com.example.aicourse.vo.PageVO;
import com.example.aicourse.vo.admin.StoragePropertiesVO;
import com.example.aicourse.vo.admin.SystemHealthVO;
import com.example.aicourse.vo.user.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    private final UserMapper userMapper;
    private final StudentMapper studentMapper;
    private final TeacherMapper teacherMapper;
    private final StorageProperties storageProperties;
    private final HealthEndpoint healthEndpoint;
    private final PasswordEncoder passwordEncoder;

    private static final List<String> VALID_ROLES = Arrays.asList("STUDENT", "TEACHER", "ADMIN");

    @Autowired
    public AdminServiceImpl(UserMapper userMapper,  StudentMapper studentMapper, TeacherMapper teacherMapper, StorageProperties storageProperties, HealthEndpoint healthEndpoint, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.studentMapper = studentMapper;
        this.teacherMapper = teacherMapper;
        this.storageProperties = storageProperties;
        this.healthEndpoint = healthEndpoint;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public StoragePropertiesVO getStorageProperties() {
        StoragePropertiesVO vo = new StoragePropertiesVO();
        BeanUtils.copyProperties(storageProperties, vo);
        if (storageProperties.getMinio() != null) {
            StoragePropertiesVO.MinioVO minioVO = new StoragePropertiesVO.MinioVO();
            BeanUtils.copyProperties(storageProperties.getMinio(), minioVO);
            vo.setMinio(minioVO);
        }
        return vo;
    }

    @Override
    public void updateStorageProperties(StoragePropertiesUpdateDTO dto) {
        // 警告：在生产环境中，动态修改配置是复杂且危险的操作。
        // 简单的实现只是修改内存中的Bean属性，但这不会持久化，且可能不会被所有组件重新加载。
        // 生产级实现需要借助 Spring Cloud Config + Spring Cloud Bus，或通过重启应用来生效。
        System.out.println("收到更新存储配置的请求（仅演示，未实际生效）: " + dto);
        // storageProperties.setType(dto.getType());
        // ...
    }

    @Override
    public SystemHealthVO getSystemHealth() {
        if (healthEndpoint == null) {
            SystemHealthVO vo = new SystemHealthVO();
            vo.setStatus("UNKNOWN");
            vo.setComponents(Map.of("actuator", new SystemHealthVO.ComponentHealth()));
            return vo;
        }

        HealthComponent healthComponent = healthEndpoint.health();
        Health health = (Health) healthComponent;
        SystemHealthVO vo = new SystemHealthVO();
        vo.setStatus(health.getStatus().getCode());

        Map<String, SystemHealthVO.ComponentHealth> componentHealthMap = health.getDetails().entrySet().stream()
                .filter(entry -> entry.getValue() instanceof HealthComponent)
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                    HealthComponent component = (HealthComponent) entry.getValue();
                    SystemHealthVO.ComponentHealth ch = new SystemHealthVO.ComponentHealth();
                    ch.setStatus(component.getStatus().getCode());

                    // HealthComponent 接口没有 getDetails()，所以这里也需要转换
                    if (component instanceof Health) {
                        ch.setDetails(((Health) component).getDetails());
                    } else {
                        ch.setDetails(Collections.emptyMap());
                    }
                    return ch;
                }));
        vo.setComponents(componentHealthMap);

        return vo;
    }

    @Override
    public PageVO<UserVO> getUsersByAdmin(Long pageNum, Long pageSize, String role, String keyword) {
        // 1. 构建分页对象
        Page<User> page = new Page<>(pageNum, pageSize);

        // 2. 构建查询条件
        LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>lambdaQuery();

        if (StringUtils.isNotBlank(role)) {
            queryWrapper.eq(User::getRole, role);
        }

        // --- 开始修改部分 ---
        // 处理 keyword 搜索：匹配用户名、邮箱、姓名、学号或工号
        if (StringUtils.isNotBlank(keyword)) {
            Set<Long> matchedUserIds = new HashSet<>(); // 用于存储从 Student 和 Teacher 表中匹配到的用户ID

            // 根据 keyword 在 Student 表中搜索匹配的姓名或学号
            // 只有当角色没有明确限制为 TEACHER 或 ADMIN 时才搜索学生表
            if (!"TEACHER".equals(role) && !"ADMIN".equals(role)) {
                LambdaQueryWrapper<Student> studentQuery = Wrappers.<Student>lambdaQuery()
                        .like(Student::getName, keyword)
                        .or()
                        .like(Student::getStuNo, keyword);
                studentMapper.selectList(studentQuery).forEach(s -> matchedUserIds.add(s.getId()));
            }

            // 根据 keyword 在 Teacher 表中搜索匹配的姓名或工号
            // 只有当角色没有明确限制为 STUDENT 或 ADMIN 时才搜索教师表
            if (!"STUDENT".equals(role) && !"ADMIN".equals(role)) {
                LambdaQueryWrapper<Teacher> teacherQuery = Wrappers.<Teacher>lambdaQuery()
                        .like(Teacher::getName, keyword)
                        .or()
                        .like(Teacher::getTeacherNo, keyword);
                teacherMapper.selectList(teacherQuery).forEach(t -> matchedUserIds.add(t.getId()));
            }


            // 构建 User 表的查询条件
            queryWrapper.and(wrapper -> {
                wrapper.like(User::getUsername, keyword) // 匹配用户名
                        .or()
                        .like(User::getEmail, keyword);   // 匹配邮箱

                // 如果从 Student 或 Teacher 表中找到了匹配的ID，则通过这些ID来匹配 User 表
                if (!matchedUserIds.isEmpty()) {
                    wrapper.or().in(User::getId, matchedUserIds);
                }
            });
        }
        // --- 结束修改部分 ---

        // 3. 执行分页查询
        IPage<User> userPage = userMapper.selectPage(page, queryWrapper);

        // 4. 转换实体为VO，并补充额外信息（如姓名）
        List<UserVO> userVOs = userPage.getRecords().stream().map(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO); // 复制User基础信息

            // 根据角色补充姓名
            if ("STUDENT".equals(user.getRole())) {
                Student student = studentMapper.selectById(user.getId());
                if (student != null) {
                    userVO.setName(student.getName());
                }
            } else if ("TEACHER".equals(user.getRole())) {
                Teacher teacher = teacherMapper.selectById(user.getId());
                if (teacher != null) {
                    userVO.setName(teacher.getName());
                }
            }
            // ADMIN 角色的 name 字段在 UserVO 中目前无直接来源，如果需要，User 实体需增加 name 字段或从其他地方获取

            return userVO;
        }).collect(Collectors.toList());

        // 5. 构建 PageVO 响应对象
        PageVO<UserVO> pageVO = new PageVO<>();
        pageVO.setRecords(userVOs);
        pageVO.setTotal(userPage.getTotal());
        pageVO.setSize(userPage.getSize());
        pageVO.setCurrent(userPage.getCurrent());

        return pageVO;
    }

    @Override
    public boolean updateUserStatus(Long userId, UserStatusUpdateDTO dto) {
        // 1. 查找用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 2. 更新用户状态
        // 确保状态值在有效范围内 (0或1)
        if (dto.getStatus() != 0 && dto.getStatus() != 1) {
            throw new RuntimeException("无效的用户状态值，只能是0（禁用）或1（启用）");
        }

        user.setStatus(dto.getStatus());
        int rowsAffected = userMapper.updateById(user);

        if (rowsAffected != 1) {
            throw new RuntimeException("更新用户状态失败");
        }
        return true;
    }

    @Override
    @Transactional // 确保User和Student/Teacher的创建在同一个事务中
    public Long createUserByAdmin(UserCreateByAdminDTO dto) {
        // 1. 基础数据校验：用户名、邮箱重复
        if (userMapper.selectCount(Wrappers.<User>lambdaQuery().eq(User::getUsername, dto.getUsername())) > 0) {
            throw new RuntimeException("用户名已存在");
        }
        if (userMapper.selectCount(Wrappers.<User>lambdaQuery().eq(User::getEmail, dto.getEmail())) > 0) {
            throw new RuntimeException("邮箱已存在");
        }

        // 2. 角色校验
        if (!VALID_ROLES.contains(dto.getRole().toUpperCase())) {
            throw new RuntimeException("无效的用户角色: " + dto.getRole() + "，角色只能是 STUDENT, TEACHER, ADMIN");
        }

        // 3. 创建User实体
        User user = new User();
        user.setUsername(dto.getUsername());
        // 使用 passwordEncoder 加密密码
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        // 手机号在UserCreateByAdminDTO中没有，根据User实体定义，User可以有phone，但此DTO没提供
        // user.setPhone(dto.getPhone()); // DTO中没有phone字段
        user.setRole(dto.getRole().toUpperCase()); // 角色转大写存储
        user.setStatus(1); // 默认启用
        userMapper.insert(user);

        // 获取新创建的User的ID，MyBatis-Plus在insert后会自动回填ID
        Long newUserId = user.getId();
        if (newUserId == null) {
            throw new RuntimeException("创建用户失败，无法获取用户ID");
        }

        // 4. 根据角色创建对应详细信息实体
        if ("STUDENT".equals(user.getRole())) {
            if (StringUtils.isBlank(dto.getStuNo())) {
                throw new RuntimeException("学生用户学号不能为空");
            }
            if (StringUtils.isBlank(dto.getName())) {
                throw new RuntimeException("学生用户姓名不能为空");
            }
            if (studentMapper.selectCount(Wrappers.<Student>lambdaQuery().eq(Student::getStuNo, dto.getStuNo())) > 0) {
                throw new RuntimeException("学号已存在");
            }

            Student student = new Student();
            student.setId(newUserId); // UserID作为Student的ID
            BeanUtils.copyProperties(dto, student); // 复制 stuNo, name, gender, major, grade
            studentMapper.insert(student);

        } else if ("TEACHER".equals(user.getRole())) {
            if (StringUtils.isBlank(dto.getTeacherNo())) {
                throw new RuntimeException("教师用户工号不能为空");
            }
            if (StringUtils.isBlank(dto.getName())) {
                throw new RuntimeException("教师用户姓名不能为空");
            }
            if (teacherMapper.selectCount(Wrappers.<Teacher>lambdaQuery().eq(Teacher::getTeacherNo, dto.getTeacherNo())) > 0) {
                throw new RuntimeException("教师工号已存在");
            }

            Teacher teacher = new Teacher();
            teacher.setId(newUserId); // UserID作为Teacher的ID
            BeanUtils.copyProperties(dto, teacher); // 复制 teacherNo, name, department, title
            teacherMapper.insert(teacher);
        }
        // 对于 ADMIN 角色，User 实体已足够

        return user.getId();
    }
}