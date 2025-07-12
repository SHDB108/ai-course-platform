import { defineStore } from 'pinia'
import { asyncRoutes, constantRoutes } from '@/router'
import { store } from '../index'
import { cloneDeep } from 'lodash-es'

export interface PermissionState {
  routers: AppRouteRecordRaw[]
  addRouters: AppRouteRecordRaw[]
  isAddRouters: boolean
  menuTabRouters: AppRouteRecordRaw[]
}

// 递归地根据角色过滤异步路由
function filterAsyncRoutes(routes: AppRouteRecordRaw[], roles: string[]): AppRouteRecordRaw[] {
  const res: AppRouteRecordRaw[] = []

  routes.forEach((route) => {
    const tmp = { ...route }
    if (hasPermission(roles, tmp)) {
      if (tmp.children) {
        tmp.children = filterAsyncRoutes(tmp.children, roles)
        // 改进：即使子路由为空，如果父路由有权限也应该显示
        // 但是如果父路由没有定义组件且子路由为空，则不显示
        if (tmp.children.length > 0 || tmp.component) {
          res.push(tmp)
        }
      } else {
        res.push(tmp)
      }
    }
  })

  return res
}

// 判断用户角色是否有权限访问该路由
function hasPermission(roles: string[], route: AppRouteRecordRaw): boolean {
  if (route.meta && route.meta.roles) {
    return roles.some((role) => (route.meta.roles as string[]).includes(role))
  } else {
    return true // 如果路由没有定义 meta.roles，则默认所有角色都可以访问
  }
}

export const usePermissionStore = defineStore('permission', {
  state: (): PermissionState => ({
    routers: [],
    addRouters: [],
    isAddRouters: false,
    menuTabRouters: []
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
    },
    getMenuTabRouters(): AppRouteRecordRaw[] {
      return this.menuTabRouters
    }
  },
  actions: {
    generateRoutes(roles: string[]): Promise<AppRouteRecordRaw[]> {
      return new Promise((resolve) => {
        // 所有角色都使用过滤逻辑，包括管理员
        const accessedRouters = filterAsyncRoutes(cloneDeep(asyncRoutes), roles)
        this.addRouters = accessedRouters
        this.routers = cloneDeep(constantRoutes).concat(accessedRouters)
        this.isAddRouters = true
        resolve(accessedRouters)
      })
    },
    setIsAddRouters(state: boolean) {
      this.isAddRouters = state
    },
    setMenuTabRouters(routers: AppRouteRecordRaw[]) {
      this.menuTabRouters = routers
    },
    // 新增：重置权限状态
    reset() {
      this.routers = []
      this.addRouters = []
      this.isAddRouters = false
      this.menuTabRouters = []
    }
  },
  persist: true
})

export const usePermissionStoreWithOut = () => {
  return usePermissionStore(store)
}
