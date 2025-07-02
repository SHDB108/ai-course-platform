import { Quiz, Question, QuizSubmission, QuizAnswer } from './QuizTypes';

/**
 * 测验全局数据与管理方法
 */

/**
 * 全部测验数据（全局变量，页面间共享）
 */
export let quizzes: Quiz[] = [
  {
    id: '201',
    courseId: '101',
    title: 'AI导论期中测验',
    description: '涵盖人工智能基础知识的期中测验。',
    type: 'quiz',
    totalScore: 100,
    timeLimit: 60,
    startTime: '2024-05-01',
    endTime: '2024-05-10',
    status: 'published',
    isAutoGrade: true,
    createTime: '2024-04-01T00:00:00Z',
    updateTime: '2024-04-01T00:00:00Z'
  },
  {
    id: '202',
    courseId: '102',
    title: '机器学习作业1',
    description: '机器学习基础知识作业。',
    type: 'assignment',
    totalScore: 100,
    timeLimit: 120,
    startTime: '2024-05-05',
    endTime: '2024-05-15',
    status: 'published',
    isAutoGrade: false,
    createTime: '2024-04-01T00:00:00Z',
    updateTime: '2024-04-01T00:00:00Z'
  }
];
export let submissions: QuizSubmission[] = [];
export let questions: Question[] = [];

/**
 * 获取所有测验
 */
export function getAllQuizzes(): Quiz[] {
  return quizzes;
}

/**
 * 根据ID获取测验详情
 */
export function getQuizById(id: string): Quiz | undefined {
  return quizzes.find((quiz: Quiz) => quiz.id === id);
}

export function getQuizzesByCourse(courseId: string): Quiz[] {
  return quizzes.filter((q: Quiz) => q.courseId === courseId && q.status === 'published');
}

export function getQuizQuestions(quizId: string): Question[] {
  return questions.filter((q: Question) => q.quizId === quizId).sort((a: Question, b: Question) => a.order - b.order);
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

/**
 * 新建测验，返回新测验ID
 */
export function createQuiz(quiz: Quiz): string {
  const id: string = (Date.now() + Math.floor(Math.random() * 1000)).toString();
  const now: string = new Date().toISOString();
  const newQuiz: Quiz = {
    id: id,
    courseId: quiz.courseId,
    title: quiz.title,
    description: quiz.description,
    type: quiz.type,
    totalScore: quiz.totalScore,
    timeLimit: quiz.timeLimit,
    startTime: quiz.startTime,
    endTime: quiz.endTime,
    status: quiz.status,
    isAutoGrade: quiz.isAutoGrade,
    createTime: now,
    updateTime: now
  };
  quizzes.push(newQuiz);
  return id;
}

/**
 * 更新测验信息
 */
export function updateQuiz(quizId: string, update: Quiz): boolean {
  const idx = quizzes.findIndex(q => q.id === quizId);
  if (idx === -1) return false;
  const old = quizzes[idx];
  quizzes[idx] = {
    id: old.id,
    courseId: update.courseId,
    title: update.title,
    description: update.description,
    type: update.type,
    totalScore: update.totalScore,
    timeLimit: update.timeLimit,
    startTime: update.startTime,
    endTime: update.endTime,
    status: update.status,
    isAutoGrade: update.isAutoGrade,
    createTime: old.createTime,
    updateTime: new Date().toISOString()
  };
  return true;
}

/**
 * 删除测验
 */
export function deleteQuiz(quizId: string): boolean {
  const idx = quizzes.findIndex(q => q.id === quizId);
  if (idx === -1) return false;
  quizzes.splice(idx, 1);
  return true;
} 