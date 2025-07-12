import request from '@/axios'
import type { IResponse, PageVO } from '@/api/types'
import type { GradeVO, CourseGradeVO, GradeStatisticsVO, GPAHistoryVO, GradeTrendVO } from './types'

// 获取学生所有成绩
export const getStudentGradesApi = (
  studentId: string | number,
  params?: {
    pageNum?: number
    pageSize?: number
    courseId?: number
    gradeType?: 'EXAM' | 'ASSIGNMENT' | 'QUIZ' | 'PROJECT' | 'PARTICIPATION'
    semester?: string
  }
): Promise<IResponse<PageVO<GradeVO>>> => {
  return request.get({
    url: `/students/${studentId}/grades`,
    params
  })
}

// 获取学生课程成绩详情
export const getStudentCourseGradesApi = (
  studentId: string | number,
  courseId: number
): Promise<IResponse<CourseGradeVO>> => {
  return request.get({
    url: `/students/${studentId}/courses/${courseId}/grades`
  })
}

// 获取学生成绩统计
export const getStudentGradeStatisticsApi = (
  studentId: string | number,
  semester?: string
): Promise<IResponse<GradeStatisticsVO>> => {
  return request.get({
    url: `/students/${studentId}/grade-statistics`,
    params: semester ? { semester } : undefined
  })
}

// 获取学生GPA历史记录
export const getStudentGPAHistoryApi = (
  studentId: string | number
): Promise<IResponse<GPAHistoryVO[]>> => {
  return request.get({
    url: `/students/${studentId}/gpa-history`
  })
}

// 获取学生成绩趋势
export const getStudentGradeTrendApi = (
  studentId: string | number,
  months?: number // 最近几个月，默认12个月
): Promise<IResponse<GradeTrendVO[]>> => {
  return request.get({
    url: `/students/${studentId}/grade-trend`,
    params: months ? { months } : undefined
  })
}

// 获取单个成绩详情
export const getGradeDetailApi = (gradeId: number): Promise<IResponse<GradeVO>> => {
  return request.get({
    url: `/grades/${gradeId}`
  })
}

// 获取学生课程排名
export const getStudentCourseRankingApi = (
  studentId: string | number,
  courseId: number
): Promise<IResponse<{ rank: number; classSize: number; score: number }>> => {
  return request.get({
    url: `/students/${studentId}/courses/${courseId}/ranking`
  })
}

// 获取学生学期成绩汇总
export const getStudentSemesterSummaryApi = (
  studentId: string | number,
  semester: string
): Promise<
  IResponse<{
    semester: string
    gpa: number
    totalCredits: number
    earnedCredits: number
    courses: CourseGradeVO[]
  }>
> => {
  return request.get({
    url: `/students/${studentId}/semester-summary`,
    params: { semester }
  })
}

// 导出成绩单
export const exportGradeReportApi = (
  studentId: string | number,
  format: 'PDF' | 'EXCEL',
  params?: {
    semester?: string
    courseId?: number
  }
): Promise<Blob> => {
  return request.get({
    url: `/students/${studentId}/grade-report/export`,
    params: { format, ...params },
    responseType: 'blob'
  })
}
