<template>
  <!-- 慢SQL监控页面 -->
  <div class="app-container">
    <h2 class="page-title">慢SQL监控</h2>

    <!-- 搜索表单 -->
    <el-form :model="query" inline class="search-form">
      <el-form-item label="最小耗时（毫秒）">
        <el-input v-model="query.minCostMs" placeholder="请输入最小耗时" clearable style="width: 180px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 慢SQL列表 -->
    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="70" align="center" />
      <el-table-column prop="sqlContent" label="SQL内容" min-width="300">
        <template #default="{ row }">
          <div style="white-space: pre-wrap; word-break: break-all; line-height: 1.6">{{ row.sqlContent }}</div>
        </template>
      </el-table-column>
      <el-table-column prop="costMs" label="耗时(ms)" width="100" align="center">
        <template #default="{ row }">
          <el-tag type="danger" size="small">{{ row.costMs }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="methodName" label="Mapper方法" min-width="200" show-overflow-tooltip />
      <el-table-column prop="createTime" label="创建时间" width="180" />
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
import { slowSqlPage } from '@/api/monitor'

/**
 * 慢SQL接口定义
 */
interface SlowSqlRecord {
  id: number
  sqlContent: string
  costMs: number
  methodName: string
  createTime: string
}

// 加载状态
const loading = ref(false)
// 表格数据
const tableData = ref<SlowSqlRecord[]>([])
// 总记录数
const total = ref(0)
// 查询参数
const query = reactive({
  pageNum: 1,
  pageSize: 10,
  minCostMs: ''
})

/**
 * 获取慢SQL列表
 */
const getList = async () => {
  loading.value = true
  try {
    const res = await slowSqlPage(query)
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
  query.minCostMs = ''
  query.pageNum = 1
  getList()
}

onMounted(() => {
  getList()
})
</script>
