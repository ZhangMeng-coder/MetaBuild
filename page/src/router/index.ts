/**
 * 路由配置
 * 定义系统路由表，包含登录页面和后台管理系统页面，并配置登录鉴权守卫
 */
import { createRouter, createWebHashHistory } from 'vue-router'
import { getToken } from '@/utils/cookie'
import type { RouteRecordRaw } from 'vue-router'

/** 路由表定义 */
const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue')
  },
  {
    path: '/',
    component: () => import('@/layout/index.vue'),
    redirect: '/system/user', // 默认重定向到用户管理页
    children: [
      {
        path: 'system/user',
        name: 'User',
        component: () => import('@/views/system/user/index.vue'),
        meta: { title: '用户管理', perms: 'system:user:list' }
      },
      {
        path: 'system/role',
        name: 'Role',
        component: () => import('@/views/system/role/index.vue'),
        meta: { title: '角色管理', perms: 'system:role:list' }
      },
      {
        path: 'system/menu',
        name: 'Menu',
        component: () => import('@/views/system/menu/index.vue'),
        meta: { title: '菜单管理', perms: 'system:menu:list' }
      },
      {
        path: 'system/log',
        name: 'Log',
        component: () => import('@/views/system/log/index.vue'),
        meta: { title: '操作日志', perms: 'system:log:list' }
      },
      {
        path: 'system/monitor/slow-sql',
        name: 'SlowSql',
        component: () => import('@/views/system/monitor/slow-sql.vue'),
        meta: { title: '慢SQL监控', perms: 'system:monitor:slow-sql' }
      },
      {
        path: 'system/monitor/api-timing',
        name: 'ApiTiming',
        component: () => import('@/views/system/monitor/api-timing.vue'),
        meta: { title: '接口耗时监控', perms: 'system:monitor:api-timing' }
      },
    ]
  }
]

/** 创建路由实例，使用 Hash 模式 */
const router = createRouter({
  history: createWebHashHistory(),
  routes
})

/**
 * 路由前置守卫 - 登录鉴权
 * 检查用户是否已登录（通过 Cookie 中的 Token 判断），
 * 未登录且访问非登录页时，自动跳转到登录页
 */
router.beforeEach((to, from, next) => {
  const token = getToken()
  if (to.path !== '/login' && !token) {
    // 未登录，重定向到登录页
    next('/login')
  } else {
    // 已登录或目标为登录页，放行
    next()
  }
})

export default router