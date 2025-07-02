import { Course, CourseEnrollment } from './CourseTypes';

/**
 * 全部课程数据（全局变量，页面间共享）
 */
export let courses: Course[] = [
  {
    id: '101',
    courseCode: 'AI101',
    name: '人工智能导论',
    title: '人工智能导论',
    description: '介绍人工智能的基本概念、发展历程与应用。',
    credits: 3,
    hours: 48,
    teacherId: '2',
    teacherName: '张老师',
    semester: '2024春季',
    department: '人工智能学院',
    classroom: 'A101',
    schedule: '周一 1-2节',
    capacity: 100,
    enrolledCount: 0,
    studentCount: 0,
    category: '基础',
    coverImage: '',
    isEnrolled: false,
    status: 'active',
    createTime: '2024-01-01T00:00:00Z',
    updateTime: '2024-01-01T00:00:00Z'
  },
  {
    id: '102',
    courseCode: 'ML201',
    name: '机器学习基础',
    title: '机器学习基础',
    description: '系统讲解机器学习的基本原理与常用算法。',
    credits: 4,
    hours: 64,
    teacherId: '2',
    teacherName: '张老师',
    semester: '2024春季',
    department: '人工智能学院',
    classroom: 'A102',
    schedule: '周三 3-4节',
    capacity: 80,
    enrolledCount: 0,
    studentCount: 0,
    category: '核心',
    coverImage: '',
    isEnrolled: false,
    status: 'active',
    createTime: '2024-01-01T00:00:00Z',
    updateTime: '2024-01-01T00:00:00Z'
  }
];
/**
 * 选课记录
 */
export let enrollments: CourseEnrollment[] = [];

/**
 * 获取所有"激活"状态的课程
 */
export function getAllCourses(): Course[] {
  return courses.filter((course: Course) => course.status === 'active');
}

/**
 * 根据ID获取课程详情
 */
export function getCourseById(id: string): Course | undefined {
  return courses.find((course: Course) => course.id === id);
}

export function getCoursesByTeacher(teacherId: string): Course[] {
  return courses.filter((course: Course) => course.teacherId === teacherId && course.status === 'active');
}

export function getEnrolledCourses(studentId: string): Course[] {
  const enrolledCourseIds: string[] = enrollments
    .filter((enrollment: CourseEnrollment) => enrollment.studentId === studentId && enrollment.status === 'enrolled')
    .map((enrollment: CourseEnrollment) => enrollment.courseId);
  return courses.filter((course: Course) => enrolledCourseIds.includes(course.id));
}

export function enrollCourse(studentId: string, courseId: string): boolean {
  const course: Course | undefined = getCourseById(courseId);
  if (!course || course.enrolledCount >= course.capacity) {
    return false;
  }
  const existingEnrollment: CourseEnrollment | undefined = enrollments.find(
    (e: CourseEnrollment) => e.studentId === studentId && e.courseId === courseId && e.status === 'enrolled'
  );
  if (existingEnrollment) {
    return false;
  }
  enrollments.push({
    id: Date.now().toString(),
    studentId,
    courseId,
    enrollTime: new Date().toISOString(),
    status: 'enrolled'
  });
  const courseIndex: number = courses.findIndex((c: Course) => c.id === courseId);
  if (courseIndex !== -1) {
    courses[courseIndex].enrolledCount++;
  }
  return true;
}

export function dropCourse(studentId: string, courseId: string): boolean {
  const enrollmentIndex: number = enrollments.findIndex(
    (e: CourseEnrollment) => e.studentId === studentId && e.courseId === courseId && e.status === 'enrolled'
  );
  if (enrollmentIndex === -1) {
    return false;
  }
  enrollments[enrollmentIndex].status = 'dropped';
  const courseIndex: number = courses.findIndex((c: Course) => c.id === courseId);
  if (courseIndex !== -1) {
    courses[courseIndex].enrolledCount--;
  }
  return true;
}

export function searchCourses(keyword: string, teacherId?: string, semester?: string): Course[] {
  return courses.filter((course: Course) => {
    const matchesKeyword: boolean = course.name.includes(keyword) || 
                          course.description.includes(keyword) ||
                          course.courseCode.includes(keyword);
    const matchesTeacher: boolean = !teacherId || course.teacherId === teacherId;
    const matchesSemester: boolean = !semester || course.semester === semester;
    return matchesKeyword && matchesTeacher && matchesSemester && course.status === 'active';
  });
}

/**
 * 新建课程，返回新课程ID
 */
export function createCourse(course: Course): string {
  const id: string = (Date.now() + Math.floor(Math.random() * 1000)).toString();
  const now: string = new Date().toISOString();
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
    coverImage: course.coverImage,
    isEnrolled: false,
    status: 'active',
    createTime: now,
    updateTime: now
  };
  courses.push(newCourse);
  return id;
}

/**
 * 更新课程信息
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

/**
 * 删除课程
 */
export function deleteCourse(courseId: string): boolean {
  const idx = courses.findIndex(c => c.id === courseId);
  if (idx === -1) return false;
  courses.splice(idx, 1);
  return true;
} 