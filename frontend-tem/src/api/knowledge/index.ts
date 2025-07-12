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
  courseId: number
  courseName: string
  nodes: {
    id: string
    name: string
    description: string
    level: number
    category: string
    importance: number
    difficulty: string
    prerequisites: string[]
    relatedResources: string[]
  }[]
  edges: {
    source: string
    target: string
    relationship: string
    strength: number
    description: string
  }[]
  metadata: {
    totalNodes: number
    totalEdges: number
    maxLevel: number
    generatedAt: string
    lastUpdated: string
  }
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
  studentId: number
  title: string
  description: string
  type: 'RESOURCE' | 'PRACTICE' | 'REVIEW' | 'ASSIGNMENT'
  priority: 'HIGH' | 'MEDIUM' | 'LOW'
  estimatedTime: number
  targetKnowledgePoints: string[]
  relatedResources: {
    id: number
    title: string
    type: string
    url: string
  }[]
  reason: string
  status: 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'DISMISSED'
  createdAt: string
  updatedAt: string
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
  status: 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'DISMISSED'
  feedback?: string
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
export const getLearningRecommendationsApi = (): Promise<IResponse<LearningRecommendationVO[]>> => {
  return request.get({ url: '/recommendations' })
}

export const generateRecommendationsApi = (
  data: RecommendationGenerationRequestDTO
): Promise<IResponse<LearningRecommendationVO[]>> => {
  return request.post({ url: '/recommendations/generate', data })
}

export const updateRecommendationStatusApi = (
  id: number,
  data: RecommendationStatusUpdateDTO
): Promise<IResponse> => {
  return request.put({ url: `/recommendations/${id}/status`, data })
}
