package com.example.aicourse.service.impl;

import com.example.aicourse.dto.teacher.TeacherCreateDTO;
import com.example.aicourse.dto.teacher.TeacherUpdateDTO;
import com.example.aicourse.entity.Teacher;
import com.example.aicourse.entity.User;
import com.example.aicourse.repository.TeacherMapper;
import com.example.aicourse.repository.UserMapper;
import com.example.aicourse.service.TeacherService;
import com.example.aicourse.vo.PageVO;
import com.example.aicourse.vo.teacher.TeacherVO;
import com.example.aicourse.vo.teacher.TeacherOptionVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 教师服务实现类
 */
@Service
public class TeacherServiceImpl implements TeacherService {
    
    private final TeacherMapper teacherMapper;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public TeacherServiceImpl(TeacherMapper teacherMapper, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.teacherMapper = teacherMapper;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public PageVO<TeacherVO> listTeachers(Long pageNum, Long pageSize, String keyword, String department, String status) {
        Page<TeacherVO> page = new Page<>(pageNum, pageSize);
        List<TeacherVO> teachers = teacherMapper.selectTeachersPage(page, keyword, department, status);
        
        PageVO<TeacherVO> pageVO = new PageVO<>();
        pageVO.setRecords(teachers);
        pageVO.setTotal(page.getTotal());
        pageVO.setCurrent(page.getCurrent());
        pageVO.setSize(page.getSize());
        pageVO.setPages(page.getPages());
        
        return pageVO;
    }
    
    @Override
    public List<TeacherOptionVO> getTeacherOptions(String keyword) {
        return teacherMapper.selectTeacherOptions(keyword);
    }
    
    @Override
    public TeacherVO getTeacherDetail(Long id) {
        return teacherMapper.selectTeacherById(id);
    }
    
    @Override
    @Transactional
    public Long createTeacher(TeacherCreateDTO dto) {
        // 检查用户名是否已存在
        if (userMapper.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查工号是否已存在
        if (teacherMapper.existsByTeacherNo(dto.getTeacherNo())) {
            throw new RuntimeException("教师工号已存在");
        }
        
        // 创建用户账户
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setRole("TEACHER");
        user.setStatus(1); // ACTIVE
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        userMapper.insert(user);
        
        // 创建教师信息
        Teacher teacher = new Teacher();
        teacher.setId(user.getId());
        teacher.setTeacherNo(dto.getTeacherNo());
        teacher.setName(dto.getName());
        teacher.setDepartment(dto.getDepartment());
        teacher.setTitle(dto.getTitle());
        teacher.setPhone(dto.getPhone());
        teacher.setEmail(dto.getEmail());
        // Note: Status removed - Teacher entity doesn't have status field, managed through User entity
        teacher.setCreatedAt(LocalDateTime.now());
        teacher.setUpdatedAt(LocalDateTime.now());
        
        teacherMapper.insert(teacher);
        
        return teacher.getId();
    }
    
    @Override
    @Transactional
    public boolean updateTeacher(Long id, TeacherUpdateDTO dto) {
        Teacher teacher = teacherMapper.selectById(id);
        if (teacher == null) {
            return false;
        }
        
        // 更新教师信息
        if (dto.getName() != null) {
            teacher.setName(dto.getName());
        }
        if (dto.getDepartment() != null) {
            teacher.setDepartment(dto.getDepartment());
        }
        if (dto.getTitle() != null) {
            teacher.setTitle(dto.getTitle());
        }
        if (dto.getPhone() != null) {
            teacher.setPhone(dto.getPhone());
        }
        if (dto.getEmail() != null) {
            teacher.setEmail(dto.getEmail());
        }
        teacher.setUpdatedAt(LocalDateTime.now());
        
        int result = teacherMapper.updateById(teacher);
        
        // 同步更新用户邮箱
        if (dto.getEmail() != null && teacher.getId() != null) {
            User user = userMapper.selectById(teacher.getId());
            if (user != null) {
                user.setEmail(dto.getEmail());
                user.setUpdatedAt(LocalDateTime.now());
                userMapper.updateById(user);
            }
        }
        
        return result > 0;
    }
    
    @Override
    @Transactional
    public boolean deleteTeacher(Long id) {
        Teacher teacher = teacherMapper.selectById(id);
        if (teacher == null) {
            return false;
        }
        
        // 软删除：更新用户状态为已删除
        // Note: Status removed from Teacher entity - only update User entity status
        teacher.setUpdatedAt(LocalDateTime.now());
        
        int result = teacherMapper.updateById(teacher);
        
        // 同步更新用户状态
        if (teacher.getId() != null) {
            User user = userMapper.selectById(teacher.getId());
            if (user != null) {
                user.setStatus(-2); // DELETED
                user.setUpdatedAt(LocalDateTime.now());
                userMapper.updateById(user);
            }
        }
        
        return result > 0;
    }
    
    @Override
    @Transactional
    public boolean updateTeacherStatus(Long id, String status) {
        Teacher teacher = teacherMapper.selectById(id);
        if (teacher == null) {
            return false;
        }
        
        // Note: Status removed from Teacher entity - only update User entity status
        teacher.setUpdatedAt(LocalDateTime.now());
        
        // Convert string status to integer
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
                throw new RuntimeException("Invalid status value: " + status);
        }
        
        int result = teacherMapper.updateById(teacher);
        
        // 同步更新用户状态
        if (teacher.getId() != null) {
            User user = userMapper.selectById(teacher.getId());
            if (user != null) {
                user.setStatus(statusInt);
                user.setUpdatedAt(LocalDateTime.now());
                userMapper.updateById(user);
            }
        }
        
        return result > 0;
    }
}