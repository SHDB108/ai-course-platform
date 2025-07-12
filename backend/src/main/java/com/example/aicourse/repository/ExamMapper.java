package com.example.aicourse.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.aicourse.entity.Exam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface ExamMapper extends BaseMapper<Exam> {

    /**
     * 分页查询学生的考试列表
     */
    @Select("<script>" +
            "SELECT e.*, c.course_name, u.username as teacher_name " +
            "FROM exam e " +
            "LEFT JOIN t_course c ON e.course_id = c.id " +
            "LEFT JOIN t_user u ON c.teacher_id = u.id " +
            "WHERE e.course_id IN (" +
            "  SELECT cs.course_id FROM t_course_student cs WHERE cs.student_id = #{studentId}" +
            ") " +
            "<if test='status != null'> AND e.status = #{status} </if>" +
            "<if test='examType != null'> AND e.exam_type = #{examType} </if>" +
            "<if test='courseId != null'> AND e.course_id = #{courseId} </if>" +
            "ORDER BY e.start_time DESC" +
            "</script>")
    IPage<Exam> selectStudentExams(Page<Exam> page, 
                                  @Param("studentId") Long studentId,
                                  @Param("status") String status,
                                  @Param("examType") String examType,
                                  @Param("courseId") Long courseId);

    /**
     * 获取学生考试统计信息
     */
    @Select("SELECT " +
            "COUNT(*) as total_exams, " +
            "SUM(CASE WHEN e.status = 'COMPLETED' THEN 1 ELSE 0 END) as completed_exams, " +
            "SUM(CASE WHEN e.status = 'NOT_STARTED' OR e.status = 'IN_PROGRESS' THEN 1 ELSE 0 END) as pending_exams, " +
            "SUM(CASE WHEN e.status = 'EXPIRED' THEN 1 ELSE 0 END) as expired_exams " +
            "FROM exam e " +
            "WHERE e.course_id IN (" +
            "  SELECT cs.course_id FROM t_course_student cs WHERE cs.student_id = #{studentId}" +
            ")")
    Map<String, Object> getStudentExamStatistics(@Param("studentId") Long studentId);

    /**
     * 获取学生考试成绩统计
     */
    @Select("SELECT " +
            "AVG(es.score) as average_score, " +
            "SUM(es.score) as total_score, " +
            "SUM(CASE WHEN es.score >= e.passing_score THEN 1 ELSE 0 END) as passed_exams, " +
            "SUM(CASE WHEN es.score < e.passing_score THEN 1 ELSE 0 END) as failed_exams " +
            "FROM exam_submission es " +
            "JOIN exam e ON es.exam_id = e.id " +
            "WHERE es.student_id = #{studentId} AND es.status = 'GRADED'")
    Map<String, Object> getStudentExamScoreStatistics(@Param("studentId") Long studentId);
}