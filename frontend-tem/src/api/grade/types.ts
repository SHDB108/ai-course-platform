// 成绩相关类型定义

export interface GradeVO {
  id: number
  studentId: string | number
  courseId: number
  courseName: string
  teacherName: string
  examId?: number
  examTitle?: string
  taskId?: number
  taskTitle?: string
  gradeType: 'EXAM' | 'ASSIGNMENT' | 'QUIZ' | 'PROJECT' | 'PARTICIPATION' // 成绩类型
  score: number
  totalScore: number
  percentage: number
  letterGrade: string // A, B, C, D, F
  gradePoint: number // GPA
  weight: number // 权重
  submissionDate: string
  gradedDate: string
  feedback?: string
  isLate: boolean
  latePenalty: number
  status: 'GRADED' | 'PENDING' | 'RESUBMIT_REQUIRED'
  createdAt: string
  updatedAt: string
}

export interface CourseGradeVO {
  courseId: number
  courseName: string
  courseCode: string
  teacherName: string
  credits: number
  semester: string
  finalGrade: number
  finalPercentage: number
  finalLetterGrade: string
  finalGradePoint: number
  status: 'IN_PROGRESS' | 'COMPLETED' | 'FAILED'
  grades: GradeVO[]
  gradeBreakdown: GradeBreakdownVO
  rank?: number // 班级排名
  classSize?: number // 班级人数
  createdAt: string
  updatedAt: string
}

export interface GradeBreakdownVO {
  examGrades: GradeVO[] // 考试成绩
  assignmentGrades: GradeVO[] // 作业成绩
  quizGrades: GradeVO[] // 测验成绩
  projectGrades: GradeVO[] // 项目成绩
  participationGrades: GradeVO[] // 参与度成绩

  examAverage: number
  assignmentAverage: number
  quizAverage: number
  projectAverage: number
  participationAverage: number

  examWeight: number
  assignmentWeight: number
  quizWeight: number
  projectWeight: number
  participationWeight: number
}

export interface GradeStatisticsVO {
  totalCourses: number
  completedCourses: number
  inProgressCourses: number
  failedCourses: number
  overallGPA: number
  semesterGPA: number
  totalCredits: number
  earnedCredits: number
  averageScore: number
  highestScore: number
  lowestScore: number
  gradesDistribution: {
    A: number
    B: number
    C: number
    D: number
    F: number
  }
}

export interface GPAHistoryVO {
  semester: string
  gpa: number
  credits: number
  courses: number
  createdAt: string
}

export interface GradeTrendVO {
  month: string
  averageScore: number
  gpa: number
  courseCount: number
}
