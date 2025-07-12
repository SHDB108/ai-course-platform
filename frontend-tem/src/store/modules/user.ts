import { defineStore } from 'pinia'
import { store } from '../index'
import { LoginResponseVO, UserInfoType, UserLoginType } from '@/api/login/types'
import { ElMessageBox } from 'element-plus'
import { useI18n } from '@/hooks/web/useI18n'
import { loginOutApi } from '@/api/login'
import { useTagsViewStore } from './tagsView'
import { usePermissionStoreWithOut } from './permission'
import router, { resetRouter } from '@/router'
import { cloneDeep } from 'lodash-es'
import { clearAllPersistedData } from '@/utils/storage'

interface UserState {
  userInfo?: UserInfoType
  tokenKey: string
  token: string
  rememberMe: boolean
  loginInfo?: UserLoginType
}

const initialState: UserState = {
  userInfo: undefined,
  tokenKey: 'Authorization',
  token: '',
  rememberMe: true,
  loginInfo: undefined
}

export const useUserStore = defineStore('user', {
  state: (): UserState => cloneDeep(initialState),
  getters: {
    getTokenKey(): string {
      return this.tokenKey
    },
    getToken(): string {
      return this.token
    },
    getUserInfo(): UserInfoType | undefined {
      return this.userInfo
    },
    getRememberMe(): boolean {
      return this.rememberMe
    },
    getLoginInfo(): UserLoginType | undefined {
      return this.loginInfo
    },
    getRole(): string {
      return this.userInfo?.role || ''
    }
  },
  actions: {
    setToken(token: string) {
      this.token = token
    },
    setUserInfo(loginResponse?: LoginResponseVO) {
      if (loginResponse) {
        this.setToken(loginResponse.token)
        this.userInfo = {
          userId: loginResponse.userId,
          username: loginResponse.username,
          // 确保这里能从登录响应中获取到 role
          role: loginResponse.role
        }
      } else {
        this.setToken('')
        this.userInfo = undefined
      }
    },
    // 改进的用户信息获取方法，增加token验证
    async fetchUserInfo() {
      try {
        if (!this.token) {
          console.warn('[用户Store] 无token，无法获取用户信息')
          return null
        }

        // 如果已有用户信息，直接返回
        if (this.userInfo && this.userInfo.role) {
          console.log('[用户Store] 从缓存获取用户信息:', this.userInfo)
          return this.userInfo
        }

        // TODO: 这里应该调用API验证token并获取用户信息
        // 目前由于没有对应的API，我们暂时返回缓存的用户信息
        console.log('[用户Store] 返回缓存的用户信息')
        return this.userInfo
      } catch (error) {
        console.error('[用户Store] 获取用户信息失败:', error)
        // 清除无效的登录状态
        this.clearUserInfo()
        throw error
      }
    },
    // 新增：清除用户信息的方法
    clearUserInfo() {
      this.token = ''
      this.userInfo = undefined
      this.loginInfo = undefined
    },
    // 新增：验证token有效性的方法
    isTokenValid(): boolean {
      if (!this.token) {
        return false
      }

      // TODO: 这里应该添加更复杂的token验证逻辑
      // 比如检查token过期时间、调用API验证等

      // 检查用户信息是否完整
      if (!this.userInfo || !this.userInfo.role) {
        console.warn('[用户Store] token存在但用户信息不完整')
        return false
      }

      return true
    },
    logoutConfirm() {
      const { t } = useI18n()
      ElMessageBox.confirm(t('common.loginOutMessage'), t('common.reminder'), {
        confirmButtonText: t('common.ok'),
        cancelButtonText: t('common.cancel'),
        type: 'warning'
      })
        .then(async () => {
          await this.logout()
        })
        .catch(() => {})
    },
    async logout() {
      console.log('[用户Store] 开始执行登出操作')

      try {
        await loginOutApi().catch(() => {})
      } catch (error) {
        console.error('[用户Store] 登出API调用失败:', error)
      }

      // 清除用户信息
      this.clearUserInfo()

      // 清除相关状态
      const tagsViewStore = useTagsViewStore()
      tagsViewStore.delAllViews()

      // 重置权限状态
      const permissionStore = usePermissionStoreWithOut()
      permissionStore.setIsAddRouters(false)
      permissionStore.reset()

      // 重置路由
      resetRouter()

      // 清除所有持久化数据
      clearAllPersistedData()

      console.log('[用户Store] 登出操作完成，跳转到登录页')

      // 跳转到登录页
      router.replace('/login')
    },
    reset() {
      this.$state = cloneDeep(initialState)
    },
    setRememberMe(rememberMe: boolean) {
      this.rememberMe = rememberMe
    },
    setLoginInfo(loginInfo: UserLoginType | undefined) {
      this.loginInfo = loginInfo
    },
    // 新增：初始化应用时检查登录状态
    async initializeApp() {
      console.log('[用户Store] 应用初始化，检查登录状态')

      // 如果没有token，直接返回
      if (!this.token) {
        console.log('[用户Store] 无token，不需要初始化')
        return false
      }

      // 检查登录状态是否有效
      if (!this.isTokenValid()) {
        console.warn('[用户Store] 初始化时发现token无效，清除登录状态')
        this.clearUserInfo()
        return false
      }

      console.log('[用户Store] 登录状态有效，用户信息:', this.userInfo)
      return true
    }
  },
  persist: true // 开启持久化
})

export const useUserStoreWithOut = () => {
  return useUserStore(store)
}
