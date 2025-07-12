// 考试相关类型定义

export interface ExamVO {
  id: number
  examTitle: string
  courseId: number
  courseName: string
  teacherName: string
  examType: 'QUIZ' | 'MIDTERM' | 'FINAL' | 'ASSIGNMENT' // 考试类型
  status: 'NOT_STARTED' | 'IN_PROGRESS' | 'COMPLETED' | 'EXPIRED' // 考试状态
  startTime: string
  endTime: string
  duration: number // 考试时长（分钟）
  totalScore: number // 总分
  passingScore: number // 及格分
  description?: string
  instructions?: string
  allowRetake: boolean // 是否允许重考
  maxAttempts: number // 最大尝试次数
  currentAttempts: number // 当前尝试次数
  lastAttemptScore?: number // 最后一次考试分数
  bestScore?: number // 最好成绩
  remainingTime?: number // 剩余时间（秒）
  createdAt: string
  updatedAt: string
}

export interface ExamSubmissionVO {
  id: number
  examId: number
  studentId: string | number
  submissionTime: string
  score: number
  totalScore: number
  percentage: number
  status: 'SUBMITTED' | 'GRADED' | 'PENDING'
  attemptNumber: number
  timeSpent: number // 用时（秒）
  answers: ExamAnswerVO[]
  feedback?: string
  createdAt: string
}

export interface ExamAnswerVO {
  questionId: number
  questionType: 'SINGLE_CHOICE' | 'MULTIPLE_CHOICE' | 'TRUE_FALSE' | 'SHORT_ANSWER' | 'ESSAY'
  studentAnswer: string | string[]
  correctAnswer?: string | string[]
  isCorrect?: boolean
  score: number
  maxScore: number
}

export interface ExamQuestionVO {
  id: number
  examId: number
  questionType: 'SINGLE_CHOICE' | 'MULTIPLE_CHOICE' | 'TRUE_FALSE' | 'SHORT_ANSWER' | 'ESSAY'
  questionText: string
  options?: string[] // 选择题选项
  correctAnswer?: string | string[]
  score: number
  explanation?: string
  order: number
}

export interface ExamStatisticsVO {
  totalExams: number
  completedExams: number
  pendingExams: number
  expiredExams: number
  averageScore: number
  totalScore: number
  passedExams: number
  failedExams: number
}
