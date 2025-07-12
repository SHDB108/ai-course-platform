import request from '@/axios'

export interface CourseCreateDTO {
  name: string
  description: string
  credits: number
  duration: number
  startDate: string
  endDate: string
  teacherId: string
  maxStudents: number
  status: string
  department?: string
  semester?: string
}

export interface CourseUpdateDTO {
  name?: string
  description?: string
  credits?: number
  duration?: number
  startDate?: string
  endDate?: string
  teacherId?: string
  maxStudents?: number
  status?: string
  department?: string
  semester?: string
}

export interface CourseScheduleDTO {
  startDate: string
  endDate: string
  schedule: string
}

export interface CourseVO {
  id: number
  name: string
  description: string
  credits: number
  duration: number
  startDate: string
  endDate: string
  teacherId: number
  teacherName: string
  maxStudents: number
  enrolledStudents: number
  status: string
  department: string
  semester: string
  categoryName: string
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

export interface StudentVO {
  id: number
  username: string
  email: string
  name: string
  stuNo: string
  phoneNumber: string
  enrollmentDate: string
  status: string
}

export interface CourseCategoryVO {
  id: string
  name: string
  description: string
  courseCount: number
  status: string
}

// ==================== 课程管理API ====================

/**
 * 获取课程列表 (分页)
 */
export const getCoursesApi = (params?: {
  pageNum?: number
  pageSize?: number
  teacherId?: string
  keyword?: string
  semester?: string
  credits?: number
  department?: string
  status?: string
}): Promise<IResponse<PageVO<CourseVO>>> => {
  return request.get({ url: '/courses', params })
}

/**
 * 获取课程详情
 */
export const getCourseApi = (id: number): Promise<IResponse<CourseVO>> => {
  return request.get({ url: `/courses/${id}` })
}

/**
 * 创建课程
 */
export const createCourseApi = (data: CourseCreateDTO): Promise<IResponse<number>> => {
  return request.post({ url: '/courses', data })
}

/**
 * 更新课程
 */
export const updateCourseApi = (id: number, data: CourseUpdateDTO): Promise<IResponse<void>> => {
  return request.put({ url: `/courses/${id}`, data })
}

/**
 * 删除课程
 */
export const deleteCourseApi = (id: number): Promise<IResponse<void>> => {
  return request.delete({ url: `/courses/${id}` })
}

/**
 * 安排课程时间
 */
export const scheduleCourseApi = (
  id: string,
  data: CourseScheduleDTO
): Promise<IResponse<void>> => {
  return request.put({ url: `/courses/${id}/schedule`, data })
}

/**
 * 学生选课
 */
export const enrollStudentApi = (courseId: number, studentId: number): Promise<IResponse<void>> => {
  return request.post({ url: `/courses/${courseId}/students/${studentId}` })
}

/**
 * 学生退课
 */
export const withdrawStudentApi = (
  courseId: number,
  studentId: number
): Promise<IResponse<void>> => {
  return request.delete({ url: `/courses/${courseId}/students/${studentId}` })
}

/**
 * 获取课程学生列表
 */
export const getCourseStudentsApi = (
  courseId: number,
  params?: {
    pageNum?: number
    pageSize?: number
  }
): Promise<IResponse<PageVO<StudentVO>>> => {
  return request.get({ url: `/courses/${courseId}/students`, params })
}

/**
 * 更新课程状态
 */
export const updateCourseStatusApi = (
  id: number,
  data: { status: string }
): Promise<IResponse<void>> => {
  return request.put({ url: `/courses/${id}/status`, data })
}

// ==================== 课程分类管理API ====================

/**
 * 获取课程分类列表
 */
export const getCourseCategoriesApi = (): Promise<IResponse<CourseCategoryVO[]>> => {
  return request.get({ url: '/course-categories' })
}

/**
 * 创建课程分类
 */
export const createCourseCategoryApi = (data: {
  name: string
  description?: string
}): Promise<IResponse<string>> => {
  return request.post({ url: '/course-categories', data })
}

/**
 * 更新课程分类
 */
export const updateCourseCategoryApi = (
  id: number,
  data: { name?: string; description?: string }
): Promise<IResponse<void>> => {
  return request.put({ url: `/course-categories/${id}`, data })
}

/**
 * 删除课程分类
 */
export const deleteCourseCategoryApi = (id: number): Promise<IResponse<void>> => {
  return request.delete({ url: `/course-categories/${id}` })
}
