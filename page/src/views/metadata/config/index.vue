<script setup lang="ts">
/**
 * 元数据拖拽配置页面
 * 通过拖拽方式配置列表展示列和新增/编辑表单字段
 * 包含"列表配置"和"表单配置"两个标签页
 */
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import PageHeader from '@/components/PageHeader.vue'
import * as metaApi from '@/api/meta'
import { ElMessage } from 'element-plus'

/** 展示类型选项映射 */
const showTypeMap: Record<string, string> = {
  text: '文本框',
  textarea: '多行文本框',
  select: '下拉框',
  radio: '单选框',
  checkbox: '复选框',
  date: '日期选择器',
  time: '时间选择器',
  datetime: '日期时间选择器'
}

/** 对齐方式选项 */
const alignOptions = [
  { value: 'left', label: '左对齐' },
  { value: 'center', label: '居中' },
  { value: 'right', label: '右对齐' }
]

/** 列配置项接口 */
interface ListConfigItem {
  id: number
  columnId: number
  columnName: string
  columnComment: string
  dataType: string
  showType: string
  columnLabel: string
  width: number | null
  align: string
  sortable: number
  visible: number
  frozen: string | null
  isSearch: number
  sort: number
}

/** 表单配置项接口 */
interface FormConfigItem {
  id: number
  columnId: number
  columnName: string
  columnComment: string
  dataType: string
  showType: string
  fieldLabel: string
  isRequired: number | null
  span: number
  placeholder: string
  defaultValue: string
  sort: number
}

const route = useRoute()

const tableId = ref(Number(route.query.tableId) || 0)
const tableName = ref(route.query.tableName as string || '')
const activeTab = ref('list')

/** 列表配置数据 */
const listConfig = ref<ListConfigItem[]>([])
/** 表单配置数据 */
const formConfig = ref<FormConfigItem[]>([])

const loading = ref(false)
const saving = ref(false)

/** 拖拽相关状态 */
const dragIndex = ref(-1)
const dragType = ref('') // 'list' or 'form'

/** 加载配置数据 */
async function loadConfig() {
  if (!tableId.value) return
  loading.value = true
  try {
    // 加载元数据列（用于获取 columnName 等补充信息）
    const colRes: any = await metaApi.metaColumnList(tableId.value)
    const columns = colRes.data || []

    // 加载列表配置
    const listRes: any = await metaApi.getListConfig(tableId.value)
    const listData = listRes.data || []
    // 合并列信息
    listConfig.value = listData.map((item: any) => {
      const col = columns.find((c: any) => c.id === item.columnId)
      return {
        ...item,
        columnName: col?.columnName || '',
        columnComment: col?.columnComment || '',
        dataType: col?.dataType || '',
        showType: col?.showType || 'text'
      }
    })

    // 加载表单配置
    const formRes: any = await metaApi.getFormConfig(tableId.value)
    const formData = formRes.data || []
    formConfig.value = formData.map((item: any) => {
      const col = columns.find((c: any) => c.id === item.columnId)
      return {
        ...item,
        columnName: col?.columnName || '',
        columnComment: col?.columnComment || '',
        dataType: col?.dataType || '',
        showType: col?.showType || 'text'
      }
    })
  } catch {
    // 错误由拦截器统一提示
  } finally {
    loading.value = false
  }
}

// ==================== 拖拽逻辑 ====================

/** 拖拽开始 */
function onDragStart(event: DragEvent, index: number, type: string) {
  dragIndex.value = index
  dragType.value = type
  if (event.dataTransfer) {
    event.dataTransfer.effectAllowed = 'move'
  }
}

/** 拖拽经过 */
function onDragOver(event: DragEvent, index: number) {
  event.preventDefault()
  if (dragIndex.value === -1 || dragIndex.value === index) return
  if (dragType.value === 'list') {
    const item = listConfig.value.splice(dragIndex.value, 1)[0]
    listConfig.value.splice(index, 0, item)
  } else {
    const item = formConfig.value.splice(dragIndex.value, 1)[0]
    formConfig.value.splice(index, 0, item)
  }
  dragIndex.value = index
}

