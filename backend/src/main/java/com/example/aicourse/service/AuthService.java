package com.example.aicourse.service;

import com.example.aicourse.dto.auth.ForgotPasswordRequestDTO;
import com.example.aicourse.dto.auth.LoginRequestDTO;
import com.example.aicourse.dto.auth.PasswordResetDTO;
import com.example.aicourse.dto.auth.PasswordUpdateDTO;
import com.example.aicourse.dto.auth.StudentRegisterDTO;
import com.example.aicourse.dto.auth.TeacherRegisterDTO;
import com.example.aicourse.vo.auth.LoginResponseVO;
import com.example.aicourse.vo.user.UserDetailVO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthService extends UserDetailsService {
    /**
     * 学生用户注册
     * @param dto 学生注册请求DTO
     * @return 注册成功返回新用户ID，否则返回null
     */
    Long registerStudent(StudentRegisterDTO dto);

    /**
     * 教师用户注册
     * @param dto 教师注册请求DTO
     * @return 注册成功返回新用户ID，否则返回null
     */
    Long registerTeacher(TeacherRegisterDTO dto);

    /**
     * 用户登出
     * @return 始终返回 true 表示处理成功
     */
    boolean logout();

    /**
     * 获取当前用户的详细信息
     * @return 当前用户的详细信息VO
     */
    UserDetailVO getCurrentUserInfo();

    /**
     * 修改用户密码
     * @param userId 要修改密码的用户ID
     * @param dto 包含旧密码和新密码的DTO
     * @return 修改成功返回true，否则抛出异常
     */
    boolean updatePassword(Long userId, PasswordUpdateDTO dto);

    /**
     * 请求密码重置
     * @param dto 包含标识符（用户名、邮箱或手机号）的DTO
     * @return 成功提示信息
     */
    String requestPasswordReset(ForgotPasswordRequestDTO dto);

    /**
     * 执行密码重置
     * @param dto 包含标识符、验证码和新密码的DTO
     * @return 密码重置成功返回true
     */
    boolean resetPassword(PasswordResetDTO dto);

    LoginResponseVO login(LoginRequestDTO dto);

}