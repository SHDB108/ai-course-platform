export type UserRole = 'admin' | 'user';
export interface UserInfo {
  username: string;
  password: string;
  role: UserRole;
}

// 本地写死的用户数据
export const users: UserInfo[] = [
  { username: 'admin', password: 'admin123', role: 'admin' },
  { username: 'user', password: 'user123', role: 'user' }
];

// 当前登录用户信息（全局状态，后续可用AppStorage或单例管理）
export let currentUser: UserInfo | null = null;

export function login(username: string, password: string): UserInfo | null {
  const user = users.find(u => u.username === username && u.password === password);
  if (user) {
    currentUser = user;
    return user;
  }
  return null;
}

export function logout() {
  currentUser = null;
} 