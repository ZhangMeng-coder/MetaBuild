<script setup lang="ts">
/**
 * 元数据列管理页面
 * 展示指定元数据表的所有列，支持编辑列展示属性（展示类型、字典项等）
 * 从元数据表列表页面的"列管理"按钮进入
 */
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import PageHeader from '@/components/PageHeader.vue'
import * as metaApi from '@/api/meta'
import * as dictApi from '@/api/dict'
import { ElMessage } from 'element-plus'

/** 元数据列信息接口 */
interface MetaColumn {
  id: number
  tableId: number
  columnName: string
  columnComment: string
  dataType: string
  isRequired: number
  isPk: number
  showType: string
  dictType: string
  dictSql: string
  sort: number
  status: number
  remark: string
}

/** 展示类型选项 */
const showTypeOptions = [
  { value: 'text', label: '文本框' },
  { value: 'textarea', label: '多行文本框' },
  { value: 'select', label: '下拉框' },
  { value: 'radio', label: '单选框' },
  { value: 'checkbox', label: '复选框' },
  { value: 'date', label: '日期选择器' },
  { value: 'time', label: '时间选择器' },
  { value: 'datetime', label: '日期时间选择器' }
]

/** 列数据 */
const columns = ref<MetaColumn[]>([])
/** 表格加载状态 */
const loading = ref(false)
/** 表名 */
const tableName = ref('')
/** 表ID */
const tableId = ref(0)
/** 编辑中的列ID */
const editingId = ref<number | null>(null)
/** 编辑中的表单数据 */
const editForm = ref<Partial<MetaColumn>>({})
/** 字典类型列表（用于下拉选择） */
const dictTypeList = ref<{ dictName: string; dictType: string }[]>([])

const route = useRoute()

/** 预览字典数据 */
const previewDialogVisible = ref(false)
const previewData = ref<any[]>([])
const previewLoading = ref(false)

/**
 * 加载列数据
 */
async function fetchColumns() {
  loading.value = true
  try {
    const res: any = await metaApi.metaColumnList(tableId.value)
    columns.value = res.data
  } catch {
    // 错误由拦截器统一提示
  } finally {
    loading.value = false
  }
}

/**
 * 加载字典类型列表
 */
async function loadDictTypes() {
  try {
    const res: any = await dictApi.dictTypePage({ current: 1, size: 999 })
    dictTypeList.value = res.data.records?.map((r: any) => ({
      dictName: r.dictName,
      dictType: r.dictType
    })) || []
  } catch {
    // 忽略错误
  }
}

/** 开始编辑 */
function startEdit(column: MetaColumn) {
  editingId.value = column.id
  editForm.value = { ...column }
}

/** 取消编辑 */
function cancelEdit() {
  editingId.value = null
  editForm.value = {}
}

/** 保存编辑 */
async function saveEdit() {
  if (!editForm.value.id) return
  try {
    await metaApi.metaColumnUpdate(editForm.value)
    ElMessage.success('保存成功')
    editingId.value = null
    fetchColumns()
  } catch {
    // 错误由拦截器统一提示
  }
}

/** 预览字典数据 */
async function previewDict(column: MetaColumn) {
  previewDialogVisible.value = true
  previewLoading.value = true
  previewData.value = []
  try {
    if (column.dictType) {
      const res: any = await dictApi.getDictDataByType(column.dictType)
      previewData.value = res.data || []
    } else if (column.dictSql) {
      const res: any = await metaApi.executeDictSql(column.dictSql)
      previewData.value = res.data || []
    }
  } catch {
    // 错误由拦截器统一提示
  } finally {
    previewLoading.value = false
  }
}

onMounted(() => {
  tableId.value = Number(route.query.tableId) || 0
  tableName.value = (route.query.tableName as string) || ''
  if (tableId.value > 0) {
    fetchColumns()
    loadDictTypes()
  }
})
</script>

<template>
  <div>
    <el-card>
      <PageHeader :title="'表名：' + tableName" backPath="/metadata/table" />
      <el-table
        :data="columns"
        v-loading="loading"
        border stripe
        style="width: 100%"
      >
        <el-table-column prop="columnName" label="列名" width="130" />
        <el-table-column prop="columnComment" label="备注" width="150" show-overflow-tooltip />
        <el-table-column prop="dataType" label="数据类型" width="100" />
        <el-table-column label="必填" width="60" align="center">
          <template #default="{ row }">
            {{ row.isRequired === 1 ? '是' : '否' }}
          </template>
        </el-table-column>
        <el-table-column label="主键" width="60" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.isPk === 1" type="danger" size="small">PK</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="展示类型" width="150">
          <template #default="{ row }">
            <template v-if="editingId === row.id">
              <el-select v-model="editForm.showType" style="width: 130px">
                <el-option
                  v-for="opt in showTypeOptions"
                  :key="opt.value"
                  :label="opt.label"
                  :value="opt.value"
                />
              </el-select>
            </template>
            <template v-else>
              {{ showTypeOptions.find(o => o.value === row.showType)?.label || row.showType }}
            </template>
          </template>
        </el-table-column>
        <el-table-column label="字典类型" width="150">
          <template #default="{ row }">
            <template v-if="editingId === row.id && (editForm.showType === 'select' || editForm.showType === 'radio' || editForm.showType === 'checkbox')">
              <el-select v-model="editForm.dictType" style="width: 130px" clearable filterable placeholder="选择字典">
                <el-option
                  v-for="item in dictTypeList"
                  :key="item.dictType"
                  :label="item.dictName"
                  :value="item.dictType"
                />
              </el-select>
            </template>
            <template v-else>
              {{ row.dictType || '-' }}
            </template>
          </template>
        </el-table-column>
        <el-table-column label="字典SQL" min-width="200">
          <template #default="{ row }">
            <template v-if="editingId === row.id && (editForm.showType === 'select' || editForm.showType === 'radio' || editForm.showType === 'checkbox')">
              <el-input
                v-model="editForm.dictSql"
                placeholder="SELECT id, name FROM ..."
                size="small"
                style="width: 100%"
              />
            </template>
            <template v-else>
              <span style="font-size: 12px; color: #999">{{ row.dictSql || '-' }}</span>
            </template>
          </template>
        </el-table-column>
        <el-table-column label="排序" width="70" align="center">
          <template #default="{ row }">
            <template v-if="editingId === row.id">
              <el-input-number v-model="editForm.sort" :min="0" :max="999" size="small" style="width: 70px" />
            </template>
            <template v-else>
              {{ row.sort }}
            </template>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <template v-if="editingId === row.id">
              <el-button type="primary" link size="small" @click="saveEdit">保存</el-button>
              <el-button link size="small" @click="cancelEdit">取消</el-button>
            </template>
            <template v-else>
              <el-button type="primary" link size="small" v-hasPermission="'server:meta:table:edit'" @click="startEdit(row)">编辑</el-button>
              <el-button
                v-if="row.showType === 'select' || row.showType === 'radio' || row.showType === 'checkbox'"
                type="info" link size="small"
                @click="previewDict(row)"
              >预览字典</el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="columns.length === 0 && !loading" style="text-align: center; padding: 40px; color: #999">
        暂无列数据
      </div>
    </el-card>

    <!-- 字典数据预览对话框 -->
    <el-dialog v-model="previewDialogVisible" title="字典数据预览" width="500px">
      <el-table :data="previewData" v-loading="previewLoading" border stripe size="small">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="名称" min-width="150" />
      </el-table>
      <template #footer>
        <el-button @click="previewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>