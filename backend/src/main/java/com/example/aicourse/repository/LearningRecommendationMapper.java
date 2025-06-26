package com.example.aicourse.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.aicourse.entity.LearningRecommendation;
import com.example.aicourse.vo.knowledge.LearningRecommendationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LearningRecommendationMapper extends BaseMapper<LearningRecommendation> {
    /**
     * 【新增】根据学生和课程ID，查询已聚合的、可直接展示的学习推荐列表
     *
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @param count 限制查询的数量
     * @return 包含完整信息的学习推荐VO列表
     */
    List<LearningRecommendationVO> selectEnrichedRecommendations(
            @Param("studentId") Long studentId,
            @Param("courseId") Long courseId,
            @Param("limit") Integer count
    );
}
