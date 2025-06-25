package com.example.aicourse.repository;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.aicourse.entity.VideoProgress;
import com.example.aicourse.vo.resource.VideoStudyStatisticsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface VideoProgressMapper extends BaseMapper<VideoProgress>{
    /**
     * 根据课程ID获取所有视频的学习统计信息
     */
    @Select("SELECT " +
            "  r.id AS resourceId, " +
            "  r.filename, " +
            "  COUNT(vp.id) AS totalViews, " +
            "  AVG(vp.completion) AS averageCompletion " +
            "FROM " +
            "  t_resource r " +
            "  LEFT JOIN t_video_progress vp ON r.id = vp.resource_id " +
            "WHERE " +
            "  r.course_id = #{courseId} AND r.type = 'VIDEO' " +
            "GROUP BY " +
            "  r.id, r.filename")
    List<VideoStudyStatisticsVO> selectCourseVideoStatistics(@Param("courseId") Long courseId);
}