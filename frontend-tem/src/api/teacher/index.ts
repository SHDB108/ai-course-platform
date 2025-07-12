import request from '@/axios'

// ==================== 类型定义 ====================

export interface TeacherCreateDTO {
  username: string
  password: string
  teacherNo: string
  name: string
  department?: string
  title?: string
  phone?: string
  email?: string
}

export interface TeacherUpdateDTO {
  name?: string
  department?: string
  title?: string
  phone?: string
  email?: string
}

export interface TeacherVO {
  id: number
  username: string
  email: string
  name: string
  teacherNo: string
  department: string
  title: string
  phone: string
  courseCount: number
  studentCount: number
  status: string
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

// ==================== 教师管理API ====================

/**
 * 获取教师列表 (分页)
 */
export const getTeachersApi = (params?: {
  page?: number
  size?: number
  keyword?: string
  department?: string
  status?: string
}): Promise<IResponse<PageVO<TeacherVO>>> => {
  return request.get({ url: '/teachers', params })
}

/**
 * 获取教师详情
 */
export const getTeacherApi = (id: number): Promise<IResponse<TeacherVO>> => {
  return request.get({ url: `/teachers/${id}` })
}

/**
 * 创建教师
 */
export const createTeacherApi = (data: TeacherCreateDTO): Promise<IResponse<number>> => {
  return request.post({ url: '/teachers', data })
}

/**
 * 更新教师信息
 */
export const updateTeacherApi = (id: number, data: TeacherUpdateDTO): Promise<IResponse<void>> => {
  return request.put({ url: `/teachers/${id}`, data })
}

/**
 * 删除教师
 */
export const deleteTeacherApi = (id: number): Promise<IResponse<void>> => {
  return request.delete({ url: `/teachers/${id}` })
}

/**
 * 更新教师状态
 */
export const updateTeacherStatusApi = (
  id: number,
  data: { status: string }
): Promise<IResponse<void>> => {
  return request.put({ url: `/teachers/${id}/status`, data })
}

/**
 * 获取教师简单列表（用于下拉选择）
 */
export const getTeacherOptionsApi = (params?: {
  keyword?: string
}): Promise<IResponse<{ id: number; name: string; teacherNo: string; department: string }[]>> => {
  return request.get({ url: '/teachers/options', params })
}

/**
 * 获取教师的课程列表
 */
export const getTeacherCoursesApi = (
  teacherId: number,
  params?: {
    pageNum?: number
    pageSize?: number
    keyword?: string
    status?: string
  }
): Promise<IResponse<PageVO<any>>> => {
  return request.get({ url: `/teachers/${teacherId}/courses`, params })
}

/**
 * 获取教师的学生列表
 */
export const getTeacherStudentsApi = (
  teacherId: number,
  params?: {
    pageNum?: number
    pageSize?: number
    keyword?: string
    courseId?: string
  }
): Promise<IResponse<PageVO<any>>> => {
  return request.get({ url: `/teachers/${teacherId}/students`, params })
}

/**
 * 导出教师数据
 */
export const exportTeachersApi = (params?: {
  department?: string
  status?: string
  format?: string
}): Promise<Blob> => {
  return request.get({ url: '/teachers/export', params, responseType: 'blob' })
}

// ==================== 教师任务管理API ====================

export interface TeacherTaskVO {
  id: number
  title: string
  courseName: string
  type: string
  dueDate: string
  maxScore: number
  submissionCount: number
  published: boolean
  createdAt: string
  updatedAt: string
}

export interface SimpleCourseVO {
  id: number
  name: string
}

/**
 * 获取教师的任务列表
 */
export const getTeacherTasksApi = (
  teacherId: string | number,
  params?: {
    current?: number
    size?: number
    courseId?: string | number
    type?: string
    published?: boolean
  }
): Promise<IResponse<PageVO<TeacherTaskVO>>> => {
  return request.get({ url: `/teachers/${teacherId}/tasks`, params })
}

/**
 * 获取教师课程简单列表（用于任务筛选）
 */
export const getTeacherCourseOptionsApi = (
  teacherId: string | number
): Promise<IResponse<SimpleCourseVO[]>> => {
  return request.get({ url: `/teachers/${teacherId}/courses/options` })
}
