import { defineStore } from 'pinia'
import { store } from '../index'
import { LoginResponseVO, UserInfoType, UserLoginType } from '@/api/login/types'
import { ElMessageBox } from 'element-plus'
import { useI18n } from '@/hooks/web/useI18n'
import { loginOutApi } from '@/api/login'
import { useTagsViewStore } from './tagsView'
import router, { resetRouter } from '@/router'
import { cloneDeep } from 'lodash-es'

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
      await loginOutApi().catch(() => {})
      const tagsViewStore = useTagsViewStore()
      tagsViewStore.delAllViews()
      resetRouter() // 重置路由
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
    }
  },
  persist: true // 开启持久化
})

export const useUserStoreWithOut = () => {
  return useUserStore(store)
}
