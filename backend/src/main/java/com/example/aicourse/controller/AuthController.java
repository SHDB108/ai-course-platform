package com.example.aicourse.controller;

import com.example.aicourse.dto.auth.AdminRegisterDTO;
import com.example.aicourse.dto.auth.ForgotPasswordRequestDTO;
import com.example.aicourse.dto.auth.LoginRequestDTO;
import com.example.aicourse.dto.auth.PasswordResetDTO;
import com.example.aicourse.dto.auth.PasswordUpdateDTO;
import com.example.aicourse.dto.auth.StudentRegisterDTO;
import com.example.aicourse.dto.auth.TeacherRegisterDTO;
import com.example.aicourse.vo.auth.LoginResponseVO;
import com.example.aicourse.vo.user.UserDetailVO;
import com.example.aicourse.service.AuthService;
import com.example.aicourse.utils.Result;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * API 2.1 用户登录
     */
    @PostMapping("/login")
    public Result<LoginResponseVO> login(@Valid @RequestBody LoginRequestDTO request) {
        try {
            LoginResponseVO response = authService.login(request);
            return Result.ok(response);
        } catch (Exception e) {
            // 记录详细错误信息用于调试
            System.err.println("Login failed for user: " + request.getUsername() + ", Error: " + e.getMessage());
            e.printStackTrace();
            // 返回更具体的认证失败信息
            return Result.error("认证失败: 用户名或密码错误");
        }
    }

    /**
     * API 2.2 用户注册 (学生)
     */
    @PostMapping("/register/student")
    public Result<Long> registerStudent(@Valid @RequestBody StudentRegisterDTO request) {
        try {
            Long newUserId = authService.registerStudent(request);
            return Result.ok(newUserId);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * API 2.3 用户注册 (教师)
     */
    @PostMapping("/register/teacher")
    public Result<Long> registerTeacher(@Valid @RequestBody TeacherRegisterDTO request) {
        try {
            Long newUserId = authService.registerTeacher(request);
            return Result.ok(newUserId);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * API 2.4 用户注册 (管理员)
     */
    @PostMapping("/register/admin")
    public Result<Long> registerAdmin(@Valid @RequestBody AdminRegisterDTO request) {
        try {
            Long newUserId = authService.registerAdmin(request);
            return Result.ok(newUserId);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * API 2.5 用户登出
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.ok();
    }

    /**
     * API 2.6 获取当前用户信息
     */
    @GetMapping("/users/me") // 路径为 /api/v1/auth/users/me
    public Result<UserDetailVO> getCurrentUserInfo() {
        try {
            UserDetailVO userInfo = authService.getCurrentUserInfo();
            return Result.ok(userInfo);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * API 2.7 修改用户密码
     */
    @PutMapping("/users/{id}/password") // 路径为 /api/v1/auth/users/{id}/password
    public Result<Void> updatePassword(
            @PathVariable Long id,
            @Valid @RequestBody PasswordUpdateDTO request) {
        try {
            authService.updatePassword(id, request);
            return Result.ok();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * API 2.8 请求密码重置
     */
    @PostMapping("/forgot-password/request")
    public Result<String> requestPasswordReset(@Valid @RequestBody ForgotPasswordRequestDTO request) {
        try {
            String message = authService.requestPasswordReset(request);
            return Result.ok(message);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * API 2.9 执行密码重置
     */
    @PostMapping("/forgot-password/reset")
    public Result<Void> resetPassword(@Valid @RequestBody PasswordResetDTO request) {
        try {
            authService.resetPassword(request);
            return Result.ok();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}