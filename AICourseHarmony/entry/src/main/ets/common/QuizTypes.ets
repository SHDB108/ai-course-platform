// 测验相关类型定义

/**
 * 测验信息结构
 */
export type QuestionType = 'single_choice' | 'multiple_choice' | 'true_false' | 'fill_blank' | 'short_answer' | 'code';

export interface Question {
  id: string;
  quizId: string;
  type: QuestionType;
  content: string;
  options?: string[];
  correctAnswer: string | string[];
  score: number;
  explanation?: string;
  order: number;
}

export interface Quiz {
  id: string;
  courseId: string;
  title: string;
  description: string;
  type: 'quiz' | 'assignment';
  totalScore: number;
  timeLimit?: number;
  startTime: string;
  endTime: string;
  status: 'draft' | 'published' | 'archived';
  isAutoGrade: boolean;
  createTime: string;
  updateTime: string;
}

export interface QuizSubmission {
  id: string;
  quizId: string;
  studentId: string;
  answers: QuizAnswer[];
  totalScore: number;
  submittedTime: string;
  gradedTime?: string;
  status: 'submitted' | 'graded' | 'late';
}

export interface QuizAnswer {
  questionId: string;
  answer: string | string[];
  score?: number;
  feedback?: string;
} 