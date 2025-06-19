package com.example.aicourse.repository;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.aicourse.entity.TaskSubmission;
import com.example.aicourse.vo.TrendPointVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper public interface TaskSubmissionMapper extends BaseMapper<TaskSubmission>{
    @Select(
            "SELECT " +
                    "  DATE_FORMAT(ts.submitted_at, '%Y-%m-%d') AS date, " +
                    "  ROUND(AVG(ts.score), 2)         AS score " +
                    "FROM t_task_submission ts " +
                    "  INNER JOIN t_task t ON ts.task_id = t.id " +
                    "WHERE t.course_id = #{courseId} " +
                    "GROUP BY DATE_FORMAT(ts.submitted_at, '%Y-%m-%d') " +
                    "ORDER BY date"
    )
    List<TrendPointVO> selectByCourse(@Param("courseId") Long courseId);
}