import request from '@/axios'
import type { CourseVO } from '@/api/course'

export interface StudentCreateDTO {
  username: string
  email: string
  name: string
  stuNo: string
  phoneNumber: string
  password: string
  major?: string
  grade?: string
  gender?: number
}

export interface StudentUpdateDTO {
  email?: string
  name?: string
  stuNo?: string
  phoneNumber?: string
  major?: string
  grade?: string
  gender?: number
}

export interface StudentVO {
  id: number
  username: string
  email: string
  name: string
  stuNo: string
  phoneNumber: string
  major: string
  grade: string
  gender: number
  enrollmentDate: string
  status: string
  courseCount: number
  avgScore: number
  createdAt: string
  updatedAt: string
}

export interface PageVO<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

export interface ImportResultVO {
  successCount: number
  failCount: number
  errorMessages: string[]
}

// ==================== 学生管理API ====================

/**
 * 获取学生列表 (分页)
 */
export const getStudentsApi = (params?: {
  pageNum?: number
  pageSize?: number
  keyword?: string
  major?: string
  grade?: string
  status?: string
}): Promise<IResponse<PageVO<StudentVO>>> => {
  return request.get({ url: '/students', params })
}

/**
 * 获取学生详情
 */
export const getStudentApi = (id: number): Promise<IResponse<StudentVO>> => {
  return request.get({ url: `/students/${id}` })
}

/**
 * 创建学生
 */
export const createStudentApi = (data: StudentCreateDTO): Promise<IResponse<number>> => {
  return request.post({ url: '/students', data })
}

/**
 * 更新学生信息
 */
export const updateStudentApi = (id: number, data: StudentUpdateDTO): Promise<IResponse<void>> => {
  return request.put({ url: `/students/${id}`, data })
}

/**
 * 删除学生
 */
export const deleteStudentApi = (id: number): Promise<IResponse<void>> => {
  return request.delete({ url: `/students/${id}` })
}

/**
 * 更新学生状态
 */
export const updateStudentStatusApi = (
  id: number,
  data: { status: string }
): Promise<IResponse<void>> => {
  return request.put({ url: `/students/${id}/status`, data })
}

/**
 * 批量导入学生
 */
export const importStudentsApi = (file: File): Promise<IResponse<ImportResultVO>> => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post({
    url: '/students/import',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 导出学生数据
 */
export const exportStudentsApi = (params?: {
  major?: string
  grade?: string
  format?: string
}): Promise<Blob> => {
  return request.get({
    url: '/students/export',
    params,
    responseType: 'blob'
  })
}

/**
 * 获取学生选修的课程列表
 */
export const getStudentCoursesApi = (
  studentId: string | number, // 支持字符串和数字类型
  params?: {
    pageNum?: number
    pageSize?: number
    keyword?: string
  }
): Promise<IResponse<PageVO<CourseVO>>> => {
  console.log('调用getStudentCoursesApi，参数:', { studentId, params })

  // 使用真实API调用
  return request.get({ url: `/students/${studentId}/courses`, params })
}

/**
 * 学生仪表板统计数据接口
 */
export interface StudentDashboardStats {
  myCourses: number
  pendingTasks: number
  weeklySubmissions: number
  unreadMessages: number
  todoItems: {
    pending: number
    total: number
  }
  projects: number
}

/**
 * 获取学生仪表板统计数据
 */
export const getStudentDashboardStatsApi = (
  studentId: string | number
): Promise<IResponse<StudentDashboardStats>> => {
  return request.get({ url: `/students/${studentId}/dashboard/stats` })
}
