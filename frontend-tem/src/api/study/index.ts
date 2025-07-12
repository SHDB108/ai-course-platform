import request from '@/axios'

// 学习进度相关类型定义
export interface StudyProgressVO {
  id: number
  studentId: number
  courseId: number
  courseName: string
  totalProgress: number
  status: string
  lastStudyTime: string
  startDate: string
  expectedEndDate: string
  videoProgress: ModuleProgress
  taskProgress: ModuleProgress
  examProgress: ModuleProgress
  knowledgeProgress: ModuleProgress
  studyTimeStats: StudyTimeStats
  completionStats: CompletionStats
}

export interface ModuleProgress {
  progress: number
  completed: number
  total: number
  status: string
}

export interface StudyTimeStats {
  totalStudyTime: number
  todayStudyTime: number
  weekStudyTime: number
  monthStudyTime: number
  averageDaily: number
}

export interface CompletionStats {
  completedVideos: number
  totalVideos: number
  completedTasks: number
  totalTasks: number
  completedExams: number
  totalExams: number
  masteredKnowledge: number
  totalKnowledge: number
}

export interface StudyAnalysisVO {
  studentId: number
  courseId: number
  courseName: string
  overallStats: OverallStats
  timeAnalysis: TimeAnalysis
  progressAnalysis: ProgressAnalysis
  knowledgeAnalysis: KnowledgeAnalysis
  behaviorAnalysis: BehaviorAnalysis
  predictions: string[]
  recommendations: string[]
}

export interface OverallStats {
  totalCourses: number
  activeCourses: number
  completedCourses: number
  totalStudyHours: number
  averageProgress: number
  overallPerformance: string
}

export interface TimeAnalysis {
  todayMinutes: number
  weekMinutes: number
  monthMinutes: number
  dailyAverage: number
  dailyTrend: DailyStudyTime[]
  peakStudyTime: string
  consistency: number
}

export interface ProgressAnalysis {
  completionRate: number
  progressTrend: string
  estimatedDaysToComplete: number
  moduleBreakdown: ModuleProgressDetail[]
}

export interface KnowledgeAnalysis {
  totalKnowledgePoints: number
  masteredPoints: number
  learningPoints: number
  weakPoints: number
  weakAreas: WeakArea[]
  strongAreas: string[]
  masteryRate: number
}

export interface BehaviorAnalysis {
  preferredStudyTime: string
  studyPattern: string
  averageSessionDuration: number
  devicePreference: string
  engagementScore: number
  learningHabits: string[]
}

export interface DailyStudyTime {
  date: string
  minutes: number
  sessionType: string
}

export interface ModuleProgressDetail {
  moduleName: string
  progress: number
  status: string
  timeSpent: number
}

export interface WeakArea {
  knowledgePointName: string
  difficulty: string
  attemptCount: number
  successRate: number
  recommendedAction: string
}

export interface StudySessionVO {
  id: number
  studentId: number
  courseId: number
  courseName: string
  sessionType: string
  resourceId: number
  resourceTitle: string
  startTime: string
  endTime: string
  duration: number
  effectiveTime: number
  completionRate: number
  result: string
  score: number
  notes: string
  deviceType: string
  browserInfo: string
}

export interface StudyPlanVO {
  id: number
  studentId: number
  courseId: number
  courseName: string
  planName: string
  description: string
  planType: string
  startDate: string
  endDate: string
  targetDate: string
  estimatedHours: number
  status: string
  progress: number
  priority: string
  completedTasks: number
  totalTasks: number
  goals: string[]
  milestones: Milestone[]
  reminderEnabled: boolean
  reminderFrequency: string
  reminderTime: string
  isAiGenerated: boolean
  aiRecommendReason: string
  daysRemaining: number
  progressRate: number
  progressStatus: string
}

export interface Milestone {
  title: string
  description: string
  targetDate: string
  completed: boolean
  completedDate: string
}

// DTO类型定义
export interface StudySessionCreateDTO {
  courseId: number
  sessionType: string
  resourceId: number
  resourceTitle: string
  deviceType?: string
  browserInfo?: string
  ipAddress?: string
  notes?: string
}

export interface StudySessionUpdateDTO {
  duration: number
  effectiveTime: number
  progressData?: string
  completionRate: number
  interactionData?: string
  result: string
  score?: number
  notes?: string
  feedback?: string
}

export interface StudyPlanCreateDTO {
  courseId?: number
  planName: string
  description: string
  planType: string
  startDate: string
  endDate: string
  targetDate: string
  estimatedHours: number
  goals: string[]
  milestones: MilestoneDTO[]
  priority: string
  reminderEnabled: boolean
  reminderFrequency?: string
  reminderTime?: string
}

export interface MilestoneDTO {
  title: string
  description: string
  targetDate: string
}

export interface KnowledgePointProgressUpdateDTO {
  masteryLevel: string
  masteryScore: number
  confidence: number
  isCorrect?: boolean
  studyTime?: number
  learningContext?: string
}

// API接口函数
export const getStudyProgressApi = (studentId: number, courseId: number) => {
  return request.get({ url: `/study/progress/${studentId}/${courseId}` })
}

export const getAllStudyProgressApi = (studentId: number) => {
  return request.get({ url: `/study/progress/${studentId}` })
}

export const updateStudyProgressApi = (studentId: number, courseId: number) => {
  return request.put({ url: `/study/progress/${studentId}/${courseId}` })
}

export const getStudyAnalysisApi = (studentId: number, courseId: number) => {
  return request.get({ url: `/study/analysis/${studentId}/${courseId}` })
}

export const getOverallStudyAnalysisApi = (studentId: number) => {
  return request.get({ url: `/study/analysis/${studentId}` })
}

export const startStudySessionApi = (studentId: number, data: StudySessionCreateDTO) => {
  return request.post({ url: `/study/sessions/${studentId}`, data })
}

export const endStudySessionApi = (sessionId: number, data: StudySessionUpdateDTO) => {
  return request.put({ url: `/study/sessions/${sessionId}`, data })
}

export const getStudySessionHistoryApi = (studentId: number, courseId?: number, limit?: number) => {
  const params: any = {}
  if (courseId) params.courseId = courseId
  if (limit) params.limit = limit
  return request.get({ url: `/study/sessions/${studentId}`, params })
}

export const createStudyPlanApi = (studentId: number, data: StudyPlanCreateDTO) => {
  return request.post({ url: `/study/plans/${studentId}`, data })
}

export const getStudyPlansApi = (studentId: number, courseId?: number) => {
  const params: any = {}
  if (courseId) params.courseId = courseId
  return request.get({ url: `/study/plans/${studentId}`, params })
}

export const updateStudyPlanProgressApi = (planId: number, progress: number) => {
  return request.put({ url: `/study/plans/${planId}/progress?progress=${progress}` })
}

export const generateAiStudyPlanApi = (
  studentId: number,
  courseId: number,
  planType: string = 'WEEKLY'
) => {
  return request.post({
    url: `/study/plans/${studentId}/${courseId}/generate?planType=${planType}`
  })
}

export const updateKnowledgePointProgressApi = (
  studentId: number,
  knowledgePointId: number,
  data: KnowledgePointProgressUpdateDTO
) => {
  return request.put({ url: `/study/knowledge-points/${studentId}/${knowledgePointId}`, data })
}

export const getKnowledgePointProgressApi = (studentId: number, courseId: number) => {
  return request.get({ url: `/study/knowledge-points/${studentId}/${courseId}` })
}

export const getKnowledgePointsNeedReviewApi = (studentId: number) => {
  return request.get({ url: `/study/knowledge-points/${studentId}/review` })
}
