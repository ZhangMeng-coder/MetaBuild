<script setup lang="ts">
/**
 * 字典数据管理页面
 * 提供字典数据的增删改查功能，以表格形式展示
 * 通过路由参数 dictType 关联到对应的字典类型
 */
import { ref, reactive, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import * as dictApi from '@/api/dict'
import { ElMessage, ElMessageBox } from 'element-plus'

/** 字典数据信息接口 */
interface DictData {
  id: number
  dictType: string
  dictLabel: string
  dictValue: string
  dictSort: number
  cssClass: string
  listClass: string
  isDefault: number
  status: number
  remark: string
  createTime: string
}

const route = useRoute()

/** 表格数据 */
const tableData = ref<DictData[]>([])
/** 数据总条数 */
const total = ref(0)
/** 表格加载状态 */
const loading = ref(false)
/** 当前字典类型 */
const currentDictType = ref('')

/** 查询参数 */
const queryParams = reactive({
  dictLabel: '',
  pageNum: 1,
  pageSize: 10
})

/** 新增/编辑对话框可见性 */
const dialogVisible = ref(false)
/** 对话框标题 */
const dialogTitle = ref('新增字典数据')
/** 是否为编辑模式 */
const isEdit = ref(false)
/** 表单引用 */
const formRef = ref()
/** 表单数据 */
const form = reactive({
  id: 0,
  dictType: '',
  dictLabel: '',
  dictValue: '',
  dictSort: 0,
  cssClass: '',
  listClass: '',
  isDefault: 0,
  status: 1,
  remark: ''
})

/** 表格回显样式列表 */
const cssClassOptions = [
  { label: '默认', value: '' },
  { label: '主要', value: 'primary' },
  { label: '成功', value: 'success' },
  { label: '信息', value: 'info' },
  { label: '警告', value: 'warning' },
  { label: '危险', value: 'danger' }
]

/**
 * 获取字典数据列表
 */
async function fetchData() {
  loading.value = true
  try {
    const res: any = await dictApi.dictDataPage(
      { current: queryParams.pageNum, size: queryParams.pageSize },
      currentDictType.value
    )
    tableData.value = res.data.records
    total.value = res.data.total
  } catch {
    // 错误由拦截器统一提示
  } finally {
    loading.value = false
  }
}

/** 搜索 */
function handleSearch() {
  queryParams.pageNum = 1
  fetchData()
}

/** 重置 */
function handleReset() {
  queryParams.dictLabel = ''
  queryParams.pageNum = 1
  fetchData()
}

/** 当前页码变化 */
function handlePageChange(page: number) {
  queryParams.pageNum = page
  fetchData()
}

/** 每页条数变化 */
function handleSizeChange(size: number) {
  queryParams.pageSize = size
  queryParams.pageNum = 1
  fetchData()
}

/** 打开新增对话框 */
function openAdd() {
  isEdit.value = false
  dialogTitle.value = '新增字典数据'
  form.id = 0
  form.dictLabel = ''
  form.dictValue = ''
  form.dictSort = 0
  form.cssClass = ''
  form.listClass = ''
  form.isDefault = 0
  form.status = 1
  form.remark = ''
  dialogVisible.value = true
}

/** 打开编辑对话框 */
function openEdit(row: DictData) {
  isEdit.value = true
  dialogTitle.value = '编辑字典数据'
  form.id = row.id
  form.dictLabel = row.dictLabel
  form.dictValue = row.dictValue
  form.dictSort = row.dictSort
  form.cssClass = row.cssClass || ''
  form.listClass = row.listClass || ''
  form.isDefault = row.isDefault
  form.status = row.status
  form.remark = row.remark
  dialogVisible.value = true
}

/** 保存字典数据 */
async function handleSave() {
  try {
    const data = {
      ...form,
      dictType: currentDictType.value
    }
    if (isEdit.value) {
      await dictApi.dictDataUpdate(data)
      ElMessage.success('更新成功')
    } else {
      await dictApi.dictDataAdd(data)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch {
    // 错误由拦截器统一提示
  }
}

/** 删除字典数据 */
async function handleDel(id: number) {
  try {
    await ElMessageBox.confirm('确认删除该字典数据吗？', '提示', { type: 'warning' })
    await dictApi.dictDataDel(id)
    ElMessage.success('删除成功')
    fetchData()
  } catch {
    // 用户取消，不做处理
  }
}

/** 监听路由参数变化 */
watch(
  () => route.query.dictType,
  (val) => {
    currentDictType.value = (val as string) || ''
    form.dictType = currentDictType.value
    queryParams.pageNum = 1
    fetchData()
  },
  { immediate: true }
)

onMounted(() => {
  currentDictType.value = (route.query.dictType as string) || ''
  form.dictType = currentDictType.value
  fetchData()
})
</script>

<template>
  <div>
    <!-- 搜索栏 -->
    <el-card style="margin-bottom: 16px">
      <el-form :model="queryParams" inline>
        <el-form-item label="字典标签">
          <el-input v-model="queryParams.dictLabel" placeholder="字典标签" clearable style="width: 160px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
        <el-form-item style="float: right">
          <el-tag type="info">当前字典：{{ currentDictType || '未选择' }}</el-tag>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card>
      <div style="margin-bottom: 16px">
        <el-button type="primary" v-hasPermission="'system:dict:data:add'" @click="openAdd">新增</el-button>
      </div>

      <el-table
        :data="tableData"
        v-loading="loading"
        border stripe
        style="width: 100%"
      >
        <el-table-column prop="dictLabel" label="字典标签" min-width="120" />
        <el-table-column prop="dictValue" label="字典键值" min-width="120" />
        <el-table-column label="回显样式" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.listClass || ''" size="small">{{ row.dictLabel }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="dictSort" label="排序" width="70" align="center" />
        <el-table-column label="默认" width="70" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.isDefault === 1" type="success" size="small">是</el-tag>
            <el-tag v-else type="info" size="small">否</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="warning" link size="small" v-hasPermission="'system:dict:data:edit'" @click="openEdit(row)">编辑</el-button>
            <el-button type="danger" link size="small" v-hasPermission="'system:dict:data:delete'" @click="handleDel(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div style="margin-top: 16px; display: flex; justify-content: flex-end">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="550px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="form" label-width="100px">
        <el-form-item label="字典标签" required>
          <el-input v-model="form.dictLabel" placeholder="请输入字典标签" />
        </el-form-item>
        <el-form-item label="字典键值" required>
          <el-input v-model="form.dictValue" placeholder="请输入字典键值" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.dictSort" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="回显样式">
          <el-select v-model="form.listClass" placeholder="请选择回显样式" clearable style="width: 100%">
            <el-option v-for="opt in cssClassOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="是否默认">
          <el-radio-group v-model="form.isDefault">
            <el-radio :value="1">是</el-radio>
            <el-radio :value="0">否</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>
