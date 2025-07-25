export type UserLoginType = {
  username: string
  password: string
}

export type UserType = {
  username: string
  password: string
  role: string
  roleId: string
  permissions: string | string[]
}

export type UserInfoType = {
  userId: string | number // 支持两种类型，但优先使用string
  username: string
  role: string
}

export type LoginResponseVO = {
  token: string
  userId: string | number // 支持两种类型，但优先使用string
  username: string
  role: string
}

// 学生注册请求体类型
export type StudentRegisterType = {
  username: string
  password: string
  stuNo: string
  name: string
  gender?: number // 0=女, 1=男
  major?: string
  grade?: string
  phone?: string
  email?: string
}

// 教师注册请求体类型
export type TeacherRegisterType = {
  username: string
  password: string
  teacherNo: string
  name: string
  department?: string
  title?: string
  phone?: string
  email?: string
}

// 管理员注册请求体类型
export type AdminRegisterType = {
  username: string
  password: string
  email?: string
  phone?: string
}
