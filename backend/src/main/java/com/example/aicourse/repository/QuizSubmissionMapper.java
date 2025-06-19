package com.example.aicourse.repository;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.aicourse.entity.QuizSubmission;
import com.example.aicourse.vo.ScoreRowVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper public interface QuizSubmissionMapper extends BaseMapper<QuizSubmission>{
    @Select(
            "SELECT " +
                    "  qs.student_id AS studentId, " +
                    "  qs.score      AS score       " +
                    "FROM t_quiz_submission qs " +
                    "  JOIN t_quiz_paper qp ON qs.paper_id = qp.id " +
                    "WHERE qp.course_id = #{courseId}"
    )
    List<ScoreRowVO> selectRows(@Param("courseId") Long courseId);
}