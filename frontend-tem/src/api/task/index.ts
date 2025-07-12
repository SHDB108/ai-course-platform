import request from '@/axios'

export interface TaskCreateDTO {
  title: string
  description: string
  courseId: number
  type: 'ASSIGNMENT' | 'QUIZ' | 'PROJECT' | 'EXAM'
  dueDate: string
  maxScore: number
  instructions: string
  attachments?: string[]
}

export interface TaskUpdateDTO {
  title?: string
  description?: string
  type?: 'ASSIGNMENT' | 'QUIZ' | 'PROJECT' | 'EXAM'
  dueDate?: string
  maxScore?: number
  instructions?: string
  attachments?: string[]
}

export interface TaskVO {
  id: number
  title: string
  description: string
  courseId: number
  courseName: string
  type: string
  dueDate: string
  maxScore: number
  instructions: string
  attachments: string[]
  published: boolean
  createdAt: string
  updatedAt: string
}

export interface TaskSubmissionCreateDTO {
  taskId: number
  content: string
  attachments?: string[]
  submissionType: 'ONLINE' | 'FILE' | 'LINK'
  linkUrl?: string
}

export interface TaskSubmissionVO {
  id: number
  taskId: number
  taskTitle: string
  studentId: number
  studentName: string
  content: string
  attachments: string[]
  submissionType: string
  linkUrl?: string
  score?: number
  feedback?: string
  status: string
  submittedAt: string
  gradedAt?: string
}

export interface TaskSubmissionGradeDTO {
  score: number
  feedback: string
}

export interface IntelligentGradeRequestDTO {
  gradingCriteria: string
  rubric?: string
}

export interface IntelligentGradeResultVO {
  score: number
  feedback: string
  detailedAnalysis: string
  suggestions: string[]
}

export interface PageVO<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

// 任务管理API
export const getCourseTasksApi = (
  courseId: number,
  params?: {
    current?: number
    size?: number
    type?: string
    published?: boolean
  }
): Promise<IResponse<PageVO<TaskVO>>> => {
  return request.get({ url: `/tasks/course/${courseId}`, params })
}

export const getTaskApi = (id: number): Promise<IResponse<TaskVO>> => {
  return request.get({ url: `/tasks/${id}` })
}

export const createTaskApi = (data: TaskCreateDTO): Promise<IResponse<TaskVO>> => {
  return request.post({ url: '/tasks', data })
}

export const updateTaskApi = (id: number, data: TaskUpdateDTO): Promise<IResponse<TaskVO>> => {
  return request.put({ url: `/tasks/${id}`, data })
}

export const deleteTaskApi = (id: number): Promise<IResponse> => {
  return request.delete({ url: `/tasks/${id}` })
}

export const publishTaskApi = (id: number): Promise<IResponse> => {
  return request.put({ url: `/tasks/${id}/publish` })
}

export const unpublishTaskApi = (id: number): Promise<IResponse> => {
  return request.put({ url: `/tasks/${id}/unpublish` })
}

// 任务提交API
export const submitTaskApi = (
  data: TaskSubmissionCreateDTO
): Promise<IResponse<TaskSubmissionVO>> => {
  return request.post({ url: '/task-submissions', data })
}

export const submitTaskFileApi = (
  taskId: number,
  file: File
): Promise<IResponse<TaskSubmissionVO>> => {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('taskId', taskId.toString())
  return request.post({ url: '/task-submissions/file', data: formData })
}

export const getStudentSubmissionsApi = (
  studentId: number,
  courseId: number
): Promise<IResponse<TaskSubmissionVO[]>> => {
  return request.get({ url: `/task-submissions/student/${studentId}/course/${courseId}` })
}

export const getTaskSubmissionsApi = (
  taskId: number,
  params?: {
    current?: number
    size?: number
  }
): Promise<IResponse<PageVO<TaskSubmissionVO>>> => {
  return request.get({ url: `/task-submissions/task/${taskId}`, params })
}

export const getSubmissionApi = (id: number): Promise<IResponse<TaskSubmissionVO>> => {
  return request.get({ url: `/task-submissions/${id}` })
}

export const gradeSubmissionApi = (
  id: number,
  data: TaskSubmissionGradeDTO
): Promise<IResponse> => {
  return request.put({ url: `/task-submissions/${id}/grade`, data })
}

// 学生任务相关接口
export interface StudentTaskVO {
  id: number
  title: string
  courseName: string
  type: string
  dueDate: string
  maxScore: number
  score?: number
  status: string
  submittedAt?: string
  submissionId?: number
}

export interface StudentTaskSummaryStats {
  total: number
  pending: number
  submitted: number
  graded: number
  overdue: number
}

// 获取学生任务列表
export const getStudentTasksApi = (
  studentId: string | number,
  params?: {
    current?: number
    size?: number
    status?: string
    courseId?: number
  }
): Promise<IResponse<PageVO<StudentTaskVO>>> => {
  return request.get({ url: `/students/${studentId}/tasks`, params })
}

// 获取学生任务统计数据
export const getStudentTaskStatsApi = (
  studentId: string | number
): Promise<IResponse<StudentTaskSummaryStats>> => {
  return request.get({ url: `/students/${studentId}/tasks/stats` })
}