/** 拖拽结束 */
function onDragEnd() {
  dragIndex.value = -1
  dragType.value = ''
  // 更新排序号
  updateSort()
}

/** 更新排序号 */
function updateSort() {
  if (dragType.value === 'list') {
    listConfig.value.forEach((item, idx) => { item.sort = idx })
  } else {
    formConfig.value.forEach((item, idx) => { item.sort = idx })
  }
}

/** 切换表单字段必填 */
function toggleRequired(item: FormConfigItem) {
  if (item.isRequired === null) {
    item.isRequired = 1
  } else if (item.isRequired === 1) {
    item.isRequired = 0
  } else {
    item.isRequired = null
  }
}

// ==================== 保存 ====================

/** 保存列表配置 */
async function saveListConfig() {
  saving.value = true
  try {
    const data = listConfig.value.map((item, idx) => ({
      id: item.id,
      columnId: item.columnId,
      columnLabel: item.columnLabel,
      width: item.width,
      align: item.align,
      sortable: item.sortable,
      visible: item.visible,
      frozen: item.frozen || null,
      isSearch: item.isSearch,
      sort: idx
    }))
    await metaApi.saveListConfig(tableId.value, data)
    ElMessage.success('列表配置保存成功')
  } catch {
    // 错误由拦截器统一提示
  } finally {
    saving.value = false
  }
}

/** 保存表单配置 */
async function saveFormConfig() {
  saving.value = true
  try {
    const data = formConfig.value.map((item, idx) => ({
      id: item.id,
      columnId: item.columnId,
      fieldLabel: item.fieldLabel,
      isRequired: item.isRequired,
      span: item.span,
      placeholder: item.placeholder,
      defaultValue: item.defaultValue,
      sort: idx
    }))
    await metaApi.saveFormConfig(tableId.value, data)
    ElMessage.success('表单配置保存成功')
  } catch {
    // 错误由拦截器统一提示
  } finally {
    saving.value = false
  }
}

/** 保存当前标签页 */
function handleSave() {
  if (activeTab.value === 'list') {
    saveListConfig()
  } else {
    saveFormConfig()
  }
}

onMounted(() => {
  loadConfig()
})
</script>

