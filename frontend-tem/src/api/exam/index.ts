import request from '@/axios'
import type { IResponse, PageVO } from '@/api/types'
import type { ExamVO, ExamSubmissionVO, ExamQuestionVO, ExamStatisticsVO } from './types'

// 获取学生的考试列表
export const getStudentExamsApi = (
  studentId: string | number,
  params?: {
    pageNum?: number
    pageSize?: number
    status?: 'NOT_STARTED' | 'IN_PROGRESS' | 'COMPLETED' | 'EXPIRED'
    courseId?: number
    examType?: 'QUIZ' | 'MIDTERM' | 'FINAL' | 'ASSIGNMENT'
  }
): Promise<IResponse<PageVO<ExamVO>>> => {
  return request.get({
    url: `/students/${studentId}/exams`,
    params
  })
}

// 获取考试详情
export const getExamDetailApi = (examId: number): Promise<IResponse<ExamVO>> => {
  return request.get({
    url: `/exams/${examId}`
  })
}

// 获取考试题目
export const getExamQuestionsApi = (examId: number): Promise<IResponse<ExamQuestionVO[]>> => {
  return request.get({
    url: `/exams/${examId}/questions`
  })
}

// 开始考试
export const startExamApi = (examId: number): Promise<IResponse<{ submissionId: number }>> => {
  return request.post({
    url: `/exams/${examId}/start`
  })
}

// 提交考试答案
export const submitExamApi = (
  examId: number,
  data: {
    submissionId: number
    answers: Array<{
      questionId: number
      answer: string | string[]
    }>
  }
): Promise<IResponse<ExamSubmissionVO>> => {
  return request.post({
    url: `/exams/${examId}/submit`,
    data
  })
}

// 保存考试进度（自动保存）
export const saveExamProgressApi = (
  examId: number,
  data: {
    submissionId: number
    answers: Array<{
      questionId: number
      answer: string | string[]
    }>
  }
): Promise<IResponse<boolean>> => {
  return request.post({
    url: `/exams/${examId}/save-progress`,
    data
  })
}

// 获取考试提交记录
export const getExamSubmissionsApi = (
  examId: number,
  studentId: string | number
): Promise<IResponse<ExamSubmissionVO[]>> => {
  return request.get({
    url: `/exams/${examId}/submissions`,
    params: { studentId }
  })
}

// 获取学生考试统计
export const getStudentExamStatisticsApi = (
  studentId: string | number
): Promise<IResponse<ExamStatisticsVO>> => {
  return request.get({
    url: `/students/${studentId}/exam-statistics`
  })
}

// 获取考试剩余时间
export const getExamRemainingTimeApi = (
  examId: number,
  submissionId: number
): Promise<IResponse<{ remainingTime: number }>> => {
  return request.get({
    url: `/exams/${examId}/remaining-time`,
    params: { submissionId }
  })
}
