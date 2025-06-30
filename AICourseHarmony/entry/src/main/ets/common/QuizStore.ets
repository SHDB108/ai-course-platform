export type QuestionType = 'single_choice' | 'multiple_choice' | 'true_false' | 'fill_blank' | 'short_answer' | 'code';

export interface Question {
  id: string;
  quizId: string;
  type: QuestionType;
  content: string;
  options?: string[]; // 选择题选项
  correctAnswer: string | string[]; // 正确答案
  score: number; // 分值
  explanation?: string; // 解析
  order: number; // 题目顺序
}

export interface Quiz {
  id: string;
  courseId: string;
  title: string;
  description: string;
  type: 'quiz' | 'assignment';
  totalScore: number;
  timeLimit?: number; // 时间限制（分钟）
  startTime: string;
  endTime: string;
  status: 'draft' | 'published' | 'archived';
  isAutoGrade: boolean; // 是否自动批改
  createTime: string;
  updateTime: string;
}

export interface QuizAnswer {
  questionId: string;
  answer: string | string[];
  score?: number; // 得分
  feedback?: string; // 教师反馈
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

// 模拟测验数据
export const quizzes: Quiz[] = [
  {
    id: '1',
    courseId: '1',
    title: '人工智能基础知识测验',
    description: '测试对人工智能基本概念的理解',
    type: 'quiz',
    totalScore: 100,
    timeLimit: 60,
    startTime: '2024-01-15 09:00:00',
    endTime: '2024-01-15 10:00:00',
    status: 'published',
    isAutoGrade: true,
    createTime: '2024-01-10 00:00:00',
    updateTime: '2024-01-10 00:00:00'
  },
  {
    id: '2',
    courseId: '1',
    title: 'AI应用案例分析作业',
    description: '分析一个实际的人工智能应用案例',
    type: 'assignment',
    totalScore: 50,
    startTime: '2024-01-20 00:00:00',
    endTime: '2024-01-27 23:59:59',
    status: 'published',
    isAutoGrade: false,
    createTime: '2024-01-18 00:00:00',
    updateTime: '2024-01-18 00:00:00'
  }
];

// 模拟题目数据
export const questions: Question[] = [
  {
    id: '1',
    quizId: '1',
    type: 'single_choice',
    content: '人工智能的英文缩写是什么？',
    options: ['AI', 'ML', 'DL', 'NN'],
    correctAnswer: 'AI',
    score: 10,
    explanation: 'AI是Artificial Intelligence的缩写',
    order: 1
  },
  {
    id: '2',
    quizId: '1',
    type: 'multiple_choice',
    content: '以下哪些是人工智能的主要应用领域？',
    options: ['自然语言处理', '计算机视觉', '机器学习', '区块链'],
    correctAnswer: ['自然语言处理', '计算机视觉', '机器学习'],
    score: 15,
    explanation: '区块链不属于AI的主要应用领域',
    order: 2
  },
  {
    id: '3',
    quizId: '1',
    type: 'true_false',
    content: '图灵测试是用来判断机器是否具有智能的标准',
    correctAnswer: 'true',
    score: 10,
    explanation: '图灵测试是艾伦·图灵提出的判断机器智能的标准',
    order: 3
  },
  {
    id: '4',
    quizId: '1',
    type: 'fill_blank',
    content: '机器学习是人工智能的一个_____',
    correctAnswer: '分支',
    score: 10,
    explanation: '机器学习是AI的一个重要分支',
    order: 4
  },
  {
    id: '5',
    quizId: '2',
    type: 'short_answer',
    content: '请分析ChatGPT在自然语言处理方面的优势和局限性',
    correctAnswer: '', // 主观题没有标准答案
    score: 50,
    order: 1
  }
];

// 模拟提交数据
export const submissions: QuizSubmission[] = [
  {
    id: '1',
    quizId: '1',
    studentId: '3',
    answers: [
      {
        questionId: '1',
        answer: 'AI',
        score: 10
      } as QuizAnswer,
      {
        questionId: '2',
        answer: ['自然语言处理', '计算机视觉', '机器学习'],
        score: 15
      } as QuizAnswer,
      {
        questionId: '3',
        answer: 'true',
        score: 10
      } as QuizAnswer,
      {
        questionId: '4',
        answer: '分支',
        score: 10
      } as QuizAnswer
    ],
    totalScore: 45,
    submittedTime: '2024-01-15 09:45:00',
    gradedTime: '2024-01-15 10:05:00',
    status: 'graded'
  } as QuizSubmission,
  {
    id: '2',
    quizId: '2',
    studentId: '3',
    answers: [
      {
        questionId: '5',
        answer: 'ChatGPT的优势包括：1. 强大的语言理解能力 2. 丰富的知识储备 3. 流畅的对话体验。局限性包括：1. 可能存在偏见 2. 知识更新滞后 3. 缺乏真实理解',
        score: 42,
        feedback: '分析比较全面，但可以更深入地讨论技术原理'
      } as QuizAnswer
    ],
    totalScore: 42,
    submittedTime: '2024-01-25 15:30:00',
    gradedTime: '2024-01-26 10:00:00',
    status: 'graded'
  } as QuizSubmission
];

// 测验管理函数
export function getQuizzesByCourse(courseId: string): Quiz[] {
  return quizzes.filter(q => q.courseId === courseId && q.status === 'published');
}

export function getQuizById(quizId: string): Quiz | undefined {
  return quizzes.find(q => q.id === quizId);
}

export function getQuizQuestions(quizId: string): Question[] {
  return questions.filter(q => q.quizId === quizId).sort((a, b) => a.order - b.order);
}

export function getStudentSubmissions(studentId: string): QuizSubmission[] {
  return submissions.filter(s => s.studentId === studentId);
}

export function getSubmissionById(submissionId: string): QuizSubmission | undefined {
  return submissions.find(s => s.id === submissionId);
}

interface QuizAnswerInput {
  questionId: string;
  answer: string | string[];
}

export function submitQuiz(
  quizId: string, 
  studentId: string, 
  answers: QuizAnswerInput[]
): string {
  const quiz = getQuizById(quizId);
  if (!quiz) {
    throw new Error('测验不存在');
  }

  // 检查是否在有效时间内
  const now = new Date();
  const startTime = new Date(quiz.startTime);
  const endTime = new Date(quiz.endTime);
  
  if (now < startTime || now > endTime) {
    throw new Error('不在测验有效时间内');
  }

  // 计算客观题分数
  let totalScore = 0;
  const gradedAnswers: QuizAnswer[] = answers.map(answer => {
    const question = questions.find(q => q.id === answer.questionId);
    if (!question) return { questionId: answer.questionId, answer: answer.answer, score: 0 } as QuizAnswer;

    let score = 0;
    if (quiz.isAutoGrade && question.type !== 'short_answer') {
      // 自动评分
      if (Array.isArray(question.correctAnswer)) {
        // 多选题
        const correctAnswers = question.correctAnswer as string[];
        const studentAnswers = Array.isArray(answer.answer) ? answer.answer : [answer.answer];
        
        if (correctAnswers.length === studentAnswers.length && 
            correctAnswers.every(a => studentAnswers.includes(a))) {
          score = question.score;
        }
      } else {
        // 单选题、判断题、填空题
        if (answer.answer === question.correctAnswer) {
          score = question.score;
        }
      }
    }
    
    totalScore += score;
    return { questionId: answer.questionId, answer: answer.answer, score } as QuizAnswer;
  });

  const submission: QuizSubmission = {
    id: Date.now().toString(),
    quizId,
    studentId,
    answers: gradedAnswers,
    totalScore,
    submittedTime: new Date().toISOString(),
    gradedTime: undefined,
    status: now > endTime ? 'late' : 'submitted'
  };

  submissions.push(submission);
  return submission.id;
}

interface GradeInput {
  questionId: string;
  score: number;
  feedback?: string;
}

export function gradeSubmission(submissionId: string, grades: GradeInput[]): boolean {
  const submission = submissions.find(s => s.id === submissionId);
  if (!submission) return false;

  // 更新答案的分数和反馈
  grades.forEach(grade => {
    const answer = submission.answers.find(a => a.questionId === grade.questionId);
    if (answer) {
      answer.score = grade.score;
      if (grade.feedback) {
        answer.feedback = grade.feedback;
      }
    }
  });

  // 重新计算总分
  submission.totalScore = submission.answers.reduce((sum, a) => sum + (a.score || 0), 0);
  submission.gradedTime = new Date().toISOString();
  submission.status = 'graded';

  return true;
}

export interface QuizStatistics {
  totalSubmissions: number;
  averageScore: number;
  highestScore: number;
  lowestScore: number;
}

export function getQuizStatistics(quizId: string): QuizStatistics {
  const quizSubmissions = submissions.filter(s => s.quizId === quizId && s.status === 'graded');
  
  if (quizSubmissions.length === 0) {
    return {
      totalSubmissions: 0,
      averageScore: 0,
      highestScore: 0,
      lowestScore: 0
    } as QuizStatistics;
  }

  const scores = quizSubmissions.map(s => s.totalScore);
  const totalScore = scores.reduce((sum, score) => sum + score, 0);

  return {
    totalSubmissions: quizSubmissions.length,
    averageScore: Math.round(totalScore / quizSubmissions.length),
    highestScore: Math.max(...scores),
    lowestScore: Math.min(...scores)
  } as QuizStatistics;
} 