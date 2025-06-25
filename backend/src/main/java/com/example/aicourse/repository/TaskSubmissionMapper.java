package com.example.aicourse.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.aicourse.entity.TaskSubmission;
import com.example.aicourse.vo.ScoreRowVO;
import com.example.aicourse.vo.course.TrendPointVO;
import org.apache.ibatis.annotations.Lang;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Lang;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface TaskSubmissionMapper extends BaseMapper<TaskSubmission>{

    /**
     * 查询课程作业的「按日平均分」走势
     *
     * @param courseId  课程 ID
     * @param startDate 开始时间(可选)
     */
    List<TrendPointVO> selectByCourse(@Param("courseId") Long courseId,
                                      @Param("startDate") LocalDateTime startDate);

    /**
     * 为导出CSV功能，查询课程下所有任务的提交成绩
     */
    @Select("SELECT " +
            "  ts.student_id AS studentId, " +
            "  ts.score " +
            "FROM t_task_submission ts " +
            "  INNER JOIN t_task t ON ts.task_id = t.id " +
            "WHERE t.course_id = #{courseId} AND ts.score IS NOT NULL")
    List<ScoreRowVO> selectTaskRowsForExport(@Param("courseId") Long courseId);

    /**
     * 查询单个学生在指定课程中的成绩趋势（按任务提交日期）
     */
    @Select(
            "SELECT " +
                    "  DATE_FORMAT(ts.submitted_at, '%Y-%m-%d') AS date, " +
                    "  ts.score AS score " +
                    "FROM t_task_submission ts " +
                    "  INNER JOIN t_task t ON ts.task_id = t.id " +
                    "WHERE t.course_id = #{courseId} AND ts.student_id = #{studentId} AND ts.score IS NOT NULL " +
                    "ORDER BY ts.submitted_at"
    )
    List<TrendPointVO> selectScoreTrendForStudent(@Param("courseId") Long courseId, @Param("studentId") Long studentId);
}