package com.example.aicourse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.aicourse.dto.course.CourseCategoryCreateDTO;
import com.example.aicourse.dto.course.CourseCategoryUpdateDTO;
import com.example.aicourse.entity.CourseCategory;
import com.example.aicourse.repository.CourseCategoryMapper;
import com.example.aicourse.service.CourseCategoryService;
import com.example.aicourse.vo.course.CourseCategoryVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 课程分类管理服务实现类
 */
@Service
@Transactional
public class CourseCategoryServiceImpl implements CourseCategoryService {

    private final CourseCategoryMapper courseCategoryMapper;

    public CourseCategoryServiceImpl(CourseCategoryMapper courseCategoryMapper) {
        this.courseCategoryMapper = courseCategoryMapper;
    }

    @Override
    public List<CourseCategoryVO> getAllCategories() {
        return courseCategoryMapper.selectCategoriesWithCourseCount();
    }

    @Override
    public Long createCategory(CourseCategoryCreateDTO dto) {
        CourseCategory category = new CourseCategory();
        BeanUtils.copyProperties(dto, category);
        
        // 设置默认值
        if (category.getSortOrder() == null) {
            // 获取当前最大排序号并+1
            QueryWrapper<CourseCategory> wrapper = new QueryWrapper<>();
            wrapper.orderByDesc("sort_order").last("LIMIT 1");
            CourseCategory lastCategory = courseCategoryMapper.selectOne(wrapper);
            category.setSortOrder(lastCategory != null ? lastCategory.getSortOrder() + 1 : 1);
        }
        
        category.setStatus("ACTIVE");
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        
        courseCategoryMapper.insert(category);
        return category.getId();
    }

    @Override
    public void updateCategory(Long categoryId, CourseCategoryUpdateDTO dto) {
        CourseCategory category = courseCategoryMapper.selectById(categoryId);
        if (category == null) {
            throw new RuntimeException("课程分类不存在");
        }
        
        BeanUtils.copyProperties(dto, category);
        category.setUpdatedAt(LocalDateTime.now());
        
        courseCategoryMapper.updateById(category);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        CourseCategory category = courseCategoryMapper.selectById(categoryId);
        if (category == null) {
            throw new RuntimeException("课程分类不存在");
        }
        
        // 检查是否有课程使用该分类
        // 这里可以根据业务需求决定是否允许删除有课程的分类
        
        courseCategoryMapper.deleteById(categoryId);
    }
}