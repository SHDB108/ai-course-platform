import request from '@/axios'
import type {
  LoginResponseVO,
  StudentRegisterType,
  TeacherRegisterType,
  AdminRegisterType,
  UserLoginType
} from './types'

interface RoleParams {
  roleName: string
}

export const loginApi = (data: UserLoginType): Promise<IResponse<LoginResponseVO>> => {
  return request.post({ url: 'auth/login', data })
}

export const loginOutApi = (): Promise<IResponse> => {
  return request.get({ url: 'auth/logout' })
}

export const getAdminRoleApi = (
  params: RoleParams
): Promise<IResponse<AppCustomRouteRecordRaw[]>> => {
  return request.get({ url: '/mock/role/list', params })
}

export const getTestRoleApi = (params: RoleParams): Promise<IResponse<string[]>> => {
  return request.get({ url: '/mock/role/list2', params })
}

export const registerStudentApi = (data: StudentRegisterType): Promise<IResponse> => {
  return request.post({ url: 'auth/register/student', data })
}

export const registerTeacherApi = (data: TeacherRegisterType): Promise<IResponse> => {
  return request.post({ url: 'auth/register/teacher', data })
}

export const registerAdminApi = (data: AdminRegisterType): Promise<IResponse> => {
  return request.post({ url: 'auth/register/admin', data })
}
