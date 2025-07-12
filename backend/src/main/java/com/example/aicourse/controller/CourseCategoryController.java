package com.example.aicourse.controller;

import com.example.aicourse.dto.course.CourseCategoryCreateDTO;
import com.example.aicourse.dto.course.CourseCategoryUpdateDTO;
import com.example.aicourse.service.CourseCategoryService;
import com.example.aicourse.utils.Result;
import com.example.aicourse.vo.course.CourseCategoryVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课程分类管理控制器
 */
@RestController
@RequestMapping("/api/v1/course-categories")
public class CourseCategoryController {

    private final CourseCategoryService courseCategoryService;

    @Autowired
    public CourseCategoryController(CourseCategoryService courseCategoryService) {
        this.courseCategoryService = courseCategoryService;
    }

    /**
     * 获取所有课程分类
     */
    @GetMapping
    public Result<List<CourseCategoryVO>> getAllCategories() {
        try {
            List<CourseCategoryVO> categories = courseCategoryService.getAllCategories();
            return Result.ok(categories);
        } catch (Exception e) {
            return Result.error("获取课程分类失败：" + e.getMessage());
        }
    }

    /**
     * 创建课程分类
     */
    @PostMapping
    public Result<Long> createCategory(@Valid @RequestBody CourseCategoryCreateDTO dto) {
        try {
            Long categoryId = courseCategoryService.createCategory(dto);
            return Result.ok(categoryId);
        } catch (Exception e) {
            return Result.error("创建课程分类失败：" + e.getMessage());
        }
    }

    /**
     * 更新课程分类
     */
    @PutMapping("/{categoryId}")
    public Result<Void> updateCategory(@PathVariable Long categoryId, @Valid @RequestBody CourseCategoryUpdateDTO dto) {
        try {
            courseCategoryService.updateCategory(categoryId, dto);
            return Result.ok();
        } catch (Exception e) {
            return Result.error("更新课程分类失败：" + e.getMessage());
        }
    }

    /**
     * 删除课程分类
     */
    @DeleteMapping("/{categoryId}")
    public Result<Void> deleteCategory(@PathVariable Long categoryId) {
        try {
            courseCategoryService.deleteCategory(categoryId);
            return Result.ok();
        } catch (Exception e) {
            return Result.error("删除课程分类失败：" + e.getMessage());
        }
    }
}