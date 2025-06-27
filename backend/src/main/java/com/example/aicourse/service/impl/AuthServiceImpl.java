package com.example.aicourse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.aicourse.dto.auth.ForgotPasswordRequestDTO;
import com.example.aicourse.dto.auth.LoginRequestDTO;
import com.example.aicourse.dto.auth.PasswordResetDTO;
import com.example.aicourse.dto.auth.PasswordUpdateDTO;
import com.example.aicourse.dto.auth.StudentRegisterDTO;
import com.example.aicourse.dto.auth.TeacherRegisterDTO;
import com.example.aicourse.entity.Student;
import com.example.aicourse.entity.Teacher;
import com.example.aicourse.entity.User;
import com.example.aicourse.repository.StudentMapper;
import com.example.aicourse.repository.TeacherMapper;
import com.example.aicourse.repository.UserMapper;
import com.example.aicourse.service.AuthService;
import com.example.aicourse.vo.auth.LoginResponseVO;
import com.example.aicourse.vo.user.UserDetailVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final StudentMapper studentMapper;
    private final TeacherMapper teacherMapper;

    @Autowired
    public AuthServiceImpl(UserMapper userMapper, StudentMapper studentMapper, TeacherMapper teacherMapper) {
        this.userMapper = userMapper;
        this.studentMapper = studentMapper;
        this.teacherMapper = teacherMapper;
    }

    @Override
    public LoginResponseVO login(String username, String password) {
        // 1. 根据用户名查找用户
        LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>lambdaQuery()
                .eq(User::getUsername, username);
        User user = userMapper.selectOne(queryWrapper);

        // 2. 检查用户是否存在以及密码是否正确
        // 注意：实际项目中密码应该加密存储，这里为简化示例直接比较明文密码
        if (user == null || !user.getPassword().equals(password)) {
            return null; // 用户名或密码错误
        }

        // 3. 检查用户状态 (例如，0=禁用)
        if (user.getStatus() != null && user.getStatus() == 0) {
            return null; // 用户已被禁用
        }

        // 4. 模拟生成Token (实际项目中使用JWT等)
        String token = "mock_jwt_token_for_" + username + "_" + System.currentTimeMillis();

        // 5. 构建并返回LoginResponseVO
        LoginResponseVO response = new LoginResponseVO();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setRole(user.getRole()); // 返回用户角色

        // TODO: 更新用户最后登录时间 (可选)
        // user.setLastLoginTime(LocalDateTime.now());
        // userMapper.updateById(user);

        return response;
    }

    @Override
    @Transactional
    public Long registerStudent(StudentRegisterDTO dto) {
        // 1. 检查用户名是否已存在
        if (userMapper.selectCount(Wrappers.<User>lambdaQuery().eq(User::getUsername, dto.getUsername())) > 0) {
            throw new RuntimeException("用户名已存在");
        }
        // 2. 检查学号是否已存在
        if (studentMapper.selectCount(Wrappers.<Student>lambdaQuery().eq(Student::getStuNo, dto.getStuNo())) > 0) {
            throw new RuntimeException("学号已存在");
        }
        // 3. 检查邮箱是否已存在 (User表中也需要校验，因为邮箱是User的字段)
        if (userMapper.selectCount(Wrappers.<User>lambdaQuery().eq(User::getEmail, dto.getEmail())) > 0) {
            throw new RuntimeException("邮箱已存在");
        }

        // 4. 创建User实体
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword()); // 实际项目中应加密密码
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setRole("STUDENT"); // 设置角色为学生
        user.setStatus(1); // 默认启用
        userMapper.insert(user);

        // 获取新创建的User的ID，MyBatis-Plus在insert后会自动回填ID
        Long newUserId = user.getId();
        if (newUserId == null) {
            throw new RuntimeException("创建用户失败，无法获取用户ID");
        }

        // 5. 创建Student实体
        Student student = new Student();
        // 将User的ID作为Student的ID
        student.setId(newUserId);
        // 复制DTO中的其他属性到Student实体
        BeanUtils.copyProperties(dto, student);
        studentMapper.insert(student);

        return newUserId;
    }

    @Override
    @Transactional
    public Long registerTeacher(TeacherRegisterDTO dto) {
        // 1. 检查用户名是否已存在
        if (userMapper.selectCount(Wrappers.<User>lambdaQuery().eq(User::getUsername, dto.getUsername())) > 0) {
            throw new RuntimeException("用户名已存在");
        }
        // 2. 检查教师工号是否已存在
        if (teacherMapper.selectCount(Wrappers.<Teacher>lambdaQuery().eq(Teacher::getTeacherNo, dto.getTeacherNo())) > 0) {
            throw new RuntimeException("教师工号已存在");
        }
        // 3. 检查邮箱是否已存在 (User表中也需要校验)
        if (userMapper.selectCount(Wrappers.<User>lambdaQuery().eq(User::getEmail, dto.getEmail())) > 0) {
            throw new RuntimeException("邮箱已存在");
        }

        // 4. 创建User实体
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword()); // 实际项目中应加密密码
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setRole("TEACHER"); // 设置角色为教师
        user.setStatus(1); // 默认启用
        userMapper.insert(user);

        // 获取新创建的User的ID，MyBatis-Plus在insert后会自动回填ID
        Long newUserId = user.getId();
        if (newUserId == null) {
            throw new RuntimeException("创建用户失败，无法获取用户ID");
        }

        // 5. 创建Teacher实体
        Teacher teacher = new Teacher();
        // 将User的ID作为Teacher的ID
        teacher.setId(newUserId);
        // 复制DTO中的其他属性到Teacher实体
        BeanUtils.copyProperties(dto, teacher);
        teacherMapper.insert(teacher);

        return newUserId;
    }

    @Override
    public boolean logout() {
        // 实际应用中，这里会执行以下操作：
        // 1. 使当前用户的认证令牌（如JWT）失效（例如，加入黑名单）
        // 2. 清除服务器端可能的会话信息
        // 3. 对于基于Spring Security等框架的认证，可能还会清除SecurityContextHolder中的认证信息
        // 目前是简化的模拟认证，所以这里只返回成功
        System.out.println("用户已登出，令牌已失效 (模拟操作)");
        return true;
    }

    /**
     * [新增] 获取当前用户ID的占位实现
     * TODO: 应替换为从Spring Security等安全上下文中获取真实用户ID的逻辑
     * @return 写死的ID 1L
     */
    private Long currentUserId() {
        // 临时占位符，模拟当前登录用户ID
        // 在实际应用中，此ID应从 Spring Security 上下文、JWT 解析或Session中获取
        return 1L; // 假设当前用户ID为1
    }

    @Override
    public UserDetailVO getCurrentUserInfo() {
        Long userId = currentUserId(); // 获取当前用户ID (占位符)
        if (userId == null) {
            throw new RuntimeException("用户未登录或会话已过期");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        UserDetailVO userDetailVO = new UserDetailVO();
        // 复制User基础信息
        BeanUtils.copyProperties(user, userDetailVO);

        // 根据角色填充特定信息 (UserDetailVO 使用 JsonInclude.Include.NON_NULL 动态隐藏 null 字段)
        if ("STUDENT".equals(user.getRole())) {
            Student student = studentMapper.selectById(userId);
            if (student != null) {
                BeanUtils.copyProperties(student, userDetailVO);
            }
        } else if ("TEACHER".equals(user.getRole())) {
            Teacher teacher = teacherMapper.selectById(userId);
            if (teacher != null) {
                BeanUtils.copyProperties(teacher, userDetailVO);
            }
        }
        // 对于ADMIN角色，UserDetailVO中已包含User的基础信息，无需额外查询

        return userDetailVO;
    }

    @Override
    @Transactional
    public boolean updatePassword(Long userId, PasswordUpdateDTO dto) {
        // 1. 验证用户ID是否匹配当前登录用户 (在实际应用中，userId应与当前认证用户的ID匹配)
        // 这里简化为直接使用传入的userId，但实际应该校验 currentUserId() == userId
        // 如果要更严格，可以这样做：
        // Long authenticatedUserId = currentUserId();
        // if (!userId.equals(authenticatedUserId)) {
        //     throw new RuntimeException("无权修改其他用户密码");
        // }

        // 2. 查找用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 3. 验证旧密码
        // 实际项目中，旧密码也需要加密后比对
        if (!user.getPassword().equals(dto.getOldPassword())) {
            throw new RuntimeException("旧密码不正确");
        }

        // 4. 更新新密码
        user.setPassword(dto.getNewPassword()); // 实际项目中，新密码也需要加密
        int rowsAffected = userMapper.updateById(user);

        if (rowsAffected != 1) {
            throw new RuntimeException("密码更新失败");
        }
        return true;
    }

    @Override
    public String requestPasswordReset(ForgotPasswordRequestDTO dto) {
        // 实际业务中，需要根据 identifier (用户名/邮箱/手机号) 查找用户
        // 然后生成验证码或重置链接，并通过邮件/短信发送给用户
        // 还需要将验证码/链接与用户ID关联并存储，设置有效期

        // 简化实现：仅查找用户是否存在，然后返回成功消息
        LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>lambdaQuery()
                .eq(User::getUsername, dto.getIdentifier()) // 假设identifier是username
                .or()
                .eq(User::getEmail, dto.getIdentifier()) // 或者email
                .or()
                .eq(User::getPhone, dto.getIdentifier()); // 或者phone

        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            // 为安全起见，即使标识符不存在，也应返回成功的假象，以避免泄露用户信息
            // 但这里为了简化和调试，直接抛出异常，实际应该返回一个模糊的成功消息
            throw new RuntimeException("用户不存在或标识符不正确");
        }

        // 模拟发送验证码/链接
        System.out.println("模拟：已向 " + dto.getIdentifier() + " 发送密码重置验证码/链接");

        return "验证码已发送至您的邮箱/手机"; // 返回文档指定的成功消息
    }

    @Override
    @Transactional
    public boolean resetPassword(PasswordResetDTO dto) {
        // 1. 根据 identifier 查找用户
        LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>lambdaQuery()
                .eq(User::getUsername, dto.getIdentifier())
                .or()
                .eq(User::getEmail, dto.getIdentifier())
                .or()
                .eq(User::getPhone, dto.getIdentifier());
        User user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 2. 验证验证码 (简化实现：在实际项目中需要与之前存储的验证码进行比对，并检查有效期)
        // 这里的简化假设验证码是正确的，或者不进行严格验证，直接进行密码重置
        // 实际场景应包含：
        //   - 从缓存/数据库获取存储的验证码
        //   - 比对 dto.getVerificationCode()
        //   - 检查验证码是否过期
        // if (! "VALID_MOCK_CODE".equals(dto.getVerificationCode())) {
        //      throw new RuntimeException("验证码不正确或已过期");
        // }
        // 模拟验证码正确
        System.out.println("模拟：验证码 '" + dto.getVerificationCode() + "' 验证通过");


        // 3. 更新新密码
        user.setPassword(dto.getNewPassword()); // 实际项目中，新密码也需要加密
        int rowsAffected = userMapper.updateById(user);

        if (rowsAffected != 1) {
            throw new RuntimeException("密码重置失败");
        }

        // 4. (实际项目中) 验证码使用后应立即失效
        // 例如：从缓存/数据库中删除该验证码记录

        return true;
    }
}