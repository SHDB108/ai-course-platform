package com.example.aicourse.repository;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.aicourse.entity.Task;
import com.example.aicourse.vo.analytics.TaskCompletionSummaryVO;
import com.example.aicourse.vo.task.TeacherTaskVO;
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
     * --- 性能优化建议 ---
     *      * 审阅注：此查询在高并发和大数据量（如 10万+ 提交记录）下存在性能风险。
     *      * 1. 短期优化: 请务必在 t_task_submission 表上为 (task_id, submitted_at, score) 创建复合索引。
     *      * SQL: CREATE INDEX idx_taskid_submittedat_score ON t_task_submission(task_id, submitted_at, score);
     *      * 2. 长期优化: 对于统计类需求，应采用离线计算。每日定时将统计结果汇总到一张新的统计表 (如 t_task_stat)，
     *      * 此接口直接查询该聚合表，避免实时进行重量级计算。
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

    /**
     * 获取教师的任务列表（分页）
     */
    @Select("<script>" +
            "SELECT " +
            "  t.id, " +
            "  t.title, " +
            "  c.course_name AS courseName, " +
            "  t.type, " +
            "  t.deadline AS dueDate, " +
            "  t.max_score AS maxScore, " +
            "  COALESCE(sub_count.count, 0) AS submissionCount, " +
            "  CASE WHEN t.status = 'PUBLISHED' THEN true ELSE false END AS published, " +
            "  t.gmt_create AS createdAt, " +
            "  t.gmt_modified AS updatedAt " +
            "FROM t_task t " +
            "LEFT JOIN t_course c ON t.course_id = c.id " +
            "LEFT JOIN (" +
            "  SELECT task_id, COUNT(DISTINCT student_id) as count " +
            "  FROM t_task_submission " +
            "  GROUP BY task_id" +
            ") sub_count ON t.id = sub_count.task_id " +
            "WHERE c.teacher_id = #{teacherId} " +
            "<if test='courseId != null'> AND t.course_id = #{courseId} </if>" +
            "<if test='type != null and type != \"\"'> AND t.type = #{type} </if>" +
            "<if test='published != null'> " +
            "  <if test='published == true'> AND t.status = 'PUBLISHED' </if>" +
            "  <if test='published == false'> AND t.status != 'PUBLISHED' </if>" +
            "</if>" +
            "ORDER BY t.gmt_create DESC" +
            "</script>")
    IPage<TeacherTaskVO> selectTeacherTasks(Page<TeacherTaskVO> page,
                                           @Param("teacherId") Long teacherId,
                                           @Param("courseId") Long courseId,
                                           @Param("type") String type,
                                           @Param("published") Boolean published);
}