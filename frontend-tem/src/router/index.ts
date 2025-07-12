import type { RouteRecordRaw } from 'vue-router'
import { createRouter, createWebHashHistory } from 'vue-router'
import type { App } from 'vue'
import { Layout } from '@/utils/routerHelper'

// 静态路由，所有用户都可以访问
export const constantRoutes: AppRouteRecordRaw[] = [
  {
    path: '/login',
    component: () => import('@/views/Login/Login.vue'),
    name: 'Login',
    meta: {
      hidden: true,
      title: '登录',
      noTagsView: true
    }
  },
  {
    path: '/register',
    component: () => import('@/views/Login/components/RegisterForm.vue'),
    name: 'Register',
    meta: {
      hidden: true,
      title: '注册',
      noTagsView: true
    }
  },
  {
    path: '/404',
    component: () => import('@/views/Error/404.vue'),
    name: 'NoFind',
    meta: {
      hidden: true,
      title: '404',
      noTagsView: true
    }
  }
]

// 动态路由，需要根据用户角色进行过滤
export const asyncRoutes: AppRouteRecordRaw[] = [
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard/overview',
    name: 'Root',
    meta: {}
  },

  // ==================== 通用首页 ====================
  {
    path: '/dashboard',
    component: Layout,
    name: 'Dashboard',
    meta: {
      title: '首页',
      icon: 'ant-design:dashboard-filled',
      alwaysShow: true
    },
    children: [
      {
        path: 'overview',
        component: () => import('@/views/Dashboard/Analysis.vue'),
        name: 'Overview',
        meta: {
          title: '数据概览',
          roles: ['ADMIN', 'TEACHER', 'STUDENT']
        }
      },
      {
        path: 'workplace',
        component: () => import('@/views/Dashboard/Workplace.vue'),
        name: 'Workplace',
        meta: {
          title: '管理工作台',
          roles: ['ADMIN']
        }
      }
    ]
  },

  // ==================== 管理员专用模块 ====================
  {
    path: '/admin',
    component: Layout,
    name: 'AdminModule',
    meta: {
      title: '系统管理',
      icon: 'ant-design:setting-filled',
      alwaysShow: true,
      roles: ['ADMIN']
    },
    children: [
      {
        path: 'users',
        component: () => import('@/views/Admin/UserManagement.vue'),
        name: 'UserManagement',
        meta: {
          title: '用户管理',
          roles: ['ADMIN']
        }
      },
      {
        path: 'teachers',
        component: () => import('@/views/Admin/TeacherManagement.vue'),
        name: 'TeacherManagement',
        meta: {
          title: '教师管理',
          roles: ['ADMIN']
        }
      },
      {
        path: 'students',
        component: () => import('@/views/Admin/StudentManagement.vue'),
        name: 'StudentManagement',
        meta: {
          title: '学生管理',
          roles: ['ADMIN']
        }
      },
      {
        path: 'roles',
        component: () => import('@/views/Admin/RoleManagement.vue'),
        name: 'RoleManagement',
        meta: {
          title: '角色权限',
          roles: ['ADMIN']
        }
      },
      {
        path: 'user-create',
        component: () => import('@/views/Admin/UserCreate.vue'),
        name: 'UserCreate',
        meta: {
          title: '新建用户',
          roles: ['ADMIN']
        }
      }
    ]
  },

  {
    path: '/course-admin',
    component: Layout,
    name: 'CourseAdminModule',
    meta: {
      title: '课程管理',
      icon: 'ant-design:book-filled',
      alwaysShow: true,
      roles: ['ADMIN']
    },
    children: [
      {
        path: 'courses',
        component: () => import('@/views/Admin/CourseManagement.vue'),
        name: 'AdminCourseManagement',
        meta: {
          title: '课程管理',
          roles: ['ADMIN']
        }
      },
      {
        path: 'categories',
        component: () => import('@/views/Admin/CourseCategoryManagement.vue'),
        name: 'CourseCategoryManagement',
        meta: {
          title: '课程分类',
          roles: ['ADMIN']
        }
      },
      {
        path: 'enrollment',
        component: () => import('@/views/Admin/EnrollmentManagement.vue'),
        name: 'EnrollmentManagement',
        meta: {
          title: '选课管理',
          roles: ['ADMIN']
        }
      }
    ]
  },

  {
    path: '/system',
    component: Layout,
    name: 'SystemModule',
    meta: {
      title: '系统设置',
      icon: 'ant-design:control-filled',
      alwaysShow: true,
      roles: ['ADMIN']
    },
    children: [
      {
        path: 'config',
        component: () => import('@/views/Admin/SystemConfig.vue'),
        name: 'SystemConfig',
        meta: {
          title: '系统配置',
          roles: ['ADMIN']
        }
      },
      {
        path: 'logs',
        component: () => import('@/views/Admin/SystemLogs.vue'),
        name: 'SystemLogs',
        meta: {
          title: '系统日志',
          roles: ['ADMIN']
        }
      },
      {
        path: 'backup',
        component: () => import('@/views/Admin/SystemBackup.vue'),
        name: 'SystemBackup',
        meta: {
          title: '数据备份',
          roles: ['ADMIN']
        }
      }
    ]
  },

  {
    path: '/analytics',
    component: Layout,
    name: 'AnalyticsModule',
    meta: {
      title: '数据分析',
      icon: 'ant-design:bar-chart-outlined',
      alwaysShow: true,
      roles: ['ADMIN']
    },
    children: [
      {
        path: 'overview',
        component: () => import('@/views/Admin/AnalyticsOverview.vue'),
        name: 'AnalyticsOverview',
        meta: {
          title: '数据概览',
          roles: ['ADMIN']
        }
      },
      {
        path: 'users',
        component: () => import('@/views/Admin/UserAnalytics.vue'),
        name: 'UserAnalytics',
        meta: {
          title: '用户分析',
          roles: ['ADMIN']
        }
      },
      {
        path: 'courses',
        component: () => import('@/views/Admin/CourseAnalytics.vue'),
        name: 'CourseAnalytics',
        meta: {
          title: '课程分析',
          roles: ['ADMIN']
        }
      },
      {
        path: 'performance',
        component: () => import('@/views/Admin/PerformanceAnalytics.vue'),
        name: 'PerformanceAnalytics',
        meta: {
          title: '性能监控',
          roles: ['ADMIN']
        }
      }
    ]
  },

  // ==================== 教师专用模块 ====================
  {
    path: '/teaching',
    component: Layout,
    name: 'TeachingModule',
    meta: {
      title: '教学管理',
      icon: 'ant-design:book-filled',
      alwaysShow: true,
      roles: ['TEACHER']
    },
    children: [
      {
        path: 'courses',
        component: () => import('@/views/Teaching/MyCourses.vue'),
        name: 'MyCourses',
        meta: {
          title: '我的课程',
          roles: ['TEACHER']
        }
      },
      {
        path: 'students',
        component: () => import('@/views/Teaching/MyStudents.vue'),
        name: 'MyStudents',
        meta: {
          title: '我的学生',
          roles: ['TEACHER']
        }
      },
      {
        path: 'tasks',
        component: () => import('@/views/Teaching/MyTasks.vue'),
        name: 'MyTasks',
        meta: {
          title: '任务管理',
          roles: ['TEACHER']
        }
      },
      {
        path: 'grading',
        component: () => import('@/views/Teaching/GradingCenter.vue'),
        name: 'GradingCenter',
        meta: {
          title: '批改中心',
          roles: ['TEACHER']
        }
      }
    ]
  },

  {
    path: '/content',
    component: Layout,
    name: 'ContentModule',
    meta: {
      title: '内容管理',
      icon: 'ant-design:file-text-filled',
      alwaysShow: true,
      roles: ['TEACHER']
    },
    children: [
      {
        path: 'questions',
        component: () => import('@/views/Quiz/QuestionBank.vue'),
        name: 'QuestionBank',
        meta: {
          title: '题库管理',
          roles: ['TEACHER']
        }
      },
      {
        path: 'papers',
        component: () => import('@/views/Quiz/PaperManagement.vue'),
        name: 'PaperManagement',
        meta: {
          title: '试卷管理',
          roles: ['TEACHER']
        }
      },
      {
        path: 'ai-tools',
        component: () => import('@/views/Teaching/AITools.vue'),
        name: 'AITools',
        meta: {
          title: 'AI教学工具',
          roles: ['TEACHER']
        }
      }
    ]
  },

  // ==================== 学生专用模块 ====================
  {
    path: '/learning',
    component: Layout,
    name: 'LearningModule',
    meta: {
      title: '我的学习',
      icon: 'ant-design:read-filled',
      alwaysShow: true,
      roles: ['STUDENT']
    },
    children: [
      {
        path: 'courses',
        component: () => import('@/views/Learning/MyCourses.vue'),
        name: 'StudentCourses',
        meta: {
          title: '我的课程',
          roles: ['STUDENT']
        }
      },
      {
        path: 'tasks',
        component: () => import('@/views/Learning/MyTasks.vue'),
        name: 'StudentTasks',
        meta: {
          title: '我的任务',
          roles: ['STUDENT']
        }
      },
      {
        path: 'exams',
        component: () =>
          import('@/views/Learning/MyExams.vue').catch((err) => {
            console.error('Failed to load MyExams:', err)
            return import('@/views/Learning/MyExams.vue')
          }),
        name: 'StudentExams',
        meta: {
          title: '我的考试',
          roles: ['STUDENT']
        }
      },
      {
        path: 'grades',
        component: () =>
          import('@/views/Learning/MyGrades.vue').catch((err) => {
            console.error('Failed to load MyGrades:', err)
            return import('@/views/Learning/MyGrades.vue')
          }),
        name: 'StudentGrades',
        meta: {
          title: '我的成绩',
          roles: ['STUDENT']
        }
      }
    ]
  },

  {
    path: '/study-tools',
    component: Layout,
    name: 'StudyToolsModule',
    meta: {
      title: '学习工具',
      icon: 'ant-design:tool-filled',
      alwaysShow: true,
      roles: ['STUDENT']
    },
    children: [
      {
        path: 'knowledge-map',
        component: () =>
          import('@/views/Learning/KnowledgeMap.vue').catch((err) => {
            console.error('Failed to load KnowledgeMap:', err)
            return import('@/views/Learning/KnowledgeMap.vue')
          }),
        name: 'StudentKnowledgeMap',
        meta: {
          title: '知识图谱',
          roles: ['STUDENT']
        }
      },
      {
        path: 'recommendations',
        component: () => import('@/views/Knowledge/LearningRecommendations.vue'),
        name: 'LearningRecommendations',
        meta: {
          title: '学习推荐',
          roles: ['STUDENT']
        }
      },
      {
        path: 'study-plan',
        component: () =>
          import('@/views/Learning/StudyPlan.vue').catch((err) => {
            console.error('Failed to load StudyPlan:', err)
            return import('@/views/Learning/StudyPlan.vue')
          }),
        name: 'StudyPlan',
        meta: {
          title: '学习计划',
          roles: ['STUDENT']
        }
      },
      {
        path: 'progress',
        component: () =>
          import('@/views/Learning/StudyProgress.vue').catch((err) => {
            console.error('Failed to load StudyProgress:', err)
            return import('@/views/Learning/StudyProgress.vue')
          }),
        name: 'StudyProgress',
        meta: {
          title: '学习进度',
          roles: ['STUDENT']
        }
      }
    ]
  },

  // ==================== 隐藏的详情页面和表单页面 ====================
  {
    path: '/forms',
    component: Layout,
    name: 'FormsModule',
    meta: {
      hidden: true
    },
    children: [
      // 课程相关表单
      {
        path: 'course/create',
        component: () => import('@/views/Course/CourseForm.vue'),
        name: 'CourseCreate',
        meta: {
          title: '新建课程',
          roles: ['ADMIN', 'TEACHER'],
          hidden: true
        }
      },
      {
        path: 'course/edit/:id',
        component: () => import('@/views/Course/CourseForm.vue'),
        name: 'CourseEdit',
        meta: {
          title: '编辑课程',
          roles: ['ADMIN', 'TEACHER'],
          hidden: true
        }
      },

      // 任务相关表单
      {
        path: 'task/create',
        component: () => import('@/views/Task/TaskForm.vue'),
        name: 'TaskCreate',
        meta: {
          title: '新建任务',
          roles: ['ADMIN', 'TEACHER'],
          hidden: true
        }
      },
      {
        path: 'task/edit/:id',
        component: () => import('@/views/Task/TaskForm.vue'),
        name: 'TaskEdit',
        meta: {
          title: '编辑任务',
          roles: ['ADMIN', 'TEACHER'],
          hidden: true
        }
      },

      // 题目相关表单
      {
        path: 'question/create',
        component: () => import('@/views/Quiz/QuestionForm.vue'),
        name: 'QuestionCreate',
        meta: {
          title: '新建题目',
          roles: ['ADMIN', 'TEACHER'],
          hidden: true
        }
      },
      {
        path: 'question/edit/:id',
        component: () => import('@/views/Quiz/QuestionForm.vue'),
        name: 'QuestionEdit',
        meta: {
          title: '编辑题目',
          roles: ['ADMIN', 'TEACHER'],
          hidden: true
        }
      }
    ]
  },

  // ==================== 详情页面 ====================
  {
    path: '/details',
    component: Layout,
    name: 'DetailsModule',
    meta: {
      hidden: true
    },
    children: [
      {
        path: 'course/:id',
        component: () => import('@/views/Course/CourseDetail.vue'),
        name: 'CourseDetail',
        meta: {
          title: '课程详情',
          roles: ['ADMIN', 'TEACHER', 'STUDENT'],
          hidden: true
        }
      },
      {
        path: 'student/:id',
        component: () => import('@/views/Student/StudentDetail.vue'),
        name: 'StudentDetail',
        meta: {
          title: '学生详情',
          roles: ['ADMIN', 'TEACHER'],
          hidden: true
        }
      },
      {
        path: 'task/:id',
        component: () => import('@/views/Task/TaskDetail.vue'),
        name: 'TaskDetail',
        meta: {
          title: '任务详情',
          roles: ['ADMIN', 'TEACHER', 'STUDENT'],
          hidden: true
        }
      },
      {
        path: 'paper/:id',
        component: () => import('@/views/Quiz/PaperDetail.vue'),
        name: 'PaperDetail',
        meta: {
          title: '试卷详情',
          roles: ['ADMIN', 'TEACHER'],
          hidden: true
        }
      },
      {
        path: 'submissions/:taskId',
        component: () => import('@/views/Task/TaskSubmissions.vue'),
        name: 'TaskSubmissions',
        meta: {
          title: '任务提交',
          roles: ['ADMIN', 'TEACHER'],
          hidden: true
        }
      },
      {
        path: 'analytics/course/:courseId',
        component: () => import('@/views/Analytics/CourseAnalytics.vue'),
        name: 'CourseAnalytics',
        meta: {
          title: '课程分析',
          roles: ['ADMIN', 'TEACHER'],
          hidden: true
        }
      },
      {
        path: 'knowledge/graph/:courseId',
        component: () => import('@/views/Knowledge/KnowledgeGraph.vue'),
        name: 'CourseKnowledgeGraph',
        meta: {
          title: '课程知识图谱',
          roles: ['ADMIN', 'TEACHER', 'STUDENT'],
          hidden: true
        }
      }
    ]
  },

  // ==================== 学生专用交互页面 ====================
  {
    path: '/student-actions',
    component: Layout,
    name: 'StudentActionsModule',
    meta: {
      hidden: true
    },
    children: [
      {
        path: 'task/submit/:id',
        component: () =>
          import('@/views/Learning/TaskSubmit.vue').catch((err) => {
            console.error('Failed to load TaskSubmit:', err)
            return import('@/views/Learning/TaskSubmit.vue')
          }),
        name: 'TaskSubmit',
        meta: {
          title: '提交任务',
          roles: ['STUDENT'],
          hidden: true
        }
      },
      {
        path: 'exam/take/:id',
        component: () =>
          import('@/views/Learning/ExamTaking.vue').catch((err) => {
            console.error('Failed to load ExamTaking:', err)
            return import('@/views/Learning/ExamTaking.vue')
          }),
        name: 'ExamTaking',
        meta: {
          title: '参加考试',
          roles: ['STUDENT'],
          hidden: true
        }
      },
      {
        path: 'course/study/:id',
        component: () =>
          import('@/views/Learning/CourseStudy.vue').catch((err) => {
            console.error('Failed to load CourseStudy:', err)
            return import('@/views/Learning/CourseStudy.vue')
          }),
        name: 'CourseStudy',
        meta: {
          title: '学习课程',
          roles: ['STUDENT'],
          hidden: true
        }
      }
    ]
  }
]

export const path404 = {
  path: '/:path(.*)*',
  name: '404',
  component: () => import('@/views/Error/404.vue'),
  meta: {
    hidden: true,
    title: '404',
    noTagsView: true
  }
}

const router = createRouter({
  history: createWebHashHistory(),
  strict: true,
  routes: constantRoutes as RouteRecordRaw[],
  scrollBehavior: () => ({ left: 0, top: 0 })
})

export const resetRouter = (): void => {
  const resetWhiteNameList = ['Redirect', 'Login', 'NoFind', 'Root']
  router.getRoutes().forEach((route) => {
    const { name } = route
    if (name && !resetWhiteNameList.includes(name as string)) {
      router.hasRoute(name) && router.removeRoute(name)
    }
  })
}

export const setupRouter = (app: App<Element>) => {
  app.use(router)
}

export default router
