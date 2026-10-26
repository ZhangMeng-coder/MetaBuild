<script setup lang="ts">
/**
 * 页面头部组件
 * 提供统一的"返回按钮 + 标题 + 右侧操作区"布局
 */
import { useRouter } from 'vue-router'

const props = withDefaults(defineProps<{
  /** 标题文字 */
  title: string
  /** 返回路径（为空则调用 router.back()） */
  backPath?: string
}>(), {
  backPath: ''
})

const router = useRouter()

function goBack() {
  if (props.backPath) {
    router.push(props.backPath)
  } else {
    router.back()
  }
}
</script>

<template>
  <div style="display: flex; align-items: center; justify-content: space-between">
    <div>
      <el-button @click="goBack">← 返回列表</el-button>
      <span style="margin-left: 12px; font-size: 16px; font-weight: bold">{{ title }}</span>
    </div>
    <div>
      <slot name="actions" />
    </div>
  </div>
</template>