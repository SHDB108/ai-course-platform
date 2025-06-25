package com.example.aicourse.repository;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.aicourse.entity.Task;
import com.example.aicourse.vo.analytics.TaskCompletionSummaryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TaskMapper extends BaseMapper<Task>{
    /**
     * 根据课程ID，查询该课程下每个任务的完成情况统计
     * @param courseId 课程ID
     * @return 任务完成统计列表
     */
    @Select("SELECT " +
            "  t.id AS taskId, " +
            "  t.title AS taskTitle, " +
            "  COUNT(DISTINCT ts.student_id) AS submittedCount, " +
            "  COUNT(CASE WHEN ts.submitted_at <= t.deadline THEN 1 END) AS onTimeCount, " +
            "  COALESCE(AVG(ts.score), 0) AS averageScore " +
            "FROM " +
            "  t_task t " +
            "  LEFT JOIN t_task_submission ts ON t.id = ts.task_id " +
            "WHERE " +
            "  t.course_id = #{courseId} " +
            "GROUP BY " +
            "  t.id, t.title, t.gmt_create " +
            "ORDER BY " +
            "  t.gmt_create DESC")
    List<TaskCompletionSummaryVO> selectTaskCompletionSummary(@Param("courseId") Long courseId);
}