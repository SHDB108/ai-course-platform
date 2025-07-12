import request from '@/axios'

export interface KnowledgePointVO {
  id: number
  name: string
  description: string
  courseId: number
  parentId?: number
  level: number
  children?: KnowledgePointVO[]
}

export interface KnowledgeGraphVO {
  nodes: {
    id: number
    name: string
    type: string
    description: string
  }[]
  edges: {
    source: number
    target: number
    relation: string
  }[]
}

export interface KnowledgeGraphGenerationDTO {
  courseId: number
  analysisDepth: 'basic' | 'detailed' | 'comprehensive'
  includeResources: boolean
  includeAssignments: boolean
  includeQuizzes: boolean
  customPrompt?: string
}

export interface LearningRecommendationVO {
  id: number
  recommendationType: string
  targetId: number
  targetName: string
  reason: string
  associatedResource?: {
    id: number
    filename: string
  }
}

export interface RecommendationGenerationRequestDTO {
  studentId: number
  courseId: number
  analysisType: 'PERFORMANCE_BASED' | 'KNOWLEDGE_GAP' | 'LEARNING_PATH' | 'COMPREHENSIVE'
  includeWeakAreas: boolean
  includeAdvancedTopics: boolean
  maxRecommendations: number
  timeConstraint?: number
}

export interface RecommendationStatusUpdateDTO {
  isDismissed: number
}

// 知识图谱API
export const getCourseKnowledgeGraphApi = (
  courseId: number
): Promise<IResponse<KnowledgeGraphVO>> => {
  return request.get({ url: `/knowledge-graphs/course/${courseId}` })
}

export const generateKnowledgeGraphApi = (
  courseId: number,
  data: KnowledgeGraphGenerationDTO
): Promise<IResponse<KnowledgeGraphVO>> => {
  return request.post({ url: `/knowledge-graphs/course/${courseId}/generate`, data })
}

// 学习推荐API
export const getLearningRecommendationsApi = (
  courseId: number,
  type?: string,
  count: number = 5
): Promise<IResponse<LearningRecommendationVO[]>> => {
  return request.get({
    url: '/recommendations',
    params: { courseId, type, count }
  })
}

export const generateRecommendationsApi = (
  data: RecommendationGenerationRequestDTO
): Promise<IResponse<LearningRecommendationVO[]>> => {
  return request.post({ url: '/recommendations/generate', data })
}

export const generateMyRecommendationsApi = (courseId: number): Promise<IResponse<string>> => {
  return request.post({ url: `/recommendations/my-recommendations?courseId=${courseId}` })
}

export const updateRecommendationStatusApi = (
  id: number,
  data: RecommendationStatusUpdateDTO
): Promise<IResponse> => {
  return request.put({ url: `/recommendations/${id}/status`, data })
}
