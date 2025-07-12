import request from '@/axios'

// ==================== 类型定义 ====================

export interface UserVO {
  id: number
  username: string
  email: string
  name: string
  role: string
  status: string
  createdAt: string
  lastLoginAt?: string
  // 教师特有字段
  teacherNo?: string
  department?: string
  title?: string
  phone?: string
  // 学生特有字段
  stuNo?: string
  major?: string
  grade?: string
  gender?: number
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
}

export interface UserCreateByAdminDTO {
  username: string
  password: string
  email: string
  name: string
  role: 'ADMIN' | 'TEACHER' | 'STUDENT'
  // 学生特有字段
  stuNo?: string
  major?: string
  grade?: string
  gender?: number
  // 教师特有字段
  teacherNo?: string
  department?: string
  title?: string
  phone?: string
}

export interface UserStatusUpdateDTO {
  status: 'ACTIVE' | 'INACTIVE' | 'SUSPENDED'
}

export interface PageVO<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

export interface SystemHealthVO {
  cpuUsage: number
  memoryUsage: number
  diskUsage: number
  databaseStatus: string
  redisStatus: string
  uptime: string
}

export interface StoragePropertiesVO {
  uploadPath: string
  maxFileSize: string
  allowedFileTypes: string[]
  enableCompression: boolean
}

export interface StoragePropertiesUpdateDTO {
  uploadPath?: string
  maxFileSize?: string
  allowedFileTypes?: string[]
  enableCompression?: boolean
}

// ==================== 用户管理API ====================

/**
 * 获取所有用户列表 (管理员)
 */
export const getUsersByAdminApi = (params?: {
  pageNum?: number
  pageSize?: number
  role?: string
  keyword?: string
}): Promise<IResponse<PageVO<UserVO>>> => {
  return request.get({ url: '/admin/users', params })
}

/**
 * 更新用户状态 (管理员)
 */
export const updateUserStatusApi = (
  id: number,
  data: UserStatusUpdateDTO
): Promise<IResponse<void>> => {
  return request.put({ url: `/admin/users/${id}/status`, data })
}

/**
 * 创建新用户 (管理员)
 */
export const createUserByAdminApi = (data: UserCreateByAdminDTO): Promise<IResponse<number>> => {
  return request.post({ url: '/admin/users', data })
}

/**
 * 删除用户 (管理员) - 暂未实现后端接口
 */
export const deleteUserApi = (id: number): Promise<IResponse<void>> => {
  // TODO: 实现后端删除用户接口
  return Promise.reject(new Error('删除用户功能暂未实现'))
}

// ==================== 教师管理API ====================

/**
 * 获取教师列表
 */
export const getTeachersApi = (params?: {
  pageNum?: number
  pageSize?: number
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
 * 更新教师状态
 */
export const updateTeacherStatusApi = (
  id: number,
  data: { status: string }
): Promise<IResponse<void>> => {
  return request.put({ url: `/teachers/${id}/status`, data })
}

/**
 * 删除教师
 */
export const deleteTeacherApi = (id: number): Promise<IResponse<void>> => {
  return request.delete({ url: `/teachers/${id}` })
}

// ==================== 系统配置API ====================

/**
 * 获取存储配置
 */
export const getStorageConfigApi = (): Promise<IResponse<StoragePropertiesVO>> => {
  return request.get({ url: '/admin/config/storage' })
}

/**
 * 更新存储配置
 */
export const updateStorageConfigApi = (
  data: StoragePropertiesUpdateDTO
): Promise<IResponse<void>> => {
  return request.put({ url: '/admin/config/storage', data })
}

/**
 * 获取系统健康状态
 */
export const getSystemHealthApi = (): Promise<IResponse<SystemHealthVO>> => {
  return request.get({ url: '/admin/health' })
}

// ==================== 角色权限管理API ====================

export interface RoleVO {
  id: number
  name: string
  displayName: string
  description: string
  userCount: number
  status: number
  permissions: string[]
}

export interface PermissionVO {
  id: number
  name: string
  displayName: string
  category: string
  description: string
}

/**
 * 获取所有角色
 */
export const getRolesApi = (): Promise<IResponse<RoleVO[]>> => {
  return request.get({ url: '/admin/roles' })
}

/**
 * 获取所有权限
 */
export const getPermissionsApi = (): Promise<IResponse<PermissionVO[]>> => {
  return request.get({ url: '/admin/permissions' })
}

/**
 * 更新角色状态
 */
export const updateRoleStatusApi = (
  id: number,
  data: { status: number }
): Promise<IResponse<void>> => {
  return request.put({ url: `/admin/roles/${id}/status`, data })
}

/**
 * 删除角色
 */
export const deleteRoleApi = (id: number): Promise<IResponse<void>> => {
  return request.delete({ url: `/admin/roles/${id}` })
}
