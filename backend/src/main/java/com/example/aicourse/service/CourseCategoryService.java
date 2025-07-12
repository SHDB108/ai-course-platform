package com.example.aicourse.service;

import com.example.aicourse.dto.course.CourseCategoryCreateDTO;
import com.example.aicourse.dto.course.CourseCategoryUpdateDTO;
import com.example.aicourse.vo.course.CourseCategoryVO;

import java.util.List;

/**
 * 课程分类管理服务接口
 */
public interface CourseCategoryService {
    
    /**
     * 获取所有课程分类列表
     */
    List<CourseCategoryVO> getAllCategories();
    
    /**
     * 创建课程分类
     */
    Long createCategory(CourseCategoryCreateDTO dto);
    
    /**
     * 更新课程分类
     */
    void updateCategory(Long categoryId, CourseCategoryUpdateDTO dto);
    
    /**
     * 删除课程分类
     */
    void deleteCategory(Long categoryId);
}