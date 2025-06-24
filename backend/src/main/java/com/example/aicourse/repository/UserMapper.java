package com.example.aicourse.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.aicourse.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper public interface UserMapper extends BaseMapper<User> {
}
