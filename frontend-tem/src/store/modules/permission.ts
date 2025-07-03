import { defineStore } from 'pinia'
import { asyncRoutes, constantRoutes } from '@/router'
import { store } from '../index'
import { cloneDeep } from 'lodash-es'

export interface PermissionState {
  routers: AppRouteRecordRaw[]
  addRouters: AppRouteRecordRaw[]
  isAddRouters: boolean
}

/**
 * 递归地根据角色过滤异步路由
 * @param routes asyncRoutes
 * @param roles 用户的角色
 */
function filterAsyncRoutes(routes: AppRouteRecordRaw[], roles: string[]): AppRouteRecordRaw[] {
  const res: AppRouteRecordRaw[] = []

  routes.forEach((route) => {
    const tmp = { ...route }
    if (hasPermission(roles, tmp)) {
      if (tmp.children) {
        tmp.children = filterAsyncRoutes(tmp.children, roles)
        // 如果过滤后的子路由不为空，则父路由也应该显示
        if (tmp.children.length > 0) {
          res.push(tmp)
        }
      } else {
        res.push(tmp)
      }
    }
  })

  return res
}

/**
 * 判断用户角色是否有权限访问该路由
 * @param roles 用户的角色
 * @param route 当前路由
 */
function hasPermission(roles: string[], route: AppRouteRecordRaw): boolean {
  if (route.meta && route.meta.roles) {
    // some() 方法测试数组中是不是至少有1个元素通过了被提供的函数测试
    // 这里是判断用户的角色数组中，是否至少有一个角色存在于路由的 meta.roles 中
    return roles.some((role) => (route.meta.roles as string[]).includes(role))
  } else {
    // 如果路由没有定义 meta.roles，则默认所有角色都可以访问
    return true
  }
}

export const usePermissionStore = defineStore('permission', {
  state: (): PermissionState => ({
    routers: [],
    addRouters: [],
    isAddRouters: false
  }),
  getters: {
    getRouters(): AppRouteRecordRaw[] {
      return this.routers
    },
    getAddRouters(): AppRouteRecordRaw[] {
      return this.addRouters
    },
    getIsAddRouters(): boolean {
      return this.isAddRouters
    }
  },
  actions: {
    // 根据角色生成路由
    generateRoutes(roles: string[]): Promise<AppRouteRecordRaw[]> {
      return new Promise((resolve) => {
        let accessedRouters: AppRouteRecordRaw[] = []
        // 如果角色中包含 'ADMIN'，则拥有所有路由权限
        if (roles.includes('ADMIN')) {
          accessedRouters = cloneDeep(asyncRoutes)
        } else {
          // 否则，根据角色过滤路由
          accessedRouters = filterAsyncRoutes(cloneDeep(asyncRoutes), roles)
        }
        this.addRouters = accessedRouters
        this.routers = cloneDeep(constantRoutes).concat(accessedRouters)
        this.isAddRouters = true
        resolve(accessedRouters)
      })
    },
    setIsAddRouters(state: boolean) {
      this.isAddRouters = state
    }
  },
  // 开启持久化
  persist: true
})

export const usePermissionStoreWithOut = () => {
  return usePermissionStore(store)
}
