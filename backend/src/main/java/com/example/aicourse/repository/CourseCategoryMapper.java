package com.example.aicourse.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.aicourse.entity.CourseCategory;
import com.example.aicourse.vo.course.CourseCategoryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 课程分类Mapper接口
 */
@Mapper
public interface CourseCategoryMapper extends BaseMapper<CourseCategory> {
    
    /**
     * 获取所有课程分类及其课程数量
     */
    @Select("SELECT cc.*, " +
            "(SELECT COUNT(*) FROM t_course c WHERE c.category_id = cc.id) as course_count " +
            "FROM course_categories cc ORDER BY cc.sort_order, cc.id")
    List<CourseCategoryVO> selectCategoriesWithCourseCount();
}