/**
 * 菜单管理 API
 * 提供菜单树结构的获取、增删改查等功能
 */
import request from '@/utils/request'

/**
 * 获取菜单树形结构
 * 返回完整的菜单树，包含目录、菜单和按钮的层级关系
 * @returns Promise - 返回树形结构的菜单列表
 */
export function tree() {
  return request.get('/api/system/menu/tree')
}

/**
 * 新增菜单
 * @param data - 菜单信息，包含父级 ID、菜单名称、类型、路径、权限标识等
 * @returns Promise - 返回新增结果
 */
export function add(data: any) {
  return request.post('/api/system/menu', data)
}

/**
 * 更新菜单信息
 * @param data - 菜单信息，包含菜单 ID、菜单名称、类型、路径、权限标识等
 * @returns Promise - 返回更新结果
 */
export function update(data: any) {
  return request.put('/api/system/menu', data)
}

/**
 * 删除菜单
 * @param id - 菜单 ID
 * @returns Promise - 返回删除结果
 */
export function del(id: number) {
  return request.delete(`/api/system/menu/${id}`)
}
