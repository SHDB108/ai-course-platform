package com.example.aicourse.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_user")
public class User {
    @TableId
    private Long id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String role; // e.g., 'STUDENT', 'TEACHER', 'ADMIN'
    private Integer status; // e.g., 0=disabled, 1=enabled

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime gmtCreate;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime gmtModified;

    private LocalDateTime lastLoginTime;
}