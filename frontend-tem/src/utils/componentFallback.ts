/**
 * 组件导入备份策略
 * 当主要的动态导入失败时，提供备用组件
 */

// 创建一个通用的fallback组件
export const createFallbackComponent = (componentName: string) => {
  return {
    name: `${componentName}Fallback`,
    template: `
      <div class="fallback-component p-6 text-center">
        <div class="mb-4">
          <i class="el-icon-warning text-4xl text-yellow-500"></i>
        </div>
        <h3 class="text-lg font-semibold mb-2">{{ componentName }} 组件加载中...</h3>
        <p class="text-gray-500 mb-4">组件正在加载，请稍候</p>
        <el-button @click="retry" type="primary">重新加载</el-button>
      </div>
    `,
    setup() {
      const retry = () => {
        window.location.reload()
      }
      return {
        componentName,
        retry
      }
    }
  }
}

// 学习组件的备用导入
export const learningComponentFallbacks = {
  MyCourses: () => createFallbackComponent('我的课程'),
  MyTasks: () => createFallbackComponent('我的任务'),
  MyExams: () => createFallbackComponent('我的考试'),
  MyGrades: () => createFallbackComponent('我的成绩'),
  KnowledgeMap: () => createFallbackComponent('知识图谱'),
  StudyPlan: () => createFallbackComponent('学习计划'),
  StudyProgress: () => createFallbackComponent('学习进度'),
  TaskSubmit: () => createFallbackComponent('任务提交'),
  ExamTaking: () => createFallbackComponent('考试参加'),
  CourseStudy: () => createFallbackComponent('课程学习')
}

// 安全的组件导入函数
export const safeImport = async (
  primaryImport: () => Promise<any>,
  fallbackComponent: () => any,
  componentName: string,
  maxRetries = 2
): Promise<any> => {
  let retryCount = 0

  while (retryCount <= maxRetries) {
    try {
      console.log(`[SafeImport] 尝试导入 ${componentName}, 重试次数: ${retryCount}`)
      const module = await primaryImport()
      console.log(`[SafeImport] 成功导入 ${componentName}`)
      return module
    } catch (error) {
      console.error(`[SafeImport] 导入失败 ${componentName}:`, error)
      retryCount++

      if (retryCount <= maxRetries) {
        // 等待一会儿再重试
        await new Promise((resolve) => setTimeout(resolve, 1000 * retryCount))
      }
    }
  }

  console.warn(`[SafeImport] 最终回退到备用组件: ${componentName}`)
  return { default: fallbackComponent() }
}
