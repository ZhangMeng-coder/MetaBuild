<script setup lang="ts">
/**
 * 字典类型管理页面
 * 提供字典类型的增删改查功能，以表格形式展示
 * 通过"数据"按钮可跳转到对应字典类型的数据管理
 */
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import * as dictApi from '@/api/dict'
import { ElMessage, ElMessageBox } from 'element-plus'

/** 字典类型信息接口 */
interface DictType {
  id: number
  dictName: string
  dictType: string
  status: number
  remark: string
  sort: number
  createTime: string
}

/** 表格数据 */
const tableData = ref<DictType[]>([])
/** 数据总条数（分页用） */
const total = ref(0)
/** 表格加载状态 */
const loading = ref(false)

/** 查询参数 */
const queryParams = reactive({
  dictName: '',
  dictType: '',
  pageNum: 1,
  pageSize: 10
})

/** 新增/编辑对话框可见性 */
const dialogVisible = ref(false)
/** 对话框标题 */
const dialogTitle = ref('新增字典类型')
/** 是否为编辑模式 */
const isEdit = ref(false)
/** 表单引用 */
const formRef = ref()
/** 表单数据 */
const form = reactive({
  id: 0,
  dictName: '',
  dictType: '',
  status: 1,
  remark: '',
  sort: 0
})

const router = useRouter()

/**
 * 获取字典类型列表
 */
async function fetchData() {
  loading.value = true
  try {
    const res: any = await dictApi.dictTypePage({
      current: queryParams.pageNum,
      size: queryParams.pageSize,
      dictName: queryParams.dictName || undefined,
      dictType: queryParams.dictType || undefined
    })
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
  queryParams.dictName = ''
  queryParams.dictType = ''
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
  dialogTitle.value = '新增字典类型'
  form.id = 0
  form.dictName = ''
  form.dictType = ''
  form.status = 1
  form.remark = ''
  form.sort = 0
  dialogVisible.value = true
}

/** 打开编辑对话框 */
function openEdit(row: DictType) {
  isEdit.value = true
  dialogTitle.value = '编辑字典类型'
  form.id = row.id
  form.dictName = row.dictName
  form.dictType = row.dictType
  form.status = row.status
  form.remark = row.remark
  form.sort = row.sort
  dialogVisible.value = true
}

/** 保存字典类型 */
async function handleSave() {
  try {
    if (isEdit.value) {
      await dictApi.dictTypeUpdate({
        id: form.id,
        dictName: form.dictName,
        dictType: form.dictType,
        status: form.status,
        remark: form.remark,
        sort: form.sort
      })
      ElMessage.success('更新成功')
    } else {
      await dictApi.dictTypeAdd({
        dictName: form.dictName,
        dictType: form.dictType,
        status: form.status,
        remark: form.remark,
        sort: form.sort
      })
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch {
    // 错误由拦截器统一提示
  }
}

/** 删除字典类型 */
async function handleDel(id: number) {
  try {
    await ElMessageBox.confirm('确认删除该字典类型吗？', '提示', { type: 'warning' })
    await dictApi.dictTypeDel(id)
    ElMessage.success('删除成功')
    fetchData()
  } catch {
    // 用户取消，不做处理
  }
}

/** 跳转到字典数据管理 */
function goDictData(row: DictType) {
  router.push({ path: '/system/dict/data', query: { dictType: row.dictType } })
}

onMounted(() => {
  fetchData()
})
</script>

<template>
  <div>
    <!-- 搜索栏 -->
    <el-card style="margin-bottom: 16px">
      <el-form :model="queryParams" inline>
        <el-form-item label="字典名称">
          <el-input v-model="queryParams.dictName" placeholder="字典名称" clearable style="width: 160px" />
        </el-form-item>
        <el-form-item label="字典类型">
          <el-input v-model="queryParams.dictType" placeholder="字典类型" clearable style="width: 160px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card>
      <div style="margin-bottom: 16px">
        <el-button type="primary" v-hasPermission="'system:dict:add'" @click="openAdd">新增</el-button>
      </div>

      <el-table
        :data="tableData"
        v-loading="loading"
        border stripe
        style="width: 100%"
      >
        <el-table-column prop="dictName" label="字典名称" min-width="150" />
        <el-table-column prop="dictType" label="字典类型" min-width="150" />
        <el-table-column prop="sort" label="排序" width="70" align="center" />
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="200" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" v-hasPermission="'system:dict:list'" @click="goDictData(row)">数据</el-button>
            <el-button type="warning" link size="small" v-hasPermission="'system:dict:edit'" @click="openEdit(row)">编辑</el-button>
            <el-button type="danger" link size="small" v-hasPermission="'system:dict:delete'" @click="handleDel(row.id)">删除</el-button>
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
        <el-form-item label="字典名称" required>
          <el-input v-model="form.dictName" placeholder="请输入字典名称" />
        </el-form-item>
        <el-form-item label="字典类型" required>
          <el-input v-model="form.dictType" placeholder="请输入字典类型" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" :max="999" />
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
