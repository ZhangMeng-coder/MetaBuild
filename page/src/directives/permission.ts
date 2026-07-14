/**
 * 权限指令（v-hasPermission）
 * 根据当前用户的权限列表控制 DOM 元素的显示与隐藏。
 * 如果用户不具备指定权限，则该元素将被从 DOM 中移除。
 *
 * 用法：<el-button v-hasPermission="'system:user:add'">新增用户</el-button>
 */
import type { Directive } from 'vue'
import { useAuthStore } from '@/store/auth'

/** 从用户菜单树中递归提取所有权限标识列表 */
function getPermissionList(menus: any[]): string[] {
  const perms: string[] = []
  function traverse(list: any[]) {
    for (const item of list) {
      if (item.perms) {
        perms.push(item.perms)
      }
      if (item.children && item.children.length > 0) {
        traverse(item.children)
      }
    }
  }
  traverse(menus)
  return perms
}

const hasPermission: Directive = {
  mounted(el: HTMLElement, binding) {
    // 获取当前用户信息
    const authStore = useAuthStore()
    const userInfo = authStore.userInfo
    if (!userInfo) {
      el.parentNode?.removeChild(el)
      return
    }

    // 获取用户的权限标识列表
    const userPermissions = getPermissionList(userInfo.menus)

    // 获取指令值（需要的权限标识），可能为字符串或数组
    const requiredPermissions = binding.value
    if (!requiredPermissions) {
      // 未传入权限标识，不移除
      return
    }

    // 转换为数组统一处理
    const permList: string[] = Array.isArray(requiredPermissions)
      ? requiredPermissions
      : [requiredPermissions]

    // 检查用户是否拥有所需的所有权限（与关系）
    const hasAll = permList.every((perm: string) => userPermissions.includes(perm))

    if (!hasAll) {
      el.parentNode?.removeChild(el)
    }
  },
}

export default hasPermission
