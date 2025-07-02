// 课程相关类型定义

/**
 * 课程信息结构
 */
export interface Course {
  id: string;
  courseCode: string;
  name: string;
  title: string;
  description: string;
  credits: number;
  hours: number;
  teacherId: string;
  teacherName: string;
  semester: string;
  department: string;
  classroom: string;
  schedule: string;
  capacity: number;
  enrolledCount: number;
  studentCount: number;
  category: string;
  coverImage?: string;
  isEnrolled?: boolean;
  status: 'active' | 'inactive';
  createTime: string;
  updateTime: string;
}

export interface CourseEnrollment {
  id: string;
  studentId: string;
  courseId: string;
  enrollTime: string;
  status: 'enrolled' | 'dropped';
} 