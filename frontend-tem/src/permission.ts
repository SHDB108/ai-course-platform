import router from './router'
import type { RouteRecordRaw } from 'vue-router'
import { useNProgress } from '@/hooks/web/useNProgress'
import { usePageLoading } from '@/hooks/web/usePageLoading'
import { useUserStoreWithOut } from '@/store/modules/user'
import { usePermissionStoreWithOut } from '@/store/modules/permission'
import { NO_REDIRECT_WHITE_LIST } from '@/constants'
import { useTitle } from '@/hooks/web/useTitle'
import { path404 } from './router'

const { start, done } = useNProgress()
const { loadStart, loadDone } = usePageLoading()

router.beforeEach(async (to, from, next) => {
  // 打印导航路径
  console.log(`[路由守卫] 导航开始: 从 ${from.path} 到 ${to.path}`)
  start()
  loadStart()
  const userStore = useUserStoreWithOut()
  const permissionStore = usePermissionStoreWithOut()

  // 检查是否有token
  if (userStore.getToken) {
    console.log('[路由守卫] 发现token，验证中...')

    // 验证token有效性
    if (!userStore.isTokenValid()) {
      console.warn('[路由守卫] token无效，清除登录状态')
      await userStore.logout()
      next(`/login?redirect=${to.path}`)
      return
    }

    if (to.path === '/login') {
      next({ path: '/' })
      return
    }

    // 获取用户角色，这是关键信息
    const userRole = userStore.getRole
    console.log('[路由守卫] 获取到的用户角色:', userRole)

    if (userRole) {
      // 如果有角色信息
      if (!permissionStore.getIsAddRouters) {
        console.log('[路由守卫] 路由尚未添加，开始生成...')
        // 生成路由
        const accessRoutes = await permissionStore.generateRoutes([userRole])
        // 打印生成的路由，看是否为空
        console.log('[路由守卫] 生成的可用路由:', accessRoutes)

        // 增加一个健壮性检查
        if (!accessRoutes || accessRoutes.length === 0) {
          console.error(
            '[路由守卫] 错误：未能为该角色生成任何可用路由。请检查角色名称和路由元信息（meta.roles）。将登出处理。'
          )
          await userStore.logout()
          next(`/login?redirect=${to.path}`)
          return
        }

        // 添加路由
        accessRoutes.forEach((route) => {
          router.addRoute(route as unknown as RouteRecordRaw)
        })
        router.addRoute(path404 as unknown as RouteRecordRaw)
        permissionStore.setIsAddRouters(true)

        console.log('[路由守卫] 路由已添加，发起重定向...')

        // 如果目标路由是根路径，且用户角色有权限访问首页，则重定向到首页
        if (
          to.path === '/' &&
          accessRoutes.some(
            (route) =>
              route.path === '/dashboard' ||
              route.children?.some((child) => child.path === 'overview')
          )
        ) {
          next({ path: '/dashboard/overview', replace: true })
        } else {
          next({ ...to, replace: true })
        }
      } else {
        console.log('[路由守卫] 路由已存在，直接放行。')
        next()
      }
    } else {
      // 如果没有角色信息
      try {
        console.log('[路由守卫] 未发现角色，正在异步获取用户信息...')
        // 调用 fetchUserInfo 方法
        const userInfo = await userStore.fetchUserInfo()

        if (userInfo && userInfo.role) {
          console.log('[路由守卫] 用户信息获取成功，重新执行导航。')
          next({ ...to, replace: true })
        } else {
          console.warn('[路由守卫] 获取的用户信息无效，清除登录状态')
          await userStore.logout()
          next(`/login?redirect=${to.path}`)
        }
      } catch (error) {
        console.error('[路由守卫] 获取用户信息失败:', error)
        await userStore.logout()
        next(`/login?redirect=${to.path}`)
      }
    }
  } else {
    // 未登录
    console.log('[路由守卫] 未发现token，检查白名单')
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
