-- 创建学习推荐表
CREATE TABLE IF NOT EXISTS t_learning_recommendation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL COMMENT '学生ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    recommendation_type VARCHAR(50) NOT NULL COMMENT '推荐类型: KNOWLEDGE_POINT, REVIEW_MATERIAL, EXERCISE, VIDEO, READING',
    target_id BIGINT COMMENT '目标资源ID (如知识点ID、任务ID等)',
    reason TEXT COMMENT '推荐理由和详细说明',
    
    -- 推荐状态
    is_dismissed INT DEFAULT 0 COMMENT '状态: 0=活跃, 1=用户已忽略, 2=系统判定为过时',
    priority INT DEFAULT 5 COMMENT '优先级: 1-10 (10为最高)',
    confidence DOUBLE DEFAULT 0.0 COMMENT '推荐置信度 (0.0-1.0)',
    
    -- 推荐元数据
    ai_generated BOOLEAN DEFAULT FALSE COMMENT '是否AI生成',
    algorithm_version VARCHAR(50) COMMENT '推荐算法版本',
    context_data TEXT COMMENT '推荐上下文数据(JSON格式)',
    
    -- 用户反馈
    user_feedback VARCHAR(50) COMMENT '用户反馈: HELPFUL, NOT_HELPFUL, IRRELEVANT',
    completion_status VARCHAR(50) COMMENT '完成状态: NOT_STARTED, IN_PROGRESS, COMPLETED',
    completion_time DATETIME COMMENT '完成时间',
    
    -- 有效期
    valid_from DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '生效时间',
    valid_until DATETIME COMMENT '失效时间',
    
    gmt_create DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    
    INDEX idx_student_id (student_id),
    INDEX idx_course_id (course_id),
    INDEX idx_recommendation_type (recommendation_type),
    INDEX idx_is_dismissed (is_dismissed),
    INDEX idx_priority (priority),
    INDEX idx_valid_period (valid_from, valid_until),
    INDEX idx_student_course (student_id, course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习推荐表';

-- 为Mapper中的查询创建对应的XML文件内容
-- 这个查询会被放入 LearningRecommendationMapper.xml 中