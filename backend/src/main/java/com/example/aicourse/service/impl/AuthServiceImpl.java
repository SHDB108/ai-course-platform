package com.example.aicourse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.aicourse.dto.auth.AdminRegisterDTO;
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
import com.example.aicourse.utils.JwtUtil;
import com.example.aicourse.vo.auth.LoginResponseVO;
import com.example.aicourse.vo.user.UserDetailVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final StudentMapper studentMapper;
    private final TeacherMapper teacherMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthServiceImpl(UserMapper userMapper, StudentMapper studentMapper, TeacherMapper teacherMapper, @Lazy PasswordEncoder passwordEncoder,@Lazy AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.studentMapper = studentMapper;
        this.teacherMapper = teacherMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 获取当前登录用户的实体
     * @return User 实体
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new RuntimeException("用户未登录或会话已过期");
        }
        return (User) authentication.getPrincipal();
    }


    /**
     * 实现 UserDetailsService 接口的核心方法
     * Spring Security 会调用此方法来获取用户信息
     */
    @Override
    @Cacheable(value = "userCache", key = "#username")
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, username));
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return user;
    }


    @Override
    public LoginResponseVO login(LoginRequestDTO dto) {
        // 1. 使用 AuthenticationManager 进行认证
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
        );

        // 2. 如果认证通过，SecurityContextHolder会持有认证信息
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. 从认证信息中获取 UserDetails
        User userDetails = (User) authentication.getPrincipal();

        // 4. 生成 JWT
        final String token = jwtUtil.generateToken(userDetails);

        // 5. 构建响应
        return new LoginResponseVO(token, userDetails.getId(), userDetails.getUsername(), userDetails.getRole());
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
        // 使用 passwordEncoder 加密密码
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setRole("STUDENT"); // 设置角色为学生
        user.setStatus(1); // 1=ACTIVE
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

        return user.getId();
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
        // 使用 passwordEncoder 加密密码
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setRole("TEACHER"); // 设置角色为教师
        user.setStatus(1); // 1=ACTIVE
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

        return user.getId();
    }

    @Override
    @Transactional
    public Long registerAdmin(AdminRegisterDTO dto) {
        // 1. 检查用户名是否已存在
        if (userMapper.selectCount(Wrappers.<User>lambdaQuery().eq(User::getUsername, dto.getUsername())) > 0) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 2. 检查邮箱是否已存在 (如果提供了邮箱)
        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            if (userMapper.selectCount(Wrappers.<User>lambdaQuery().eq(User::getEmail, dto.getEmail())) > 0) {
                throw new RuntimeException("邮箱已存在");
            }
        }

        // 3. 创建User实体
        User user = new User();
        user.setUsername(dto.getUsername());
        // 使用 passwordEncoder 加密密码
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setRole("ADMIN"); // 设置角色为管理员
        user.setStatus(1); // 1=ACTIVE
        userMapper.insert(user);

        // 获取新创建的User的ID，MyBatis-Plus在insert后会自动回填ID
        Long newUserId = user.getId();
        if (newUserId == null) {
            throw new RuntimeException("创建用户失败，无法获取用户ID");
        }

        // 管理员不需要创建额外的关联表记录，只需要User表记录即可
        return user.getId();
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

    @Override
    public UserDetailVO getCurrentUserInfo() {
        User user = getCurrentUser();

        UserDetailVO userDetailVO = new UserDetailVO();
        // 复制User基础信息
        BeanUtils.copyProperties(user, userDetailVO);

        // 根据角色填充特定信息
        if ("STUDENT".equals(user.getRole())) {
            Student student = studentMapper.selectById(user.getId());
            if (student != null) {
                BeanUtils.copyProperties(student, userDetailVO);
            }
        } else if ("TEACHER".equals(user.getRole())) {
            Teacher teacher = teacherMapper.selectById(user.getId());
            if (teacher != null) {
                BeanUtils.copyProperties(teacher, userDetailVO);
            }
        }
        return userDetailVO;
    }


    @Override
    @Transactional
    @CacheEvict(value = "userCache", key = "#result.username")
    public boolean updatePassword(Long userId, PasswordUpdateDTO dto) {
        // 1. 验证用户ID是否匹配当前登录用户
        User currentUser = getCurrentUser();
        if (!userId.equals(currentUser.getId())) {
            throw new RuntimeException("无权修改其他用户的密码");
        }

        // 2. 验证旧密码 (使用 passwordEncoder.matches)
        if (!passwordEncoder.matches(dto.getOldPassword(), currentUser.getPassword())) {
            throw new RuntimeException("旧密码不正确");
        }

        // 3. 更新为加密后的新密码
        currentUser.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        int rowsAffected = userMapper.updateById(currentUser);

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
    @CacheEvict(value = "userCache", key = "#dto.identifier")
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


        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        int rowsAffected = userMapper.updateById(user);

        if (rowsAffected != 1) {
            throw new RuntimeException("密码重置失败");
        }

        // 4. (实际项目中) 验证码使用后应立即失效
        // 例如：从缓存/数据库中删除该验证码记录

        return true;
    }
}