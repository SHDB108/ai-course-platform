-- 创建角色表
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE COMMENT '角色名称（唯一标识）',
    display_name VARCHAR(100) NOT NULL COMMENT '角色显示名称',
    description TEXT COMMENT '角色描述',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '角色状态：1-启用，0-禁用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT='角色表';

-- 创建权限表
CREATE TABLE IF NOT EXISTS permissions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE COMMENT '权限名称（唯一标识）',
    display_name VARCHAR(200) NOT NULL COMMENT '权限显示名称',
    description TEXT COMMENT '权限描述',
    resource VARCHAR(100) COMMENT '资源标识',
    action VARCHAR(50) COMMENT '操作类型',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT='权限表';

-- 创建角色权限关联表
CREATE TABLE IF NOT EXISTS role_permissions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE,
    UNIQUE KEY uk_role_permission (role_id, permission_id)
) COMMENT='角色权限关联表';

-- 创建课程分类表
CREATE TABLE IF NOT EXISTS course_categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '分类名称',
    description TEXT COMMENT '分类描述',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-启用，INACTIVE-禁用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT='课程分类表';

-- 插入初始角色数据
INSERT IGNORE INTO roles (name, display_name, description, status) VALUES
('ADMIN', '管理员', '系统管理员，拥有所有权限', 1),
('TEACHER', '教师', '教师角色，可以管理课程和学生', 1),
('STUDENT', '学生', '学生角色，可以学习课程', 1);

-- 插入初始权限数据
INSERT IGNORE INTO permissions (name, display_name, description, resource, action) VALUES
('admin:user:read', '查看用户', '查看用户列表和详情', 'admin:user', 'read'),
('admin:user:write', '管理用户', '创建、修改、删除用户', 'admin:user', 'write'),
('admin:role:read', '查看角色', '查看角色列表和权限', 'admin:role', 'read'),
('admin:role:write', '管理角色', '创建、修改、删除角色和权限', 'admin:role', 'write'),
('course:read', '查看课程', '查看课程列表和详情', 'course', 'read'),
('course:write', '管理课程', '创建、修改、删除课程', 'course', 'write'),
('course:teach', '教授课程', '教授和管理自己的课程', 'course', 'teach'),
('quiz:read', '查看测验', '查看测验内容和结果', 'quiz', 'read'),
('quiz:write', '管理测验', '创建、修改、删除测验', 'quiz', 'write'),
('task:read', '查看任务', '查看任务内容和提交', 'task', 'read'),
('task:write', '管理任务', '创建、修改、删除任务', 'task', 'write');

-- 插入初始角色权限关联
INSERT IGNORE INTO role_permissions (role_id, permission_id) 
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.name = 'ADMIN';

INSERT IGNORE INTO role_permissions (role_id, permission_id) 
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.name = 'TEACHER' AND p.name IN ('course:read', 'course:write', 'course:teach', 'quiz:read', 'quiz:write', 'task:read', 'task:write');

INSERT IGNORE INTO role_permissions (role_id, permission_id) 
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.name = 'STUDENT' AND p.name IN ('course:read', 'quiz:read', 'task:read');

-- 插入初始课程分类数据
INSERT IGNORE INTO course_categories (name, description, sort_order, status) VALUES
('计算机科学', '计算机科学相关课程', 1, 'ACTIVE'),
('数学', '数学相关课程', 2, 'ACTIVE'),
('物理', '物理相关课程', 3, 'ACTIVE'),
('英语', '英语相关课程', 4, 'ACTIVE'),
('人工智能', '人工智能和机器学习相关课程', 5, 'ACTIVE');