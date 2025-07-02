import { UserInfo, UserRole } from './UserTypes';

/**
 * 用户全局数据与管理方法
 */

/**
 * 全部用户数据（全局变量，页面间共享）
 */
export let users: UserInfo[] = [
  {
    id: '1',
    username: 'admin',
    password: 'admin123',
    role: 'admin',
    realName: '管理员',
    email: 'admin@example.com',
    phone: '12345678901',
    avatar: '',
    status: 'active',
    createTime: '2024-01-01T00:00:00Z',
    lastLoginTime: '',
    studentId: '',
    major: '',
    grade: '',
    teacherId: '',
    department: '',
    title: ''
  },
  {
    id: '2',
    username: 'teacher1',
    password: 'teacher123',
    role: 'teacher',
    realName: '张老师',
    email: 'teacher1@example.com',
    phone: '12345678902',
    avatar: '',
    status: 'active',
    createTime: '2024-01-01T00:00:00Z',
    lastLoginTime: '',
    studentId: '',
    major: '',
    grade: '',
    teacherId: 'T001',
    department: '人工智能学院',
    title: '副教授'
  },
  {
    id: '3',
    username: 'student1',
    password: 'student123',
    role: 'student',
    realName: '李同学',
    email: 'student1@example.com',
    phone: '12345678903',
    avatar: '',
    status: 'active',
    createTime: '2024-01-01T00:00:00Z',
    lastLoginTime: '',
    studentId: 'S001',
    major: '人工智能',
    grade: '2021',
    teacherId: '',
    department: '人工智能学院',
    title: ''
  }
];
export let currentUser: UserInfo | null = null;

/**
 * 获取所有用户
 */
export function getAllUsers(): UserInfo[] {
  return users;
}

/**
 * 根据ID获取用户详情
 */
export function getUserById(id: string): UserInfo | undefined {
  return users.find((user: UserInfo) => user.id === id);
}

/**
 * 获取当前登录用户
 */
export function getCurrentUser(): UserInfo | null {
  return currentUser;
}

export function login(username: string, password: string): UserInfo | null {
  const user: UserInfo | undefined = users.find((u: UserInfo) => u.username === username && u.password === password && u.status === 'active');
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

export function logout(): void {
  currentUser = null;
}

export function updateUserInfo(userInfo: UserInfo): boolean {
  if (!currentUser) return false;
  const userIndex: number = users.findIndex((u: UserInfo) => u.id === currentUser!.id);
  if (userIndex === -1) return false;
  users[userIndex].realName = userInfo.realName;
  users[userIndex].email = userInfo.email;
  users[userIndex].phone = userInfo.phone;
  users[userIndex].avatar = userInfo.avatar;
  users[userIndex].status = userInfo.status;
  users[userIndex].lastLoginTime = userInfo.lastLoginTime;
  users[userIndex].studentId = userInfo.studentId;
  users[userIndex].major = userInfo.major;
  users[userIndex].grade = userInfo.grade;
  users[userIndex].teacherId = userInfo.teacherId;
  users[userIndex].department = userInfo.department;
  users[userIndex].title = userInfo.title;
  currentUser = users[userIndex];
  return true;
}

/**
 * 新建用户，返回新用户ID
 */
export function createUser(user: UserInfo): string {
  users.push(user);
  return user.id;
}

/**
 * 更新用户信息
 */
export function updateUser(userId: string, update: UserInfo): boolean {
  const userIndex: number = users.findIndex((u: UserInfo) => u.id === userId);
  if (userIndex === -1) return false;
  users[userIndex].realName = update.realName;
  users[userIndex].email = update.email;
  users[userIndex].phone = update.phone;
  users[userIndex].avatar = update.avatar;
  users[userIndex].status = update.status;
  users[userIndex].lastLoginTime = update.lastLoginTime;
  users[userIndex].studentId = update.studentId;
  users[userIndex].major = update.major;
  users[userIndex].grade = update.grade;
  users[userIndex].teacherId = update.teacherId;
  users[userIndex].department = update.department;
  users[userIndex].title = update.title;
  return true;
}

/**
 * 删除用户
 */
export function deleteUser(userId: string): boolean {
  const userIndex: number = users.findIndex((u: UserInfo) => u.id === userId);
  if (userIndex === -1) return false;
  users.splice(userIndex, 1);
  return true;
} 