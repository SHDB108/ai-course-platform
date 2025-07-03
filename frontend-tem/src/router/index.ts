import type { RouteRecordRaw } from 'vue-router'
import { createRouter, createWebHashHistory } from 'vue-router'
import type { App } from 'vue'
import { Layout } from '@/utils/routerHelper'

/**
 * constantRoutes:
 * 无需权限要求的基础页面，所有角色都可以访问。
 */
export const constantRoutes: AppRouteRecordRaw[] = [
  {
    path: '/login',
    component: () => import('@/views/Login/Login.vue'),
    name: 'Login',
    meta: {
      hidden: true,
      title: '登录',
      noTagsView: true
    }
  },
  {
    path: '/register',
    component: () => import('@/views/Login/components/RegisterForm.vue'),
    name: 'Register',
    meta: {
      hidden: true,
      title: '注册',
      noTagsView: true
    }
  },
  {
    path: '/404',
    component: () => import('@/views/Error/404.vue'),
    name: 'NoFind',
    meta: {
      hidden: true,
      title: '404',
      noTagsView: true
    }
  }
]

/**
 * asyncRoutes:
 * 需要根据用户角色动态加载的路由。
 * 每个路由的 meta.roles 字段定义了可以访问该路由的角色。
 */
export const asyncRoutes: AppRouteRecordRaw[] = [
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard/analysis',
    name: 'Root',
    meta: {
      // 此处不设置 roles，表示所有登录用户都有权限访问根路径
      // 这对于确保重定向正常工作至关重要
    }
  },
  {
    path: '/dashboard',
    component: Layout,
    name: 'Dashboard',
    meta: {
      title: '首页',
      icon: 'ant-design:dashboard-filled',
      alwaysShow: true
      // 父级菜单的 roles 可选，如果子菜单有 roles，父菜单会自动显示
    },
    children: [
      {
        path: 'analysis',
        component: () => import('@/views/Dashboard/Analysis.vue'),
        name: 'Analysis',
        meta: {
          title: '分析页',
          roles: ['ADMIN', 'TEACHER'] // 仅限 ADMIN 和 TEACHER
        }
      },
      {
        path: 'workplace',
        component: () => import('@/views/Dashboard/Workplace.vue'),
        name: 'Workplace',
        meta: {
          title: '工作台',
          roles: ['ADMIN'] // 仅限 ADMIN
        }
      }
    ]
  }
  // 注意：通配符 404 路由已从此数组中移除
]

const router = createRouter({
  history: createWebHashHistory(),
  strict: true,
  routes: constantRoutes as RouteRecordRaw[],
  scrollBehavior: () => ({ left: 0, top: 0 })
})

export const resetRouter = (): void => {
  const resetWhiteNameList = ['Redirect', 'Login', 'NoFind', 'Root']
  router.getRoutes().forEach((route) => {
    const { name } = route
    if (name && !resetWhiteNameList.includes(name as string)) {
      router.hasRoute(name) && router.removeRoute(name)
    }
  })
}

export const setupRouter = (app: App<Element>) => {
  app.use(router)
}

export default router
