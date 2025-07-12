package com.example.aicourse.vo.analytics;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 数据分析概览VO
 */
@Data
public class AnalyticsOverviewVO {
    
    /**
     * 总用户数
     */
    private Integer totalUsers;
    
    /**
     * 总课程数
     */
    private Integer totalCourses;
    
    /**
     * 活跃课程数
     */
    private Integer activeCourses;
    
    /**
     * 总选课数
     */
    private Integer totalEnrollments;
    
    /**
     * 活跃用户数
     */
    private Integer activeUsers;
    
    /**
     * 本月新增用户数
     */
    private Integer newUsersThisMonth;
    
    /**
     * 本月新增课程数
     */
    private Integer newCoursesThisMonth;
    
    /**
     * 完成率
     */
    private Double completionRate;
    
    /**
     * 平均评分
     */
    private Double avgRating;
    
    /**
     * 系统健康状态
     */
    private Map<String, Object> systemHealth;
    
    /**
     * 用户分布
     */
    private Map<String, Integer> userDistribution;
    
    /**
     * 课程统计
     */
    private Map<String, Object> courseStats;
    
    /**
     * 最近活动
     */
    private List<Map<String, Object>> recentActivities;
    
    /**
     * 热门课程
     */
    private List<Map<String, Object>> popularCourses;
}