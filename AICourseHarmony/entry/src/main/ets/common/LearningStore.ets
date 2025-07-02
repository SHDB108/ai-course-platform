// 学习进度全局数据与管理方法

/**
 * 全部学习进度数据（全局变量，页面间共享）
 */
export interface LearningResource {
  id: string;
  courseId: string;
  name: string;
  type: 'video' | 'document' | 'quiz' | 'assignment';
  url: string;
  description: string;
  duration?: number; // 视频时长（秒）
  fileSize?: number; // 文件大小（字节）
  createTime: string;
  updateTime: string;
}

export interface ProgressData {
  currentPosition: number;
  bookmarks: number[];
  notes: string[];
}

export interface LearningProgress {
  id: string;
  studentId: string;
  resourceId: string;
  courseId: string;
  progress: number; // 0-100
  watchTime: number; // 观看时长（秒）
  lastWatchTime: string;
  isCompleted: boolean;
  completionTime?: string;
  // 详细进度数据（JSON格式）
  progressData: ProgressData;
}

export interface LearningRecommendation {
  id: string;
  studentId: string;
  courseId: string;
  resourceId?: string;
  type: 'knowledge_point' | 'review_material' | 'next_lesson' | 'practice_exercise';
  title: string;
  description: string;
  priority: number; // 1-10，优先级
  status: 'active' | 'ignored' | 'outdated';
  reason: string; // 推荐原因
  createTime: string;
  expireTime?: string;
}

// 模拟学习资源数据
export const learningResources: LearningResource[] = [
  {
    id: '1',
    courseId: '1',
    name: '人工智能发展历程',
    type: 'video',
    url: 'https://example.com/video1.mp4',
    description: '介绍人工智能从诞生到现代的发展历程',
    duration: 1800, // 30分钟
    fileSize: 50000000, // 50MB
    createTime: '2024-01-01 00:00:00',
    updateTime: '2024-01-01 00:00:00'
  },
  {
    id: '2',
    courseId: '1',
    name: 'AI基础知识文档',
    type: 'document',
    url: 'https://example.com/doc1.pdf',
    description: '人工智能基础知识详细文档',
    fileSize: 2000000, // 2MB
    createTime: '2024-01-01 00:00:00',
    updateTime: '2024-01-01 00:00:00'
  },
  {
    id: '3',
    courseId: '2',
    name: '机器学习算法介绍',
    type: 'video',
    url: 'https://example.com/video2.mp4',
    description: '详细介绍各种机器学习算法',
    duration: 2400, // 40分钟
    fileSize: 80000000, // 80MB
    createTime: '2024-01-01 00:00:00',
    updateTime: '2024-01-01 00:00:00'
  }
];

// 模拟学习进度数据
export const learningProgresses: LearningProgress[] = [
  {
    id: '1',
    studentId: '3',
    resourceId: '1',
    courseId: '1',
    progress: 75,
    watchTime: 1350, // 22.5分钟
    lastWatchTime: '2024-01-20 15:30:00',
    isCompleted: false,
    progressData: {
      currentPosition: 1350,
      bookmarks: [300, 600, 900],
      notes: ['重要概念：图灵测试', '关键点：专家系统']
    }
  },
  {
    id: '2',
    studentId: '3',
    resourceId: '2',
    courseId: '1',
    progress: 100,
    watchTime: 0,
    lastWatchTime: '2024-01-19 10:00:00',
    isCompleted: true,
    completionTime: '2024-01-19 10:00:00',
    progressData: {
      currentPosition: 0,
      bookmarks: [],
      notes: ['文档已完整阅读']
    }
  }
];

// 模拟学习推荐数据
export const learningRecommendations: LearningRecommendation[] = [
  {
    id: '1',
    studentId: '3',
    courseId: '1',
    resourceId: '1',
    type: 'next_lesson',
    title: '继续观看：人工智能发展历程',
    description: '您还有25%的内容未完成，建议继续学习',
    priority: 8,
    status: 'active',
    reason: '基于学习进度推荐',
    createTime: '2024-01-20 16:00:00'
  },
  {
    id: '2',
    studentId: '3',
    courseId: '1',
    type: 'knowledge_point',
    title: '深度学习基础知识',
    description: '建议学习深度学习相关概念',
    priority: 6,
    status: 'active',
    reason: '基于学习历史推荐',
    createTime: '2024-01-20 16:00:00'
  }
];

// 学习进度管理函数
export function getLearningProgress(studentId: string, resourceId: string): LearningProgress | undefined {
  return learningProgresses.find(p => p.studentId === studentId && p.resourceId === resourceId);
}

export function updateLearningProgress(
  studentId: string, 
  resourceId: string, 
  courseId: string, 
  progress: number, 
  watchTime: number
): boolean {
  const existingProgress = learningProgresses.find(
    p => p.studentId === studentId && p.resourceId === resourceId
  );

  if (existingProgress) {
    // 更新现有进度
    existingProgress.progress = progress;
    existingProgress.watchTime = watchTime;
    existingProgress.lastWatchTime = new Date().toISOString();
    existingProgress.isCompleted = progress >= 100;
    if (progress >= 100 && !existingProgress.completionTime) {
      existingProgress.completionTime = new Date().toISOString();
    }
  } else {
    // 创建新进度记录
    const newProgress: LearningProgress = {
      id: Date.now().toString(),
      studentId,
      resourceId,
      courseId,
      progress,
      watchTime,
      lastWatchTime: new Date().toISOString(),
      isCompleted: progress >= 100,
      completionTime: progress >= 100 ? new Date().toISOString() : undefined,
      progressData: {
        currentPosition: watchTime,
        bookmarks: [],
        notes: []
      }
    };
    learningProgresses.push(newProgress);
  }

  return true;
}

export function getCourseProgress(studentId: string, courseId: string): number {
  const courseResources = learningResources.filter(r => r.courseId === courseId);
  const courseProgresses = learningProgresses.filter(p => p.studentId === studentId && p.courseId === courseId);
  
  if (courseResources.length === 0) return 0;
  
  const totalProgress = courseProgresses.reduce((sum, p) => sum + p.progress, 0);
  return Math.round(totalProgress / courseResources.length);
}

// 学习推荐管理函数
export function getActiveRecommendations(studentId: string): LearningRecommendation[] {
  return learningRecommendations.filter(
    r => r.studentId === studentId && r.status === 'active'
  ).sort((a, b) => b.priority - a.priority);
}

export function updateRecommendationStatus(recommendationId: string, status: 'active' | 'ignored' | 'outdated'): boolean {
  const recommendation = learningRecommendations.find(r => r.id === recommendationId);
  if (recommendation) {
    recommendation.status = status;
    return true;
  }
  return false;
}

// 资源管理函数
export function getCourseResources(courseId: string): LearningResource[] {
  return learningResources.filter(r => r.courseId === courseId);
}

export function getResourceById(resourceId: string): LearningResource | undefined {
  return learningResources.find(r => r.id === resourceId);
} 