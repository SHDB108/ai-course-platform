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
  // 学生特有字段
  studentId?: string;
  major?: string;
  grade?: string;
  // 教师特有字段
  teacherId?: string;
  department?: string;
  title?: string;
}

// 本地写死的用户数据
export const users: UserInfo[] = [
  { 
    id: '1',
    username: 'admin', 
    password: 'admin123', 
    role: 'admin',
    realName: '系统管理员',
    email: 'admin@aicourse.com',
    phone: '13800138000',
    status: 'active',
    createTime: '2024-01-01 00:00:00'
  },
  { 
    id: '2',
    username: 'teacher1', 
    password: 'teacher123', 
    role: 'teacher',
    realName: '张教授',
    email: 'zhang@aicourse.com',
    phone: '13800138001',
    status: 'active',
    createTime: '2024-01-01 00:00:00',
    teacherId: 'T001',
    department: '计算机学院',
    title: '教授'
  },
  { 
    id: '3',
    username: 'student1', 
    password: 'student123', 
    role: 'student',
    realName: '李同学',
    email: 'li@aicourse.com',
    phone: '13800138002',
    status: 'active',
    createTime: '2024-01-01 00:00:00',
    studentId: 'S001',
    major: '计算机科学与技术',
    grade: '2024级'
  }
];

// 当前登录用户信息（全局状态，后续可用AppStorage或单例管理）
export let currentUser: UserInfo | null = null;

export function login(username: string, password: string): UserInfo | null {
  const user = users.find(u => u.username === username && u.password === password && u.status === 'active');
  if (user) {
    currentUser = {
      id: user.id,
      username: user.username,
      password: user.password,
      role: user.role,
      realName: user.realName,
      email: user.email,
      phone: user.phone,
      avatar: user.avatar,
      status: user.status,
      createTime: user.createTime,
      lastLoginTime: new Date().toISOString(),
      studentId: user.studentId,
      major: user.major,
      grade: user.grade,
      teacherId: user.teacherId,
      department: user.department,
      title: user.title
    };
    return currentUser;
  }
  return null;
}

export function logout() {
  currentUser = null;
}

export function getCurrentUser(): UserInfo | null {
  return currentUser;
}

export function updateUserInfo(userInfo: Partial<UserInfo>): boolean {
  if (!currentUser) return false;
  
  const userIndex = users.findIndex(u => u.id === currentUser!.id);
  if (userIndex === -1) return false;
  
  // 更新users数组中的用户信息
  const updatedUser: UserInfo = {
    id: users[userIndex].id,
    username: users[userIndex].username,
    password: users[userIndex].password,
    role: users[userIndex].role,
    realName: userInfo.realName ?? users[userIndex].realName,
    email: userInfo.email ?? users[userIndex].email,
    phone: userInfo.phone ?? users[userIndex].phone,
    avatar: userInfo.avatar ?? users[userIndex].avatar,
    status: users[userIndex].status,
    createTime: users[userIndex].createTime,
    lastLoginTime: users[userIndex].lastLoginTime,
    studentId: userInfo.studentId ?? users[userIndex].studentId,
    major: userInfo.major ?? users[userIndex].major,
    grade: userInfo.grade ?? users[userIndex].grade,
    teacherId: userInfo.teacherId ?? users[userIndex].teacherId,
    department: userInfo.department ?? users[userIndex].department,
    title: userInfo.title ?? users[userIndex].title
  };
  
  users[userIndex] = updatedUser;
  currentUser = updatedUser;
  return true;
} 