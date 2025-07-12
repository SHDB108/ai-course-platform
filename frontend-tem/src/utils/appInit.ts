import { useUserStoreWithOut } from '@/store/modules/user'
import { usePermissionStoreWithOut } from '@/store/modules/permission'

/**
 * 应用初始化函数
 * 在应用启动时调用，用于处理持久化的登录状态
 */
export async function initializeApp() {
  console.log('[应用初始化] 开始初始化应用')

  try {
    const userStore = useUserStoreWithOut()
    const permissionStore = usePermissionStoreWithOut()

    // 初始化用户状态
    const isLoggedIn = await userStore.initializeApp()

    if (!isLoggedIn) {
      console.log('[应用初始化] 用户未登录或登录状态无效')
      // 重置权限状态
      permissionStore.reset()
      return false
    }

    console.log('[应用初始化] 用户登录状态有效')
    return true
  } catch (error) {
    console.error('[应用初始化] 初始化失败:', error)
    return false
  }
}
