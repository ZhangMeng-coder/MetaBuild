<script setup lang="ts">
/**
 * 后台管理布局组件
 * 提供左右结构布局：左侧菜单导航栏 + 右侧顶部栏和内容区域
 * 页面加载时获取用户信息及菜单权限，支持退出登录和菜单导航
 */
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/store/auth'

const router = useRouter()
const authStore = useAuthStore()

/** 用户拥有的菜单列表 */
const menus = ref<any[]>([])

onMounted(async () => {
  try {
    // 获取用户信息（包括菜单权限）
    await authStore.getUserInfo()
    menus.value = authStore.userInfo?.menus || []
  } catch {
    // 获取用户信息失败（如 token 过期），自动退出登录
    authStore.logout()
    router.push('/login')
  }
})

/** 当前用户信息（只读计算属性） */
const userInfo = computed(() => authStore.userInfo)

/**
 * 退出登录
 * 清除认证状态并跳转到登录页
 */
function handleLogout() {
  authStore.logout()
  router.push('/login')
}

/**
 * 菜单选择处理
 * 根据选中的菜单路径进行路由跳转
 * @param index - 选中菜单的路径（index 值）
 */
function handleMenuSelect(index: string) {
  router.push(index)
}
</script>

<!--
  后台管理布局模板
  左侧：侧边栏菜单导航
  右侧：顶部用户信息栏 + 主内容区域（路由视图）
-->
<template>
  <el-container style="height: 100vh">
    <!-- 左侧侧边栏 -->
    <el-aside width="220px" style="background-color: #304156">
      <!-- 系统标题 -->
      <div style="height: 60px; display: flex; align-items: center; justify-content: center; color: #fff; font-size: 20px; font-weight: bold; border-bottom: 1px solid rgba(255,255,255,0.1)">
        管理系统
      </div>
      <!-- 左侧导航菜单 -->
      <el-menu
        :default-active="router.currentRoute.value.path"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
        @select="handleMenuSelect"
      >
        <template v-for="menu in menus" :key="menu.id">
          <!-- 有子菜单：渲染为可展开的子菜单 -->
          <el-sub-menu v-if="menu.children && menu.children.length > 0" :index="menu.path">
            <template #title>
              <span>{{ menu.menuName }}</span>
            </template>
            <el-menu-item v-for="child in menu.children" :key="child.id" :index="child.path">
              <span>{{ child.menuName }}</span>
            </el-menu-item>
          </el-sub-menu>
          <!-- 无子菜单：渲染为普通菜单项 -->
          <el-menu-item v-else :index="menu.path">
            <span>{{ menu.menuName }}</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>
    <!-- 右侧内容区 -->
    <el-container>
      <!-- 顶部栏：显示当前用户信息，包含退出登录下拉菜单 -->
      <el-header style="background-color: #fff; border-bottom: 1px solid #e6e6e6; display: flex; align-items: center; justify-content: flex-end; padding: 0 20px">
        <el-dropdown trigger="click">
          <span style="cursor: pointer; display: flex; align-items: center; gap: 8px">
            <!-- 用户头像（显示姓名首字） -->
            <el-avatar :size="32">{{ userInfo?.realName?.charAt(0) || 'U' }}</el-avatar>
            <!-- 用户姓名 -->
            <span>{{ userInfo?.realName || userInfo?.username || '用户' }}</span>
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>
      <!-- 主内容区域：显示当前路由对应的页面 -->
      <el-main style="background-color: #f0f2f5; padding: 20px">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>
