<template>
  <!-- 操作日志管理页面 -->
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-form :model="query" inline class="search-form">
      <el-form-item label="操作名称">
        <el-input v-model="query.operationName" placeholder="请输入操作名称" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 日志列表 -->
    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="id" label="日志ID" width="70" align="center" />
      <el-table-column prop="operationName" label="操作名称" width="150" />
      <el-table-column prop="httpMethod" label="请求方式" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="methodTagType(row.httpMethod)" size="small">{{ row.httpMethod }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="url" label="请求URL" min-width="250" show-overflow-tooltip />
      <el-table-column prop="params" label="请求参数" min-width="200" show-overflow-tooltip />
      <el-table-column prop="resultCode" label="响应码" width="80" align="center">
        <template #default="{ row }">
          <el-tag :type="row.resultCode === 200 ? 'success' : 'danger'" size="small">{{ row.resultCode }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="resultMsg" label="响应信息" width="120" show-overflow-tooltip />
      <el-table-column prop="operationTime" label="操作时间" width="180" />
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-model:current-page="query.pageNum"
      v-model:page-size="query.pageSize"
      :page-sizes="[10, 20, 50, 100]"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
      class="pagination"
      @size-change="getList"
      @current-change="getList"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { page } from '@/api/log'

/**
 * 操作日志接口定义
 */
interface SysUserLog {
  id: number
  operationName: string
  httpMethod: string
  url: string
  params: string
  resultCode: number
  resultMsg: string
  operationTime: string
}

// 加载状态
const loading = ref(false)
// 表格数据
const tableData = ref<SysUserLog[]>([])
// 总记录数
const total = ref(0)
// 查询参数
const query = reactive({
  pageNum: 1,
  pageSize: 10,
  operationName: ''
})

/**
 * 获取操作日志列表
 */
const getList = async () => {
  loading.value = true
  try {
    const res = await page(query)
    tableData.value = res.data.records
    total.value = res.data.total
  } catch {
    // 错误由拦截器统一提示
  } finally {
    loading.value = false
  }
}

/**
 * 查询按钮点击事件
 */
const handleSearch = () => {
  query.pageNum = 1
  getList()
}

/**
 * 重置按钮点击事件
 */
const handleReset = () => {
  query.operationName = ''
  query.pageNum = 1
  getList()
}

/**
 * 请求方式对应的标签类型
 */
const methodTagType = (method: string) => {
  const map: Record<string, string> = {
    GET: 'default',
    POST: 'primary',
    PUT: 'warning',
    DELETE: 'danger'
  }
  return map[method] || 'info'
}

onMounted(() => {
  getList()
})
</script>
