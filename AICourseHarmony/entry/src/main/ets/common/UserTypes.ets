// 用户相关类型定义

/**
 * 用户信息结构
 */
export type UserRole = 'student' | 'teacher' | 'admin';

export interface UserInfo {
  id: string;
  username: string;
  password: string;
  role: UserRole;
  realName: string;
  email: string;
  phone: string;
  avatar?: string;
  status: 'active' | 'inactive';
  createTime: string;
  lastLoginTime?: string;
  studentId?: string;
  major?: string;
  grade?: string;
  teacherId?: string;
  department?: string;
  title?: string;
} 