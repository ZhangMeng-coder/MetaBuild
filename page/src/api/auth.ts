/**
 * 认证相关 API
 * 提供登录和获取当前用户信息的功能
 */
import request from '@/utils/request'

/**
 * 用户登录
 * @param data - 登录参数，包含用户名和密码
 * @param data.username - 用户名
 * @param data.password - 密码
 * @returns Promise - 返回登录结果，包含 token 等数据
 */
export function login(data: { username: string; password: string }) {
  return request.post('/api/auth/login', data)
}

/**
 * 获取当前登录用户信息
 * @returns Promise - 返回用户信息，包含用户基本信息、角色、菜单权限等
 */
export function getUserInfo() {
  return request.get('/api/auth/me')
}
