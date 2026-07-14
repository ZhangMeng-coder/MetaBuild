/**
 * 角色管理 API
 * 提供角色的增删改查、状态管理以及菜单权限分配等功能
 */
import request from '@/utils/request'

/**
 * 分页查询角色列表
 * @param params - 查询参数，包含分页信息和筛选条件
 * @returns Promise - 返回分页后的角色列表数据
 */
export function page(params: any) {
  return request.post('/api/system/role/page', params)
}

/**
 * 新增角色
 * @param data - 角色信息，包含角色名称、角色编码、描述等
 * @returns Promise - 返回新增结果
 */
export function add(data: any) {
  return request.post('/api/system/role', data)
}

/**
 * 更新角色信息
 * @param data - 角色信息，包含角色 ID、角色名称、角色编码、描述等
 * @returns Promise - 返回更新结果
 */
export function update(data: any) {
  return request.put('/api/system/role', data)
}

/**
 * 删除角色
 * @param id - 角色 ID
 * @returns Promise - 返回删除结果
 */
export function del(id: number) {
  return request.delete(`/api/system/role/${id}`)
}

/**
 * 设置角色状态（启用/停用）
 * @param data - 状态参数
 * @param data.id - 角色 ID
 * @param data.status - 目标状态：1 启用，0 停用
 * @returns Promise - 返回状态更新结果
 */
export function setStatus(data: { id: number; status: number }) {
  return request.put('/api/system/role/status', data)
}

/**
 * 获取角色已分配的菜单 ID 列表
 * @param roleId - 角色 ID
 * @returns Promise - 返回菜单 ID 数组
 */
export function getMenus(roleId: number) {
  return request.get(`/api/system/role/${roleId}/menus`)
}

/**
 * 为角色分配菜单权限
 * @param roleId - 角色 ID
 * @param menuIds - 菜单 ID 数组
 * @returns Promise - 返回分配结果
 */
export function setMenus(roleId: number, menuIds: number[]) {
  return request.put(`/api/system/role/${roleId}/menus`, menuIds)
}
