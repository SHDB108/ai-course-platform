package com.example.aicourse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.aicourse.entity.Grade;
import com.example.aicourse.entity.SemesterGPA;
import com.example.aicourse.entity.Course;
import com.example.aicourse.entity.User;
import com.example.aicourse.entity.Exam;
import com.example.aicourse.entity.Task;
import com.example.aicourse.repository.GradeMapper;
import com.example.aicourse.repository.CourseMapper;
import com.example.aicourse.repository.UserMapper;
import com.example.aicourse.repository.ExamMapper;
import com.example.aicourse.repository.TaskMapper;
import com.example.aicourse.service.GradeService;
import com.example.aicourse.vo.grade.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GradeServiceImpl implements GradeService {

    @Autowired
    private GradeMapper gradeMapper;
    
    @Autowired
    private CourseMapper courseMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private ExamMapper examMapper;
    
    @Autowired
    private TaskMapper taskMapper;

    @Override
    public IPage<GradeVO> getStudentGrades(Long studentId, Integer pageNum, Integer pageSize,
                                           String gradeType, String semester, Long courseId) {
        Page<Grade> page = new Page<>(pageNum, pageSize);
        
        IPage<Grade> gradePage = gradeMapper.selectStudentGrades(page, studentId, gradeType, semester, courseId);
        
        // 转换为VO
        IPage<GradeVO> voPage = new Page<>();
        BeanUtils.copyProperties(gradePage, voPage);
        
        List<GradeVO> gradeVOs = new ArrayList<>();
        for (Grade grade : gradePage.getRecords()) {
            GradeVO vo = convertToVO(grade);
            gradeVOs.add(vo);
        }
        voPage.setRecords(gradeVOs);
        
        return voPage;
    }

    @Override
    public CourseGradeVO getStudentCourseGrades(Long studentId, Long courseId) {
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }
        
        CourseGradeVO vo = new CourseGradeVO();
        vo.setCourseId(courseId);
        vo.setCourseName(course.getCourseName());
        vo.setCourseCode(course.getCourseCode());
        vo.setCredits(course.getCredits());
        
        // 获取教师信息
        User teacher = userMapper.selectById(course.getTeacherId());
        if (teacher != null) {
            vo.setTeacherName(teacher.getUsername());
        }
        
        // 获取该课程的所有成绩
        QueryWrapper<Grade> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId)
               .eq("course_id", courseId)
               .eq("status", "GRADED");
        
        List<Grade> grades = gradeMapper.selectList(wrapper);
        List<GradeVO> gradeVOs = grades.stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
        
        vo.setGrades(gradeVOs);
        
        // 计算加权平均成绩
        double totalWeightedScore = 0;
        double totalWeight = 0;
        for (Grade grade : grades) {
            totalWeightedScore += grade.getScore() * grade.getWeight();
            totalWeight += grade.getWeight();
        }
        
        if (totalWeight > 0) {
            vo.setFinalGrade(totalWeightedScore / totalWeight);
            vo.setFinalPercentage((totalWeightedScore / totalWeight));
            vo.setFinalLetterGrade(calculateLetterGrade(vo.getFinalGrade()));
            vo.setFinalGradePoint(calculateGradePoint(vo.getFinalLetterGrade()));
        }
        
        // 获取课程排名
        Map<String, Object> ranking = gradeMapper.getStudentCourseRanking(studentId, courseId);
        if (ranking != null) {
            vo.setRank(((Number) ranking.get("rank")).intValue());
            vo.setClassSize(((Number) ranking.get("class_size")).intValue());
        }
        
        return vo;
    }

    @Override
    public GradeStatisticsVO getStudentGradeStatistics(Long studentId, String semester) {
        Map<String, Object> stats = gradeMapper.getStudentGradeStatistics(studentId, semester);
        Map<String, Object> distribution = gradeMapper.getStudentGradeDistribution(studentId);
        
        GradeStatisticsVO statisticsVO = new GradeStatisticsVO();
        
        if (stats != null) {
            statisticsVO.setTotalCourses(((Number) stats.getOrDefault("total_courses", 0)).intValue());
            statisticsVO.setCompletedCourses(((Number) stats.getOrDefault("completed_courses", 0)).intValue());
            statisticsVO.setInProgressCourses(((Number) stats.getOrDefault("in_progress_courses", 0)).intValue());
            statisticsVO.setFailedCourses(((Number) stats.getOrDefault("failed_courses", 0)).intValue());
            statisticsVO.setOverallGPA(((Number) stats.getOrDefault("overall_gpa", 0)).doubleValue());
            statisticsVO.setTotalCredits(((Number) stats.getOrDefault("total_credits", 0)).intValue());
            statisticsVO.setEarnedCredits(((Number) stats.getOrDefault("earned_credits", 0)).intValue());
            statisticsVO.setAverageScore(((Number) stats.getOrDefault("average_score", 0)).doubleValue());
            statisticsVO.setHighestScore(((Number) stats.getOrDefault("highest_score", 0)).doubleValue());
            statisticsVO.setLowestScore(((Number) stats.getOrDefault("lowest_score", 0)).doubleValue());
        }
        
        if (distribution != null) {
            Map<String, Integer> gradeDistribution = new HashMap<>();
            gradeDistribution.put("A", ((Number) distribution.getOrDefault("grade_a", 0)).intValue());
            gradeDistribution.put("B", ((Number) distribution.getOrDefault("grade_b", 0)).intValue());
            gradeDistribution.put("C", ((Number) distribution.getOrDefault("grade_c", 0)).intValue());
            gradeDistribution.put("D", ((Number) distribution.getOrDefault("grade_d", 0)).intValue());
            gradeDistribution.put("F", ((Number) distribution.getOrDefault("grade_f", 0)).intValue());
            statisticsVO.setGradesDistribution(gradeDistribution);
        }
        
        return statisticsVO;
    }

    @Override
    public List<GPAHistoryVO> getStudentGPAHistory(Long studentId) {
        List<SemesterGPA> gpaRecords = gradeMapper.getStudentGPAHistory(studentId);
        
        return gpaRecords.stream().map(record -> {
            GPAHistoryVO vo = new GPAHistoryVO();
            BeanUtils.copyProperties(record, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<GradeTrendVO> getStudentGradeTrend(Long studentId, Integer months) {
        if (months == null) months = 12;
        
        List<Map<String, Object>> trendData = gradeMapper.getStudentGradeTrend(studentId, months);
        
        return trendData.stream().map(data -> {
            GradeTrendVO vo = new GradeTrendVO();
            vo.setMonth((String) data.get("month"));
            vo.setAverageScore(((Number) data.getOrDefault("average_score", 0)).doubleValue());
            vo.setGpa(((Number) data.getOrDefault("gpa", 0)).doubleValue());
            vo.setCourseCount(((Number) data.getOrDefault("course_count", 0)).intValue());
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getStudentCourseRanking(Long studentId, Long courseId) {
        return gradeMapper.getStudentCourseRanking(studentId, courseId);
    }

    @Override
    public Map<String, Object> getStudentSemesterSummary(Long studentId, String semester) {
        // 获取学期内的所有课程成绩
        QueryWrapper<Grade> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId);
        // 这里需要根据实际的数据库结构来查询特定学期的成绩
        
        List<Grade> semesterGrades = gradeMapper.selectList(wrapper);
        
        // 按课程分组并计算各课程成绩
        Map<Long, List<Grade>> gradesByCourse = semesterGrades.stream()
            .collect(Collectors.groupingBy(Grade::getCourseId));
        
        List<CourseGradeVO> courseGrades = new ArrayList<>();
        double totalGradePoints = 0;
        int totalCredits = 0;
        int earnedCredits = 0;
        
        for (Map.Entry<Long, List<Grade>> entry : gradesByCourse.entrySet()) {
            Long courseId = entry.getKey();
            List<Grade> grades = entry.getValue();
            
            Course course = courseMapper.selectById(courseId);
            if (course != null) {
                CourseGradeVO courseGrade = getStudentCourseGrades(studentId, courseId);
                courseGrades.add(courseGrade);
                
                totalCredits += course.getCredits();
                if (courseGrade.getFinalGradePoint() != null && courseGrade.getFinalGradePoint() >= 1.0) {
                    earnedCredits += course.getCredits();
                    totalGradePoints += courseGrade.getFinalGradePoint() * course.getCredits();
                }
            }
        }
        
        double gpa = totalCredits > 0 ? totalGradePoints / totalCredits : 0;
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("semester", semester);
        summary.put("gpa", gpa);
        summary.put("totalCredits", totalCredits);
        summary.put("earnedCredits", earnedCredits);
        summary.put("courses", courseGrades);
        
        return summary;
    }

    @Override
    public byte[] exportGradeReport(Long studentId, String format, String semester, Long courseId) {
        // 这里应该实现PDF或Excel导出逻辑
        // 为了简化，返回模拟数据
        String content = "学生成绩报告 - 格式: " + format;
        return content.getBytes();
    }

    private GradeVO convertToVO(Grade grade) {
        GradeVO vo = new GradeVO();
        BeanUtils.copyProperties(grade, vo);
        
        // 获取课程信息
        Course course = courseMapper.selectById(grade.getCourseId());
        if (course != null) {
            vo.setCourseName(course.getCourseName());
            
            // 获取教师信息
            User teacher = userMapper.selectById(course.getTeacherId());
            if (teacher != null) {
                vo.setTeacherName(teacher.getUsername());
            }
        }
        
        // 获取考试或任务标题
        if (grade.getExamId() != null) {
            Exam exam = examMapper.selectById(grade.getExamId());
            if (exam != null) {
                vo.setExamTitle(exam.getExamTitle());
            }
        }
        
        if (grade.getTaskId() != null) {
            Task task = taskMapper.selectById(grade.getTaskId());
            if (task != null) {
                vo.setTaskTitle(task.getTitle());
            }
        }
        
        return vo;
    }

    private String calculateLetterGrade(Double score) {
        if (score >= 90) return "A";
        if (score >= 80) return "B";
        if (score >= 70) return "C";
        if (score >= 60) return "D";
        return "F";
    }

    private Double calculateGradePoint(String letterGrade) {
        switch (letterGrade) {
            case "A": return 4.0;
            case "B": return 3.0;
            case "C": return 2.0;
            case "D": return 1.0;
            case "F": return 0.0;
            default: return 0.0;
        }
    }
}