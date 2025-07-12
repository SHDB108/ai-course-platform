package com.example.aicourse.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.aicourse.entity.Grade;
import com.example.aicourse.entity.SemesterGPA;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface GradeMapper extends BaseMapper<Grade> {

    /**
     * 分页查询学生成绩列表
     */
    @Select("<script>" +
            "SELECT g.*, c.course_name, u.username as teacher_name, " +
            "CASE WHEN g.exam_id IS NOT NULL THEN e.exam_title " +
            "     WHEN g.task_id IS NOT NULL THEN t.title END as task_title " +
            "FROM grade g " +
            "LEFT JOIN t_course c ON g.course_id = c.id " +
            "LEFT JOIN t_user u ON c.teacher_id = u.id " +
            "LEFT JOIN exam e ON g.exam_id = e.id " +
            "LEFT JOIN t_task t ON g.task_id = t.id " +
            "WHERE g.student_id = #{studentId} " +
            "<if test='gradeType != null'> AND g.grade_type = #{gradeType} </if>" +
            "<if test='semester != null'> AND c.semester = #{semester} </if>" +
            "<if test='courseId != null'> AND g.course_id = #{courseId} </if>" +
            "ORDER BY g.graded_date DESC" +
            "</script>")
    IPage<Grade> selectStudentGrades(Page<Grade> page,
                                    @Param("studentId") Long studentId,
                                    @Param("gradeType") String gradeType,
                                    @Param("semester") String semester,
                                    @Param("courseId") Long courseId);

    /**
     * 获取学生成绩统计信息
     */
    @Select("SELECT " +
            "COUNT(DISTINCT g.course_id) as total_courses, " +
            "COUNT(DISTINCT CASE WHEN c.semester = #{semester} THEN g.course_id END) as completed_courses, " +
            "0 as in_progress_courses, " +
            "0 as failed_courses, " +
            "AVG(g.grade_point) as overall_gpa, " +
            "SUM(c.credits) as total_credits, " +
            "SUM(CASE WHEN g.grade_point >= 1.0 THEN c.credits ELSE 0 END) as earned_credits, " +
            "AVG(g.score) as average_score, " +
            "MAX(g.score) as highest_score, " +
            "MIN(g.score) as lowest_score " +
            "FROM grade g " +
            "JOIN t_course c ON g.course_id = c.id " +
            "WHERE g.student_id = #{studentId} AND g.status = 'GRADED'")
    Map<String, Object> getStudentGradeStatistics(@Param("studentId") Long studentId, @Param("semester") String semester);

    /**
     * 获取学生成绩分布
     */
    @Select("SELECT " +
            "SUM(CASE WHEN g.letter_grade = 'A' THEN 1 ELSE 0 END) as grade_a, " +
            "SUM(CASE WHEN g.letter_grade = 'B' THEN 1 ELSE 0 END) as grade_b, " +
            "SUM(CASE WHEN g.letter_grade = 'C' THEN 1 ELSE 0 END) as grade_c, " +
            "SUM(CASE WHEN g.letter_grade = 'D' THEN 1 ELSE 0 END) as grade_d, " +
            "SUM(CASE WHEN g.letter_grade = 'F' THEN 1 ELSE 0 END) as grade_f " +
            "FROM grade g " +
            "WHERE g.student_id = #{studentId} AND g.status = 'GRADED'")
    Map<String, Object> getStudentGradeDistribution(@Param("studentId") Long studentId);

    /**
     * 获取学生课程排名
     */
    @Select("SELECT " +
            "COUNT(*) + 1 as rank, " +
            "(SELECT COUNT(*) FROM grade WHERE course_id = #{courseId} AND status = 'GRADED') as class_size, " +
            "g.score " +
            "FROM grade g2 " +
            "JOIN grade g ON g.student_id = #{studentId} AND g.course_id = #{courseId} AND g.status = 'GRADED' " +
            "WHERE g2.course_id = #{courseId} AND g2.status = 'GRADED' AND g2.score > g.score")
    Map<String, Object> getStudentCourseRanking(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    /**
     * 获取学生GPA历史记录
     */
    @Select("SELECT * FROM semester_gpa WHERE student_id = #{studentId} ORDER BY created_at DESC")
    List<SemesterGPA> getStudentGPAHistory(@Param("studentId") Long studentId);

    /**
     * 获取学生成绩趋势（按月统计）
     */
    @Select("SELECT " +
            "DATE_FORMAT(g.graded_date, '%Y-%m') as month, " +
            "AVG(g.score) as average_score, " +
            "AVG(g.grade_point) as gpa, " +
            "COUNT(DISTINCT g.course_id) as course_count " +
            "FROM grade g " +
            "WHERE g.student_id = #{studentId} AND g.status = 'GRADED' " +
            "AND g.graded_date >= DATE_SUB(NOW(), INTERVAL #{months} MONTH) " +
            "GROUP BY DATE_FORMAT(g.graded_date, '%Y-%m') " +
            "ORDER BY month ASC")
    List<Map<String, Object>> getStudentGradeTrend(@Param("studentId") Long studentId, @Param("months") Integer months);
}