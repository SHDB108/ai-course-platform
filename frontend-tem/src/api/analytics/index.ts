import request from '@/axios'

export interface TrendPointVO {
  date: string
  value: number
  label: string
}

export interface CourseStudentScoreVO {
  studentId: number
  studentName: string
  studentNo: string
  totalScore: number
  averageScore: number
  taskCount: number
  quizCount: number
  completionRate: number
  lastActivityDate: string
}

export interface AnalyticsOverviewVO {
  totalUsers: number
  totalCourses: number
  totalEnrollments: number
  activeUsers: number
  newUsersThisMonth: number
  newCoursesThisMonth: number
  completionRate: number
  avgRating: number
  userDistribution: {
    students: number
    teachers: number
    admins: number
  }
  courseStats: {
    published: number
    draft: number
    suspended: number
    totalLessons: number
    totalAssignments: number
    totalQuizzes: number
  }
  recentActivities: Array<{
    id: number
    type: string
    description: string
    time: string
  }>
  popularCourses: Array<{
    id: number
    name: string
    enrollments: number
    rating: number
    completion: number
  }>
}

export interface TaskCompletionSummaryVO {
  taskId: number
  taskTitle: string
  totalStudents: number
  submittedCount: number
  completionRate: number
  averageScore: number
  dueDate: string
  status: string
}

export interface StudentCoursePerformanceVO {
  studentId: number
  studentName: string
  courseId: number
  courseName: string
  totalScore: number
  averageScore: number
  ranking: number
  completionRate: number
  taskPerformance: {
    taskId: number
    taskTitle: string
    score: number
    maxScore: number
    submittedAt: string
    status: string
  }[]
  quizPerformance: {
    quizId: number
    quizTitle: string
    score: number
    maxScore: number
    submittedAt: string
    timeSpent: number
  }[]
  progressTrend: TrendPointVO[]
  weakKnowledgePoints: string[]
  strongKnowledgePoints: string[]
}

export interface KnowledgePointPerformanceVO {
  knowledgePoint: string
  totalQuestions: number
  correctAnswers: number
  accuracyRate: number
  averageScore: number
  difficultyLevel: string
  studentCount: number
  improvementSuggestions: string[]
}

export interface PageVO<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

// ==================== 数据分析API ====================

/**
 * 获取分析概览数据
 */
export const getAnalyticsOverviewApi = (): Promise<IResponse<AnalyticsOverviewVO>> => {
  return request.get({ url: '/analytics/overview' })
}

// 分析报告API
export const getCourseScoreTrendApi = (
  courseId: number,
  params?: {
    startDate?: string
    endDate?: string
    granularity?: 'daily' | 'weekly' | 'monthly'
  }
): Promise<IResponse<TrendPointVO[]>> => {
  return request.get({ url: `/analytics/courses/${courseId}/trend`, params })
}

export const exportCourseScoresApi = (
  courseId: number,
  params?: {
    format?: 'csv' | 'excel'
    startDate?: string
    endDate?: string
  }
): Promise<Blob> => {
  return request.get({
    url: `/analytics/courses/${courseId}/export`,
    params,
    responseType: 'blob'
  })
}

export const getCourseStudentScoresApi = (
  courseId: number,
  params?: {
    current?: number
    size?: number
    sortBy?: 'score' | 'completion' | 'activity'
    sortOrder?: 'asc' | 'desc'
  }
): Promise<IResponse<PageVO<CourseStudentScoreVO>>> => {
  return request.get({ url: `/analytics/courses/${courseId}/student-scores`, params })
}

export const getTaskCompletionSummaryApi = (
  courseId: number
): Promise<IResponse<TaskCompletionSummaryVO[]>> => {
  return request.get({ url: `/analytics/courses/${courseId}/task-completion-summary` })
}

export const getStudentPerformanceOverviewApi = (
  studentId: number,
  courseId: number
): Promise<IResponse<StudentCoursePerformanceVO>> => {
  return request.get({
    url: `/analytics/student/${studentId}/course/${courseId}/performance-overview`
  })
}

export const getKnowledgePointPerformanceApi = (
  courseId: number,
  params?: {
    knowledgePoints?: string[]
    minAccuracy?: number
    maxAccuracy?: number
  }
): Promise<IResponse<KnowledgePointPerformanceVO[]>> => {
  return request.get({
    url: `/analytics/courses/${courseId}/knowledge-points/performance`,
    params
  })
}

// 通用分析API
export const getScoreTrendApi = (courseId: number): Promise<IResponse<any[]>> => {
  return request.get({ url: `/scores/${courseId}/trend` })
}
