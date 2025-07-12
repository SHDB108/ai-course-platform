import request from '@/axios'

export interface QuestionDTO {
  type: 'SINGLE_CHOICE' | 'MULTIPLE_CHOICE' | 'TRUE_FALSE' | 'SHORT_ANSWER' | 'ESSAY'
  content: string
  options: OptionDTO[]
  correctAnswer: string
  explanation: string
  difficulty: 'EASY' | 'MEDIUM' | 'HARD'
  tags: string[]
  courseId: number
  knowledgePoints: string[]
}

export interface OptionDTO {
  label: string
  content: string
  isCorrect: boolean
}

export interface QuestionVO {
  id: number
  type: string
  content: string
  options: OptionDTO[]
  correctAnswer: string
  explanation: string
  difficulty: string
  tags: string[]
  courseId: number
  courseName: string
  knowledgePoints: string[]
  createdAt: string
  updatedAt: string
}

export interface PaperGenDTO {
  title: string
  description: string
  courseId: number
  totalScore: number
  timeLimit: number
  questionCount: number
  difficultyDistribution: {
    easy: number
    medium: number
    hard: number
  }
  knowledgePoints: string[]
  questionTypes: string[]
}

export interface QuizPaperVO {
  id: number
  title: string
  description: string
  courseId: number
  courseName: string
  totalScore: number
  timeLimit: number
  questionCount: number
  status: string
  createdAt: string
  updatedAt: string
}

export interface QuizPaperDetailsVO {
  id: number
  title: string
  description: string
  courseId: number
  courseName: string
  totalScore: number
  timeLimit: number
  questions: QuestionVO[]
  status: string
  createdAt: string
  updatedAt: string
}

export interface QuizSubmissionDTO {
  paperId: number
  answers: {
    questionId: number
    answer: string
  }[]
  timeSpent: number
}

export interface QuizSubmissionVO {
  id: number
  paperId: number
  paperTitle: string
  studentId: number
  studentName: string
  answers: {
    questionId: number
    answer: string
    isCorrect: boolean
  }[]
  totalScore: number
  achievedScore: number
  timeSpent: number
  submittedAt: string
  gradedAt?: string
}

export interface QuizSubmissionDetailVO {
  id: number
  paperId: number
  paperTitle: string
  studentId: number
  studentName: string
  answers: {
    questionId: number
    questionContent: string
    answer: string
    correctAnswer: string
    isCorrect: boolean
    score: number
  }[]
  totalScore: number
  achievedScore: number
  timeSpent: number
  submittedAt: string
  gradedAt?: string
}

export interface QuizGradeDTO {
  scores: {
    questionId: number
    score: number
  }[]
  feedback: string
}

export interface PageVO<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

// 题库管理API
export const createQuestionApi = (data: QuestionDTO): Promise<IResponse<QuestionVO>> => {
  return request.post({ url: '/questions', data })
}

export const getQuestionsApi = (params?: {
  current?: number
  size?: number
  courseId?: number
  type?: string
  difficulty?: string
  tags?: string[]
  keyword?: string
}): Promise<IResponse<PageVO<QuestionVO>>> => {
  return request.get({ url: '/questions', params })
}

export const getQuestionApi = (id: number): Promise<IResponse<QuestionVO>> => {
  return request.get({ url: `/questions/${id}` })
}

export const updateQuestionApi = (
  id: number,
  data: QuestionDTO
): Promise<IResponse<QuestionVO>> => {
  return request.put({ url: `/questions/${id}`, data })
}

export const deleteQuestionApi = (id: number): Promise<IResponse> => {
  return request.delete({ url: `/questions/${id}` })
}

export const importQuestionsApi = (file: File): Promise<IResponse> => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post({ url: '/questions/import', data: formData })
}

export const exportQuestionsApi = (): Promise<Blob> => {
  return request.get({ url: '/questions/export', responseType: 'blob' })
}

// 试卷管理API
export const generatePaperApi = (data: PaperGenDTO): Promise<IResponse<QuizPaperVO>> => {
  return request.post({ url: '/papers/generate', data })
}

export const getPapersApi = (params?: {
  current?: number
  size?: number
  courseId?: number
  title?: string
}): Promise<IResponse<PageVO<QuizPaperVO>>> => {
  return request.get({ url: '/papers', params })
}

export const getPaperApi = (id: number): Promise<IResponse<QuizPaperDetailsVO>> => {
  return request.get({ url: `/papers/${id}` })
}

export const deletePaperApi = (id: number): Promise<IResponse> => {
  return request.delete({ url: `/papers/${id}` })
}

// 测验提交API
export const submitQuizApi = (data: QuizSubmissionDTO): Promise<IResponse<QuizSubmissionVO>> => {
  return request.post({ url: '/quiz-submissions', data })
}

export const getStudentQuizSubmissionsApi = (
  studentId: number,
  paperId: number
): Promise<IResponse<QuizSubmissionVO[]>> => {
  return request.get({ url: `/quiz-submissions/student/${studentId}/paper/${paperId}` })
}

export const getQuizSubmissionApi = (id: number): Promise<IResponse<QuizSubmissionDetailVO>> => {
  return request.get({ url: `/quiz-submissions/${id}` })
}

export const gradeQuizSubmissionApi = (id: number, data: QuizGradeDTO): Promise<IResponse> => {
  return request.put({ url: `/quiz-submissions/${id}/grade`, data })
}
