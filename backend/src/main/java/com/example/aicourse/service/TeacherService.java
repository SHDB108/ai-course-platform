package com.example.aicourse.service;

import com.example.aicourse.dto.teacher.TeacherCreateDTO;
import com.example.aicourse.dto.teacher.TeacherUpdateDTO;
import com.example.aicourse.vo.PageVO;
import com.example.aicourse.vo.teacher.TeacherVO;
import com.example.aicourse.vo.teacher.TeacherOptionVO;

import java.util.List;

/**
 * 教师服务接口
 */
public interface TeacherService {
    
    /**
     * 获取教师列表 (分页)
     */
    PageVO<TeacherVO> listTeachers(Long pageNum, Long pageSize, String keyword, String department, String status);
    
    /**
     * 获取教师选项列表（用于下拉选择）
     */
    List<TeacherOptionVO> getTeacherOptions(String keyword);
    
    /**
     * 获取教师详情
     */
    TeacherVO getTeacherDetail(Long id);
    
    /**
     * 创建教师
     */
    Long createTeacher(TeacherCreateDTO dto);
    
    /**
     * 更新教师信息
     */
    boolean updateTeacher(Long id, TeacherUpdateDTO dto);
    
    /**
     * 删除教师
     */
    boolean deleteTeacher(Long id);
    
    /**
     * 更新教师状态
     */
    boolean updateTeacherStatus(Long id, String status);
}