<template>
  <div>
    <!-- 顶部导航 -->
    <el-card style="margin-bottom: 16px">
      <PageHeader :title="'配置 - ' + tableName" backPath="/metadata/table">
        <template #actions>
          <el-button type="primary" :loading="saving" @click="handleSave">保存配置</el-button>
        </template>
      </PageHeader>
    </el-card>

    <!-- 标签页 -->
    <el-card v-loading="loading">
      <el-tabs v-model="activeTab">
        <!-- 列表配置 -->
        <el-tab-pane label="列表配置" name="list">
          <div style="margin-bottom: 12px; color: #909399; font-size: 13px">
            拖拽行可调整列顺序。点击"可见"开关控制是否在表格中显示，点击"搜索"开关控制是否作为搜索条件。
          </div>
          <el-table :data="listConfig" border stripe size="small">
            <el-table-column label="排序" width="50" align="center">
              <template #default="{ $index }">
                <span
                  draggable="true"
                  @dragstart="onDragStart($event, $index, 'list')"
                  @dragover="onDragOver($event, $index)"
                  @dragend="onDragEnd"
                  style="cursor: grab; font-size: 18px; user-select: none"
                >⠿</span>
              </template>
            </el-table-column>
            <el-table-column label="列名" prop="columnName" width="130" />
            <el-table-column label="列备注" prop="columnComment" width="150" show-overflow-tooltip />
            <el-table-column label="数据类型" prop="dataType" width="90" />
            <el-table-column label="展示类型" width="110">
              <template #default="{ row }">
                {{ showTypeMap[row.showType] || row.showType }}
              </template>
            </el-table-column>
            <el-table-column label="显示名称" width="150">
              <template #default="{ row }">
                <el-input v-model="row.columnLabel" size="small" placeholder="默认使用列备注" />
              </template>
            </el-table-column>
            <el-table-column label="宽度" width="80">
              <template #default="{ row }">
                <el-input-number v-model="row.width" :min="0" :max="500" size="small" style="width: 70px" placeholder="auto" />
              </template>
            </el-table-column>
            <el-table-column label="对齐" width="90">
              <template #default="{ row }">
                <el-select v-model="row.align" size="small">
                  <el-option v-for="opt in alignOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="可见" width="60" align="center">
              <template #default="{ row }">
                <el-switch v-model="row.visible" :active-value="1" :inactive-value="0" size="small" />
              </template>
            </el-table-column>
            <el-table-column label="搜索" width="60" align="center">
              <template #default="{ row }">
                <el-switch v-model="row.isSearch" :active-value="1" :inactive-value="0" size="small" />
              </template>
            </el-table-column>
          </el-table>
          <div v-if="listConfig.length === 0" style="text-align: center; padding: 40px; color: #999">
            暂无配置数据
          </div>
        </el-tab-pane>

        <!-- 表单配置 -->
        <el-tab-pane label="表单配置" name="form">
          <div style="margin-bottom: 12px; color: #909399; font-size: 13px">
            拖拽行可调整字段顺序。配置新增/编辑对话框中各字段的显示方式、布局宽度和默认值。
          </div>
          <el-table :data="formConfig" border stripe size="small">
            <el-table-column label="排序" width="50" align="center">
              <template #default="{ $index }">
                <span
                  draggable="true"
                  @dragstart="onDragStart($event, $index, 'form')"
                  @dragover="onDragOver($event, $index)"
                  @dragend="onDragEnd"
                  style="cursor: grab; font-size: 18px; user-select: none"
                >⠿</span>
              </template>
            </el-table-column>
            <el-table-column label="列名" prop="columnName" width="130" />
            <el-table-column label="列备注" prop="columnComment" width="150" show-overflow-tooltip />
            <el-table-column label="数据类型" prop="dataType" width="80" />
            <el-table-column label="展示类型" width="100">
              <template #default="{ row }">
                {{ showTypeMap[row.showType] || row.showType }}
              </template>
            </el-table-column>
            <el-table-column label="字段名称" width="150">
              <template #default="{ row }">
                <el-input v-model="row.fieldLabel" size="small" placeholder="默认使用列备注" />
              </template>
            </el-table-column>
            <el-table-column label="必填" width="80" align="center">
              <template #default="{ row }">
                <el-switch
                  :model-value="row.isRequired"
                  :active-value="1"
                  :inactive-value="0"
                  :indeterminate-value="null"
                  size="small"
                  @click.prevent="toggleRequired(row)"
                />
                <div style="font-size: 10px; color: #999">
                  {{ row.isRequired === null ? '默认' : row.isRequired === 1 ? '必填' : '可选' }}
                </div>
              </template>
            </el-table-column>
            <el-table-column label="栅格宽度" width="100">
              <template #default="{ row }">
                <el-select v-model="row.span" size="small">
                  <el-option :value="24" label="整行(24)" />
                  <el-option :value="12" label="半行(12)" />
                  <el-option :value="8" label="1/3行(8)" />
                  <el-option :value="6" label="1/4行(6)" />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="占位提示" width="150">
              <template #default="{ row }">
                <el-input v-model="row.placeholder" size="small" placeholder="请输入" />
              </template>
            </el-table-column>
            <el-table-column label="默认值" width="130">
              <template #default="{ row }">
                <el-input v-model="row.defaultValue" size="small" placeholder="无" />
              </template>
            </el-table-column>
          </el-table>
          <div v-if="formConfig.length === 0" style="text-align: center; padding: 40px; color: #999">
            暂无配置数据
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>