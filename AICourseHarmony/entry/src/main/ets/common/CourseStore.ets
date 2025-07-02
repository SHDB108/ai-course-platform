export interface Course {
  id: string;
  courseCode: string;
  name: string;
  title: string; // 添加title字段，与CourseListPage中使用的一致
  description: string;
  credits: number;
  hours: number;
  teacherId: string;
  teacherName: string;
  semester: string;
  department: string;
  classroom: string;
  schedule: string; // 上课时间
  capacity: number;
  enrolledCount: number;
  studentCount: number; // 添加studentCount字段
  category: string; // 添加category字段
  coverImage?: string; // 添加coverImage字段
  isEnrolled?: boolean; // 添加isEnrolled字段
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

// 模拟课程数据
export const courses: Course[] = [
  {
    id: '1',
    courseCode: 'CS101',
    name: '人工智能导论',
    title: '人工智能导论',
    description: '本课程介绍人工智能的基本概念、发展历程和主要应用领域',
    credits: 3,
    hours: 48,
    teacherId: '2',
    teacherName: '张教授',
    semester: '2024春季',
    department: '计算机学院',
    classroom: 'A101',
    schedule: '周一 1-2节',
    capacity: 50,
    enrolledCount: 35,
    studentCount: 35,
    category: '人工智能',
    status: 'active',
    createTime: '2024-01-01 00:00:00',
    updateTime: '2024-01-01 00:00:00'
  },
  {
    id: '2',
    courseCode: 'CS102',
    name: '机器学习基础',
    title: '机器学习基础',
    description: '学习机器学习的基本算法和原理',
    credits: 4,
    hours: 64,
    teacherId: '2',
    teacherName: '张教授',
    semester: '2024春季',
    department: '计算机学院',
    classroom: 'A102',
    schedule: '周二 3-4节',
    capacity: 40,
    enrolledCount: 28,
    studentCount: 28,
    category: '机器学习',
    status: 'active',
    createTime: '2024-01-01 00:00:00',
    updateTime: '2024-01-01 00:00:00'
  }
];

// 模拟选课数据
export const enrollments: CourseEnrollment[] = [
  {
    id: '1',
    studentId: '3',
    courseId: '1',
    enrollTime: '2024-01-15 10:00:00',
    status: 'enrolled'
  },
  {
    id: '2',
    studentId: '3',
    courseId: '2',
    enrollTime: '2024-01-16 14:00:00',
    status: 'enrolled'
  }
];

// 课程管理相关函数
export function getAllCourses(): Course[] {
  return courses.filter(course => course.status === 'active');
}

export function getCourseById(id: string): Course | undefined {
  return courses.find(course => course.id === id);
}

export function getCoursesByTeacher(teacherId: string): Course[] {
  return courses.filter(course => course.teacherId === teacherId && course.status === 'active');
}

export function getEnrolledCourses(studentId: string): Course[] {
  const enrolledCourseIds = enrollments
    .filter(enrollment => enrollment.studentId === studentId && enrollment.status === 'enrolled')
    .map(enrollment => enrollment.courseId);
  
  return courses.filter(course => enrolledCourseIds.includes(course.id));
}

export function enrollCourse(studentId: string, courseId: string): boolean {
  const course = getCourseById(courseId);
  if (!course || course.enrolledCount >= course.capacity) {
    return false;
  }

  const existingEnrollment = enrollments.find(
    e => e.studentId === studentId && e.courseId === courseId && e.status === 'enrolled'
  );
  
  if (existingEnrollment) {
    return false; // 已经选过这门课
  }

  // 添加选课记录
  enrollments.push({
    id: Date.now().toString(),
    studentId,
    courseId,
    enrollTime: new Date().toISOString(),
    status: 'enrolled'
  });

  // 更新课程选课人数
  const courseIndex = courses.findIndex(c => c.id === courseId);
  if (courseIndex !== -1) {
    courses[courseIndex].enrolledCount++;
  }

  return true;
}

export function dropCourse(studentId: string, courseId: string): boolean {
  const enrollmentIndex = enrollments.findIndex(
    e => e.studentId === studentId && e.courseId === courseId && e.status === 'enrolled'
  );

  if (enrollmentIndex === -1) {
    return false;
  }

  // 更新选课状态
  enrollments[enrollmentIndex].status = 'dropped';

  // 更新课程选课人数
  const courseIndex = courses.findIndex(c => c.id === courseId);
  if (courseIndex !== -1) {
    courses[courseIndex].enrolledCount--;
  }

  return true;
}

export function searchCourses(keyword: string, teacherId?: string, semester?: string): Course[] {
  return courses.filter(course => {
    const matchesKeyword = course.name.includes(keyword) || 
                          course.description.includes(keyword) ||
                          course.courseCode.includes(keyword);
    const matchesTeacher = !teacherId || course.teacherId === teacherId;
    const matchesSemester = !semester || course.semester === semester;
    
    return matchesKeyword && matchesTeacher && matchesSemester && course.status === 'active';
  });
}

/**
 * 新建课程，返回新课程id
 */
export function createCourse(course: Course): string {
  const id = (Date.now() + Math.floor(Math.random() * 1000)).toString();
  const now = new Date().toISOString();
  const newCourse: Course = {
    id: id,
    courseCode: course.courseCode,
    name: course.name,
    title: course.title,
    description: course.description,
    credits: course.credits,
    hours: course.hours,
    teacherId: course.teacherId,
    teacherName: course.teacherName,
    semester: course.semester,
    department: course.department,
    classroom: course.classroom,
    schedule: course.schedule,
    capacity: course.capacity,
    enrolledCount: 0,
    studentCount: 0,
    category: course.category,
    status: 'active',
    createTime: now,
    updateTime: now,
    coverImage: course.coverImage,
    isEnrolled: course.isEnrolled
  };
  courses.push(newCourse);
  return id;
}

/**
 * 更新课程，返回是否成功
 */
export function updateCourse(courseId: string, update: Course): boolean {
  const idx = courses.findIndex(c => c.id === courseId);
  if (idx === -1) return false;
  const old = courses[idx];
  courses[idx] = {
    id: old.id,
    courseCode: update.courseCode,
    name: update.name,
    title: update.title,
    description: update.description,
    credits: update.credits,
    hours: update.hours,
    teacherId: update.teacherId,
    teacherName: update.teacherName,
    semester: update.semester,
    department: update.department,
    classroom: update.classroom,
    schedule: update.schedule,
    capacity: update.capacity,
    enrolledCount: old.enrolledCount,
    studentCount: old.studentCount,
    category: update.category,
    status: update.status,
    createTime: old.createTime,
    updateTime: new Date().toISOString(),
    coverImage: update.coverImage,
    isEnrolled: update.isEnrolled
  };
  return true;
} 