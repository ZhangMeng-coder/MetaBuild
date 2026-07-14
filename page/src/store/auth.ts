/**
 * 认证状态管理（Pinia Store）
 * 管理用户的登录状态、Token 和用户信息，提供登录、获取用户信息、登出等操作
 */
import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/utils/request'
import { getToken, setToken, removeToken } from '@/utils/cookie'

/** 用户信息接口定义 */
interface UserInfo {
  userId: number       // 用户 ID
  username: string     // 用户名
  realName: string     // 真实姓名
  roles: string[]      // 角色标识列表
  menus: any[]         // 菜单权限列表
}

/** 认证状态 Store */
export const useAuthStore = defineStore('auth', () => {
  /** 登录 Token，初始化时从 Cookie 中读取 */
  const token = ref(getToken())
  /** 当前登录用户信息 */
  const userInfo = ref<UserInfo | null>(null)

  /**
   * 用户登录
   * @param username - 用户名
   * @param password - 密码
   * @returns 登录响应数据，包含 token
   * @throws 登录失败时抛出错误
   */
  async function login(username: string, password: string) {
    const res: any = await request.post('/api/auth/login', { username, password })
    // 业务状态码非 200 视为登录失败
    if (res.code !== 200) {
      throw new Error(res.msg || '登录失败')
    }
    // 保存 token 到内存和 Cookie
    token.value = res.data.token
    setToken(res.data.token)
    return res
  }

  /**
   * 获取当前登录用户信息
   * 从后端获取用户基本信息、角色和菜单权限，保存到 store 中
   * @returns 用户信息响应数据
   * @throws 获取失败时抛出错误
   */
  async function getUserInfo() {
    const res: any = await request.get('/api/auth/me')
    if (res.code !== 200) {
      throw new Error(res.msg || '获取用户信息失败')
    }
    userInfo.value = res.data
    return res
  }

  /**
   * 退出登录
   * 清空内存中的 token 和用户信息，同时清除 Cookie 中的 token
   */
  function logout() {
    token.value = ''
    userInfo.value = null
    removeToken()
  }

  return { token, userInfo, login, getUserInfo, logout }
})