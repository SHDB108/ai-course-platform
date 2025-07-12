-- 学习进度表
CREATE TABLE IF NOT EXISTS t_study_progress (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL COMMENT '学生ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    total_progress INT DEFAULT 0 COMMENT '总体进度 (0-100)',
    
    -- 各模块进度
    video_progress INT DEFAULT 0 COMMENT '视频学习进度 (0-100)',
    task_progress INT DEFAULT 0 COMMENT '任务完成进度 (0-100)',
    exam_progress INT DEFAULT 0 COMMENT '考试完成进度 (0-100)',
    knowledge_progress INT DEFAULT 0 COMMENT '知识点掌握进度 (0-100)',
    
    -- 学习统计
    total_study_time INT DEFAULT 0 COMMENT '总学习时长(分钟)',
    today_study_time INT DEFAULT 0 COMMENT '今日学习时长(分钟)',
    week_study_time INT DEFAULT 0 COMMENT '本周学习时长(分钟)',
    month_study_time INT DEFAULT 0 COMMENT '本月学习时长(分钟)',
    
    -- 完成数量统计
    completed_videos INT DEFAULT 0 COMMENT '已完成视频数',
    total_videos INT DEFAULT 0 COMMENT '总视频数',
    completed_tasks INT DEFAULT 0 COMMENT '已完成任务数',
    total_tasks INT DEFAULT 0 COMMENT '总任务数',
    completed_exams INT DEFAULT 0 COMMENT '已完成考试数',
    total_exams INT DEFAULT 0 COMMENT '总考试数',
    mastered_knowledge INT DEFAULT 0 COMMENT '已掌握知识点数',
    total_knowledge INT DEFAULT 0 COMMENT '总知识点数',
    
    -- 学习状态
    status VARCHAR(50) DEFAULT 'ACTIVE' COMMENT '学习状态: ACTIVE, COMPLETED, SUSPENDED, DROPPED',
    last_study_time DATETIME COMMENT '最后学习时间',
    start_date DATETIME COMMENT '开始学习日期',
    expected_end_date DATETIME COMMENT '预期完成日期',
    
    gmt_create DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    
    UNIQUE KEY uk_student_course (student_id, course_id),
    INDEX idx_student_id (student_id),
    INDEX idx_course_id (course_id),
    INDEX idx_status (status),
    INDEX idx_last_study_time (last_study_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习进度表';

-- 学习会话表
CREATE TABLE IF NOT EXISTS t_study_session (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL COMMENT '学生ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    session_type VARCHAR(50) NOT NULL COMMENT '学习类型: VIDEO, TASK, EXAM, READING, QUIZ',
    resource_id BIGINT COMMENT '资源ID',
    resource_title VARCHAR(255) COMMENT '资源标题',
    
    -- 会话时间信息
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    duration INT COMMENT '学习时长(分钟)',
    effective_time INT COMMENT '有效学习时长(分钟)',
    
    -- 学习行为数据
    progress_data TEXT COMMENT '进度数据(JSON格式)',
    completion_rate INT COMMENT '完成率 (0-100)',
    interaction_data TEXT COMMENT '交互数据(JSON格式)',
    
    -- 学习结果
    result VARCHAR(50) COMMENT '学习结果: COMPLETED, PARTIAL, SKIPPED, FAILED, IN_PROGRESS',
    score INT COMMENT '得分',
    notes TEXT COMMENT '学习笔记',
    feedback TEXT COMMENT '反馈信息',
    
    -- 设备和环境信息
    device_type VARCHAR(50) COMMENT '设备类型: PC, MOBILE, TABLET',
    browser_info VARCHAR(255) COMMENT '浏览器信息',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    
    gmt_create DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    
    INDEX idx_student_id (student_id),
    INDEX idx_course_id (course_id),
    INDEX idx_session_type (session_type),
    INDEX idx_start_time (start_time),
    INDEX idx_result (result)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习会话表';

-- 学习计划表
CREATE TABLE IF NOT EXISTS t_study_plan (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL COMMENT '学生ID',
    course_id BIGINT COMMENT '课程ID',
    plan_name VARCHAR(255) NOT NULL COMMENT '计划名称',
    description TEXT COMMENT '计划描述',
    plan_type VARCHAR(50) NOT NULL COMMENT '计划类型: DAILY, WEEKLY, MONTHLY, CUSTOM',
    
    -- 计划时间
    start_date DATETIME COMMENT '开始日期',
    end_date DATETIME COMMENT '结束日期',
    target_date DATETIME COMMENT '目标完成日期',
    schedule_data TEXT COMMENT '学习安排(JSON格式)',
    
    -- 计划内容
    goals TEXT COMMENT '学习目标(JSON数组)',
    milestones TEXT COMMENT '里程碑(JSON数组)',
    estimated_hours INT COMMENT '预计学习时长(小时)',
    priority VARCHAR(50) DEFAULT 'MEDIUM' COMMENT '优先级: HIGH, MEDIUM, LOW',
    
    -- 计划状态
    status VARCHAR(50) DEFAULT 'ACTIVE' COMMENT '状态: DRAFT, ACTIVE, COMPLETED, PAUSED, CANCELLED',
    progress INT DEFAULT 0 COMMENT '完成进度 (0-100)',
    completed_tasks INT DEFAULT 0 COMMENT '已完成任务数',
    total_tasks INT DEFAULT 0 COMMENT '总任务数',
    
    -- 提醒设置
    reminder_enabled BOOLEAN DEFAULT FALSE COMMENT '是否启用提醒',
    reminder_frequency VARCHAR(50) COMMENT '提醒频率: DAILY, WEEKLY, CUSTOM',
    reminder_time VARCHAR(50) COMMENT '提醒时间',
    
    -- 智能推荐
    is_ai_generated BOOLEAN DEFAULT FALSE COMMENT '是否AI生成',
    ai_recommend_reason TEXT COMMENT 'AI推荐理由',
    last_ai_update DATETIME COMMENT '最后AI更新时间',
    
    gmt_create DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    
    INDEX idx_student_id (student_id),
    INDEX idx_course_id (course_id),
    INDEX idx_status (status),
    INDEX idx_priority (priority),
    INDEX idx_target_date (target_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习计划表';

-- 知识点掌握进度表
CREATE TABLE IF NOT EXISTS t_knowledge_point_progress (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL COMMENT '学生ID',
    knowledge_point_id BIGINT NOT NULL COMMENT '知识点ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    
    -- 掌握程度
    mastery_level VARCHAR(50) DEFAULT 'UNKNOWN' COMMENT '掌握等级: UNKNOWN, LEARNING, PARTIAL, MASTERED, EXPERT',
    mastery_score INT DEFAULT 0 COMMENT '掌握分数 (0-100)',
    confidence INT DEFAULT 0 COMMENT '置信度 (0-100)',
    
    -- 学习统计
    study_count INT DEFAULT 0 COMMENT '学习次数',
    test_count INT DEFAULT 0 COMMENT '测试次数',
    correct_count INT DEFAULT 0 COMMENT '正确次数',
    wrong_count INT DEFAULT 0 COMMENT '错误次数',
    accuracy DOUBLE DEFAULT 0.0 COMMENT '正确率',
    
    -- 时间记录
    total_study_time INT DEFAULT 0 COMMENT '总学习时长(分钟)',
    first_study_time DATETIME COMMENT '首次学习时间',
    last_study_time DATETIME COMMENT '最后学习时间',
    mastered_time DATETIME COMMENT '掌握时间',
    
    -- 学习路径
    learning_path TEXT COMMENT '学习路径(JSON数组)',
    weakness_areas TEXT COMMENT '薄弱环节(JSON数组)',
    related_resources TEXT COMMENT '相关资源(JSON数组)',
    
    -- 复习计划
    next_review_time DATETIME COMMENT '下次复习时间',
    review_interval INT DEFAULT 1 COMMENT '复习间隔(天)',
    review_count INT DEFAULT 0 COMMENT '复习次数',
    review_status VARCHAR(50) DEFAULT 'SCHEDULED' COMMENT '复习状态: SCHEDULED, OVERDUE, COMPLETED',
    
    gmt_create DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    
    UNIQUE KEY uk_student_knowledge (student_id, knowledge_point_id),
    INDEX idx_student_id (student_id),
    INDEX idx_course_id (course_id),
    INDEX idx_mastery_level (mastery_level),
    INDEX idx_review_status (review_status),
    INDEX idx_next_review_time (next_review_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识点掌握进度表';