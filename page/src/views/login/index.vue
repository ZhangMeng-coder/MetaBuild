<script setup lang="ts">
/**
 * 登录页面
 * 提供用户名/密码输入表单，调用认证 Store 进行登录，登录成功后跳转到首页
 */
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/store/auth'
import {ElMessage} from "element-plus";

const router = useRouter()
const authStore = useAuthStore()

/** 登录按钮加载状态 */
const loading = ref(false)
/** 登录表单数据 */
const form = reactive({
  username: '',  // 用户名
  password: ''   // 密码
})

/**
 * 处理登录
 * 验证表单数据，调用登录接口，成功则跳转到首页，失败则显示错误提示
 */
async function handleLogin() {
  // 表单校验：用户名和密码不能为空
  if (!form.username || !form.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    await authStore.login(form.username, form.password)
    ElMessage.success('登录成功')
    router.push('/')  // 跳转到首页
  } catch (e: any) {
    ElMessage.error(e?.message || '登录失败，请检查用户名和密码')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <!-- 登录页面容器：全屏居中，渐变背景 -->
  <div style="height: 100vh; display: flex; align-items: center; justify-content: center; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
    <!-- 登录卡片 -->
    <el-card style="width: 420px; padding: 20px" shadow="always">
      <h2 style="text-align: center; margin-bottom: 30px; color: #303133">系统登录</h2>
      <!-- 登录表单：支持回车键提交 -->
      <el-form :model="form" label-width="0" @keyup.enter="handleLogin">
        <el-form-item>
          <!-- 用户名输入框 -->
          <el-input v-model="form.username" placeholder="用户名" size="large" :prefix-icon="'User'" />
        </el-form-item>
        <el-form-item>
          <!-- 密码输入框（带显示/隐藏切换） -->
          <el-input v-model="form.password" type="password" placeholder="密码" size="large" :prefix-icon="'Lock'" show-password />
        </el-form-item>
        <el-form-item>
          <!-- 登录按钮，加载中时显示 loading 状态 -->
          <el-button type="primary" size="large" style="width: 100%" :loading="loading" @click="handleLogin">
            登 录
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>
