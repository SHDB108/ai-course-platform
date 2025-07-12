import { ComponentPublicInstance } from 'vue'

/**
 * 动态导入修复工具
 * 用于解决Vue组件动态导入失败的问题
 */

export class ImportFixer {
  private static retryCount = 0
  private static maxRetries = 3

  /**
   * 安全的动态导入
   * @param importFn 导入函数
   * @param componentName 组件名称
   */
  static async safeImport(
    importFn: () => Promise<any>,
    componentName: string
  ): Promise<ComponentPublicInstance | null> {
    try {
      console.log(`[导入修复] 尝试导入组件: ${componentName}`)
      const module = await importFn()
      console.log(`[导入修复] 成功导入组件: ${componentName}`)
      this.retryCount = 0
      return module.default || module
    } catch (error) {
      console.error(`[导入修复] 导入组件失败: ${componentName}`, error)

      if (this.retryCount < this.maxRetries) {
        this.retryCount++
        console.log(`[导入修复] 重试导入 (${this.retryCount}/${this.maxRetries}): ${componentName}`)

        // 延迟重试
        await new Promise((resolve) => setTimeout(resolve, 1000))
        return this.safeImport(importFn, componentName)
      }

      // 返回错误组件
      return this.createErrorComponent(componentName, error)
    }
  }

  /**
   * 创建错误组件
   */
  private static createErrorComponent(componentName: string, error: any) {
    return {
      name: `${componentName}Error`,
      template: `
        <div class="error-component">
          <h3>组件加载失败: {{ componentName }}</h3>
          <p>错误信息: {{ error.message }}</p>
          <button @click="reload">重新加载</button>
        </div>
      `,
      data() {
        return {
          componentName,
          error
        }
      },
      methods: {
        reload() {
          window.location.reload()
        }
      }
    }
  }

  /**
   * 预加载关键组件
   */
  static async preloadCriticalComponents() {
    const criticalComponents = [
      { name: 'MyCourses', path: '@/views/Learning/MyCourses.vue' },
      { name: 'MyTasks', path: '@/views/Learning/MyTasks.vue' },
      { name: 'MyExams', path: '@/views/Learning/MyExams.vue' },
      { name: 'MyGrades', path: '@/views/Learning/MyGrades.vue' }
    ]

    console.log('[导入修复] 开始预加载关键组件')

    for (const component of criticalComponents) {
      try {
        await import(/* @vite-ignore */ component.path)
        console.log(`[导入修复] 预加载成功: ${component.name}`)
      } catch (error) {
        console.warn(`[导入修复] 预加载失败: ${component.name}`, error)
      }
    }
  }
}
