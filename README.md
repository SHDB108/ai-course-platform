# 🎓 AI智慧课程平台

> 基于Vue 3 + Spring Boot的现代化在线教育管理系统

[![Vue.js](https://img.shields.io/badge/Vue.js-3.4.15-green.svg)](https://vuejs.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.6-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Element Plus](https://img.shields.io/badge/Element%20Plus-2.5.5-blue.svg)](https://element-plus.org/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.3.3-blue.svg)](https://www.typescriptlang.org/)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

## 📖 项目简介

AI智慧课程平台是一个功能完善的在线教育管理系统，集成了课程管理、智能题库、AI试卷生成、学习进度跟踪等功能。系统采用前后端分离架构，为教育机构提供完整的数字化教学解决方案。

### ✨ 核心特色

- 🤖 **AI智能试卷生成** - 基于题库内容智能生成个性化试卷
- 📚 **智能题库管理** - 支持多种题型，标签分类，难度配置
- 👥 **多角色权限管理** - 管理员、教师、学生三级权限体系
- 📊 **数据分析可视化** - 学习进度、成绩统计、课程分析
- 🎯 **个性化学习推荐** - 基于学习行为的智能推荐系统
- 📱 **响应式设计** - 支持PC、平板、手机多端访问

## 🏗️ 技术架构

### 前端技术栈
- **框架**: Vue 3.4.15 + TypeScript 5.3.3
- **UI库**: Element Plus 2.5.5
- **构建工具**: Vite 5.0.12
- **状态管理**: Pinia 2.3.1
- **路由管理**: Vue Router 4.5.1
- **图表库**: ECharts 5.6.0
- **样式**: UnoCSS + Less

### 后端技术栈
- **框架**: Spring Boot 3.2.6
- **数据库**: MySQL 8.0+
- **ORM**: MyBatis-Plus 3.5.6
- **安全**: Spring Security + JWT
- **缓存**: Redis
- **文件存储**: MinIO
- **构建工具**: Maven

## 🚀 快速开始

### 环境要求

- Node.js >= 18.0.0
- Java 17+
- MySQL 8.0+
- Redis 6.0+
- pnpm >= 8.1.0

### 安装部署

#### 1. 克隆项目
```bash
git clone https://github.com/SHDB108/ai-course-platform.git
cd ai-course-platform
```

#### 2. 后端部署
```bash
cd backend

# 配置数据库连接 (修改 application.yml)
# 创建数据库并导入初始化SQL

# 编译运行
mvn clean compile
mvn spring-boot:run
```

#### 3. 前端部署
```bash
cd frontend-tem

# 安装依赖
pnpm install

# 开发环境启动
pnpm dev

# 生产环境构建
pnpm build:pro
```

#### 4. 访问应用
- 前端地址: http://localhost:3000
- 后端API: http://localhost:8080

### 默认账户
```
管理员账户:
用户名: admin123
密码: admin123

教师账户:
用户名: teacher
密码: 12345

学生账户:
用户名: student
密码: 12345
```

## 📋 功能模块

### 🎯 管理员模块
- **用户管理** - 用户增删改查、角色分配、权限管理
- **课程管理** - 课程发布、分类管理、选课管理
- **系统管理** - 系统配置、日志管理、数据备份
- **数据分析** - 用户统计、课程分析、系统监控

### 👨‍🏫 教师模块
- **教学管理** - 我的课程、学生管理、任务发布
- **内容管理** - 智能题库、AI试卷生成、课件上传
- **批改中心** - 作业批改、成绩管理、学习统计
- **数据概览** - 教学数据、学生表现、课程进度

### 👨‍🎓 学生模块
- **学习中心** - 我的课程、学习进度、任务提交
- **考试系统** - 在线考试、成绩查看、错题回顾
- **学习工具** - 知识图谱、学习推荐、学习计划
- **个人中心** - 学习统计、成绩分析、个人设置

## 🎨 界面预览

### 仪表板
![仪表板](docs/screenshots/dashboard.png)

### 智能题库
![题库管理](docs/screenshots/question-bank.png)

### AI试卷生成
![试卷生成](docs/screenshots/ai-paper-generation.png)

### 学习进度
![学习进度](docs/screenshots/learning-progress.png)

## 🔧 开发指南

### 项目结构
```
ai-course-platform/
├── frontend-tem/                 # 前端项目
│   ├── src/
│   │   ├── api/                 # API接口
│   │   ├── components/          # 公共组件
│   │   ├── views/               # 页面组件
│   │   ├── router/              # 路由配置
│   │   ├── store/               # 状态管理
│   │   └── utils/               # 工具函数
│   └── package.json
├── backend/                      # 后端项目
│   ├── src/main/java/
│   │   └── com/example/aicourse/
│   │       ├── controller/      # 控制器
│   │       ├── service/         # 服务层
│   │       ├── entity/          # 实体类
│   │       ├── repository/      # 数据访问层
│   │       └── config/          # 配置类
│   └── pom.xml
└── docs/                        # 文档
```

### API接口规范
所有API接口统一使用 `/api/v1` 前缀，返回格式：
```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

### 开发规范
- 遵循Vue 3 Composition API规范
- 使用TypeScript进行类型约束
- 遵循RESTful API设计原则
- 代码需要通过ESLint和Prettier检查

## 🧪 测试

### 前端测试
```bash
cd frontend-tem
pnpm test
```

### 后端测试
```bash
cd backend
mvn test
```

## 📦 部署

### Docker部署
```bash
# 构建镜像
docker-compose build

# 启动服务
docker-compose up -d
```

### 传统部署
1. 前端构建后部署到Nginx
2. 后端打包为JAR文件部署到服务器
3. 配置MySQL和Redis连接
