import router from './router'
// import { useAppStoreWithOut } from '@/store/modules/app'
import type { RouteRecordRaw } from 'vue-router'
import { useTitle } from '@/hooks/web/useTitle'
import { useNProgress } from '@/hooks/web/useNProgress'
import { usePermissionStoreWithOut } from '@/store/modules/permission'
import { usePageLoading } from '@/hooks/web/usePageLoading'
import { NO_REDIRECT_WHITE_LIST } from '@/constants'
import { useUserStoreWithOut } from '@/store/modules/user'

const { start, done } = useNProgress()
const { loadStart, loadDone } = usePageLoading()

router.beforeEach(async (to, from, next) => {
  start()
  loadStart()
  const permissionStore = usePermissionStoreWithOut()
  const userStore = useUserStoreWithOut()

  if (userStore.getToken) {
    if (to.path === '/login') {
      next({ path: '/' })
    } else {
      // 如果已经添加过路由，直接放行
      if (permissionStore.getIsAddRouters) {
        next()
        return
      }

      // 获取用户角色 - 确保你的登录API返回了角色信息，并存储在userInfo中
      const role = userStore.getUserInfo?.role
      if (!role) {
        // 如果没有角色信息，说明用户信息不完整，登出并重新登录
        await userStore.logout()
        next(`/login?redirect=${to.path}`)
        return
      }

      // 根据角色生成可访问的路由
      const accessRoutes = await permissionStore.generateRoutes([role]) // 传入角色数组

      // 动态添加可访问路由
      accessRoutes.forEach((route) => {
        router.addRoute(route as unknown as RouteRecordRaw)
      })

      // hack method to ensure that addRoutes is complete
      // set the replace: true, so the navigation will not leave a history record
      const redirectPath = from.query.redirect || to.path
      const redirect = decodeURIComponent(redirectPath as string)
      const nextData = to.path === redirect ? { ...to, replace: true } : { path: redirect }
      next(nextData)
    }
  } else {
    if (NO_REDIRECT_WHITE_LIST.indexOf(to.path) !== -1) {
      next()
    } else {
      next(`/login?redirect=${to.path}`)
    }
  }
})

router.afterEach((to) => {
  useTitle(to?.meta?.title as string)
  done()
  loadDone()
})
