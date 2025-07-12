/**
 * 清除所有持久化数据的工具函数
 * 用于彻底清除应用的缓存状态
 */
export function clearAllPersistedData() {
  console.log('[清除数据] 开始清除所有持久化数据')

  try {
    // 清除 localStorage
    const keysToRemove = []
    for (let i = 0; i < localStorage.length; i++) {
      const key = localStorage.key(i)
      if (
        key &&
        (key.startsWith('user') || key.startsWith('permission') || key.startsWith('pinia'))
      ) {
        keysToRemove.push(key)
      }
    }

    keysToRemove.forEach((key) => {
      localStorage.removeItem(key)
      console.log(`[清除数据] 已清除 localStorage 键: ${key}`)
    })

    // 清除 sessionStorage
    const sessionKeysToRemove = []
    for (let i = 0; i < sessionStorage.length; i++) {
      const key = sessionStorage.key(i)
      if (
        key &&
        (key.startsWith('user') || key.startsWith('permission') || key.startsWith('pinia'))
      ) {
        sessionKeysToRemove.push(key)
      }
    }

    sessionKeysToRemove.forEach((key) => {
      sessionStorage.removeItem(key)
      console.log(`[清除数据] 已清除 sessionStorage 键: ${key}`)
    })

    console.log('[清除数据] 所有持久化数据已清除')
    return true
  } catch (error) {
    console.error('[清除数据] 清除过程中发生错误:', error)
    return false
  }
}

/**
 * 强制重置应用状态的工具函数
 * 用于测试和调试
 */
export function forceResetApp() {
  console.log('[强制重置] 开始强制重置应用状态')

  // 清除所有持久化数据
  clearAllPersistedData()

  // 刷新页面
  setTimeout(() => {
    window.location.reload()
  }, 100)
}
