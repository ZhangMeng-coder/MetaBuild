/**
 * 用户管理 API
 * 提供用户信息的增删改查、状态管理、密码重置及角色分配等功能
 */
import request from '@/utils/request'

/**
 * 分页查询用户列表
 * @param params - 查询参数，包含分页信息和筛选条件
 * @returns Promise - 返回分页后的用户列表数据
 */
export function page(params: any) {
  return request.post('/api/system/user/page', params)
}

/**
 * 新增用户
 * @param data - 用户信息，包含用户名、密码、真实姓名、邮箱、手机号等
 * @returns Promise - 返回新增结果
 */
export function add(data: any) {
  return request.post('/api/system/user', data)
}

/**
 * 更新用户信息
 * @param data - 用户信息，包含用户 ID、用户名、真实姓名、邮箱、手机号等
 * @returns Promise - 返回更新结果
 */
export function update(data: any) {
  return request.put('/api/system/user', data)
}

/**
 * 删除用户
 * @param id - 用户 ID
 * @returns Promise - 返回删除结果
 */
export function del(id: number) {
  return request.delete(`/api/system/user/${id}`)
}

/**
 * 重置用户密码
 * @param id - 用户 ID
 * @returns Promise - 返回重置结果，密码通常重置为默认密码
 */
export function resetPassword(id: number) {
  return request.put(`/api/system/user/reset-password/${id}`)
}

/**
 * 设置用户状态（启用/停用）
 * @param data - 状态参数
 * @param data.id - 用户 ID
 * @param data.status - 目标状态：1 启用，0 停用
 * @returns Promise - 返回状态更新结果
 */
export function setStatus(data: { id: number; status: number }) {
  return request.put('/api/system/user/status', data)
}

/**
 * 获取用户已分配的角色 ID 列表
 * @param userId - 用户 ID
 * @returns Promise - 返回角色 ID 数组
 */
export function getRoles(userId: number) {
  return request.get(`/api/system/user/${userId}/roles`)
}

/**
 * 为用户分配角色
 * @param userId - 用户 ID
 * @param roleIds - 角色 ID 数组
 * @returns Promise - 返回分配结果
 */
export function setRoles(userId: number, roleIds: number[]) {
  return request.put(`/api/system/user/${userId}/roles`, roleIds)
}
