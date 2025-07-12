package com.example.aicourse.vo.grade;

import lombok.Data;

import java.util.List;

@Data
public class GradeBreakdownVO {
    private List<GradeVO> examGrades; // 考试成绩
    private List<GradeVO> assignmentGrades; // 作业成绩
    private List<GradeVO> quizGrades; // 测验成绩
    private List<GradeVO> projectGrades; // 项目成绩
    private List<GradeVO> participationGrades; // 参与度成绩

    private Double examAverage;
    private Double assignmentAverage;
    private Double quizAverage;
    private Double projectAverage;
    private Double participationAverage;

    private Double examWeight;
    private Double assignmentWeight;
    private Double quizWeight;
    private Double projectWeight;
    private Double participationWeight;
}