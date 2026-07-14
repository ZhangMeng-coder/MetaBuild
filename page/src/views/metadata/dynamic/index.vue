<script setup lang="ts">
/**
 * 元数据动态数据管理页面
 * 根据元数据配置，动态生成数据列表、搜索、新增/编辑、删除、导出/导入功能
 */
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import * as metaApi from '@/api/meta'
import * as dictApi from '@/api/dict'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()

const tableId = ref(Number(route.query.tableId) || 0)
const tableName = ref(route.query.tableName as string || '')
const tableComment = ref(route.query.tableComment as string || '')

/** 列表配置（可见的列） */
const listColumns = ref<any[]>([])
/** 表单配置 */
const formFields = ref<any[]>([])
/** 元数据列（完整信息，含 show_type, dict_type, dict_sql） */
const metaColumns = ref<any[]>([])

/** 表格数据 */
const tableData = ref<any[]>([])
/** 数据总条数 */
const total = ref(0)
/** 加载状态 */
const loading = ref(false)

/** 搜索参数 */
const searchParams = reactive<Record<string, any>>({})

/** 分页参数 */
const pageParams = reactive({
  current: 1,
  size: 10
})

/** 对话框 */
const dialogVisible = ref(false)
const dialogTitle = ref('新增')
const isEdit = ref(false)
const formData = reactive<Record<string, any>>({})

/** 字典数据缓存 */
const dictCache = reactive<Record<string, any[]>>({})

/** 导入状态 */
const importVisible = ref(false)
const importFile = ref<File | null>(null)
const importing = ref(false)

/** 导出状态 */
const exporting = ref(false)

/** 可搜索的列 */
const searchColumns = computed(() => listColumns.value.filter(c => c.isSearch === 1))

/** 可显示的表列 */
const visibleColumns = computed(() => listColumns.value.filter(c => c.visible === 1))

/** 是否通过菜单路由进入（隐藏配置页面按钮） */
const isFromMenu = computed(() => !!route.params.tableId)

/** 跳转到配置页 */
function goConfig() {
  router.push({ path: '/metadata/config', query: { tableId: String(tableId.value), tableName: tableName.value } })
}

/** 加载配置 */
async function loadConfig() {
  if (!tableId.value) return
  try {
    // 加载元数据列
    const colRes: any = await metaApi.metaColumnList(tableId.value)
    metaColumns.value = colRes.data || []

    // 加载列表配置
    const listRes: any = await metaApi.getListConfig(tableId.value)
    const listData = listRes.data || []
    listColumns.value = listData.map((item: any) => {
      const col = metaColumns.value.find((c: any) => c.id === item.columnId)
      return { ...item, columnName: col?.columnName || '', columnComment: col?.columnComment || '', showType: col?.showType || 'text', dataType: col?.dataType || '', dictType: col?.dictType || '', dictSql: col?.dictSql || '' }
    })

    // 加载表单配置
    const formRes: any = await metaApi.getFormConfig(tableId.value)
    const formData = formRes.data || []
    formFields.value = formData.map((item: any) => {
      const col = metaColumns.value.find((c: any) => c.id === item.columnId)
      return { ...item, columnName: col?.columnName || '', columnComment: col?.columnComment || '', showType: col?.showType || 'text', dataType: col?.dataType || '', dictType: col?.dictType || '', dictSql: col?.dictSql || '' }
    })

    // 预加载字典数据（表单字段 + 搜索列）
    const dictFields = [...formFields.value, ...listColumns.value.filter(c => c.isSearch === 1)]
    for (const field of dictFields) {
      if ((field.showType === 'select' || field.showType === 'radio' || field.showType === 'checkbox') && field.dictType && !dictCache[field.dictType]) {
        try {
          const res: any = await dictApi.getDictDataByType(field.dictType)
          dictCache[field.dictType] = res.data || []
        } catch {
          dictCache[field.dictType] = []
        }
      }
      if ((field.showType === 'select' || field.showType === 'radio' || field.showType === 'checkbox') && field.dictSql && !dictCache[field.dictSql]) {
        try {
          const res: any = await metaApi.executeDictSql(field.dictSql)
          dictCache[field.dictSql] = res.data || []
        } catch {
          dictCache[field.dictSql] = []
        }
      }
    }
  } catch {
    // 错误由拦截器统一提示
  }
}

/** 获取字典选项 */
function getDictOptions(field: any): any[] {
  if (field.dictType && dictCache[field.dictType]) {
    return dictCache[field.dictType].map((d: any) => ({
      value: d.dictValue,
      label: d.dictLabel
    }))
  }
  if (field.dictSql && dictCache[field.dictSql]) {
    return dictCache[field.dictSql].map((d: any) => ({
      value: d.id,
      label: d.name
    }))
  }
  return []
}

/** 获取数据 */
async function fetchData() {
  loading.value = true
  try {
    const params: any = {
      current: pageParams.current,
      size: pageParams.size
    }
    // 添加搜索条件
    const hasSearch = Object.keys(searchParams).length > 0
    if (hasSearch) {
      params.params = { ...searchParams }
    }
    const res: any = await metaApi.dynamicPage(tableId.value, params)
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch {
    // 错误由拦截器统一提示
  } finally {
    loading.value = false
  }
}

/** 搜索 */
function handleSearch() {
  pageParams.current = 1
  fetchData()
}

/** 重置搜索 */
function handleReset() {
  Object.keys(searchParams).forEach(key => { delete searchParams[key] })
  pageParams.current = 1
  fetchData()
}

/** 分页变化 */
function handlePageChange(page: number) {
  pageParams.current = page
  fetchData()
}

/** 每页条数变化 */
function handleSizeChange(size: number) {
  pageParams.size = size
  pageParams.current = 1
  fetchData()
}

/** 打开新增对话框 */
function openAdd() {
  isEdit.value = false
  dialogTitle.value = '新增'
  // 清空表单数据
  Object.keys(formData).forEach(key => { delete formData[key] })
  // 设置默认值
  for (const field of formFields.value) {
    if (field.defaultValue) {
      formData[field.columnName] = field.defaultValue
    }
  }
  dialogVisible.value = true
}

/** 打开编辑对话框 */
async function openEdit(row: any) {
  isEdit.value = true
  dialogTitle.value = '编辑'
  // 加载数据
  try {
    const res: any = await metaApi.dynamicGet(tableId.value, row.id)
    const data = res.data || {}
    Object.keys(formData).forEach(key => { delete formData[key] })
    // 只填充表单配置中的字段
    for (const field of formFields.value) {
      formData[field.columnName] = data[field.columnName] ?? ''
    }
    formData.id = data.id
    dialogVisible.value = true
  } catch {
    // 错误由拦截器统一提示
  }
}

/** 保存 */
async function handleSave() {
  try {
    // 构建提交数据
    const data: Record<string, any> = {}
    for (const field of formFields.value) {
      const val = formData[field.columnName]
      if (val !== undefined && val !== '' && val !== null) {
        data[field.columnName] = val
      }
    }
    if (isEdit.value && formData.id) {
      data.id = formData.id
      await metaApi.dynamicUpdate(tableId.value, data)
      ElMessage.success('修改成功')
    } else {
      await metaApi.dynamicAdd(tableId.value, data)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch {
    // 错误由拦截器统一提示
  }
}

/** 删除 */
async function handleDel(id: number) {
  try {
    await ElMessageBox.confirm('确认删除该数据吗？', '提示', { type: 'warning' })
    await metaApi.dynamicDel(tableId.value, id)
    ElMessage.success('删除成功')
    fetchData()
  } catch {
    // 用户取消，不做处理
  }
}

/** 导出数据 */
async function handleExport() {
  exporting.value = true
  try {
    const res: any = await metaApi.dynamicExport(tableId.value, {})
    const data = res.data || []
    // 构建 CSV
    const headers = visibleColumns.value.map(c => c.columnLabel || c.columnName)
    const csvRows = [headers.join(',')]
    for (const row of data) {
      const values = visibleColumns.value.map(c => {
        let val = row[c.columnName]
        if (val === null || val === undefined) return ''
        // 对字典值做转换
        const field = formFields.value.find(f => f.columnName === c.columnName)
        if (field && (field.showType === 'select' || field.showType === 'radio')) {
          const options = getDictOptions(field)
          const opt = options.find(o => o.value === String(val))
          if (opt) return opt.label
        }
        return String(val).replace(/,/g, '，')
      })
      csvRows.push(values.join(','))
    }
    // 下载
    const blob = new Blob(['\uFEFF' + csvRows.join('\n')], { type: 'text/csv;charset=utf-8;' })
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `${tableName.value}_${Date.now()}.csv`
    a.click()
    URL.revokeObjectURL(url)
    ElMessage.success(`导出成功，共 ${data.length} 条`)
  } catch {
    // 错误由拦截器统一提示
  } finally {
    exporting.value = false
  }
}

/** 选择导入文件 */
function onFileChange(file: File | undefined) {
  importFile.value = file || null
}

/** 导入数据 */
async function handleImport() {
  if (!importFile.value) {
    ElMessage.warning('请选择 CSV 文件')
    return
  }
  importing.value = true
  try {
    const res: any = await metaApi.dynamicImport(tableId.value, importFile.value)
    ElMessage.success(res.msg || '导入成功')
    importVisible.value = false
    importFile.value = null
    fetchData()
  } catch {
    // 错误由拦截器统一提示
  } finally {
    importing.value = false
  }
}

/** 渲染表格单元格值 */
function renderCellValue(row: any, col: any): string {
  const val = row[col.columnName]
  if (val === null || val === undefined) return '-'
  // 从表单配置或列表配置中查找字段信息（含 showType, dictType, dictSql）
  const field = formFields.value.find(f => f.columnName === col.columnName)
    || listColumns.value.find(f => f.columnName === col.columnName)
  if (field && (field.showType === 'select' || field.showType === 'radio')) {
    const options = getDictOptions(field)
    const opt = options.find(o => o.value === String(val))
    if (opt) return opt.label
  }
  if (field?.showType === 'checkbox') {
    const options = getDictOptions(field)
    const vals = String(val).split(',')
    return options.filter(o => vals.includes(String(o.value))).map(o => o.label).join(', ')
  }
  return String(val)
}

onMounted(async () => {
  tableId.value = Number(route.params.tableId) || Number(route.query.tableId) || 0
  tableName.value = route.query.tableName as string || ''
  tableComment.value = route.query.tableComment as string || ''
  if (tableId.value) {
    await loadConfig()
    fetchData()
  }
})

/** 监听路由变化（侧边栏菜单切换时重新加载数据） */
watch(
  () => [route.params.tableId, route.query.tableId],
  async () => {
    const newId = Number(route.params.tableId) || Number(route.query.tableId) || 0
    if (newId && newId !== tableId.value) {
      tableId.value = newId
      tableName.value = route.query.tableName as string || ''
      tableComment.value = route.query.tableComment as string || ''
      // 重置搜索和分页
      pageParams.current = 1
      Object.keys(searchParams).forEach(key => { delete searchParams[key] })
      await loadConfig()
      fetchData()
    }
  }
)
</script>

<template>
  <div>
    <!-- 搜索栏 -->
    <el-card v-if="searchColumns.length > 0" style="margin-bottom: 16px">
      <el-form inline>
        <el-form-item v-for="col in searchColumns" :key="col.columnId" :label="col.columnLabel || col.columnName">
          <!-- 文本框 / 多行文本框 -->
          <el-input
            v-if="col.showType === 'text' || col.showType === 'textarea' || !col.showType"
            v-model="searchParams[col.columnName]"
            :placeholder="'请输入' + (col.columnLabel || col.columnName)"
            clearable
            style="width: 160px"
          />
          <!-- 下拉框（select / radio 在搜索时均使用下拉选择） -->
          <el-select
            v-else-if="col.showType === 'select' || col.showType === 'radio'"
            v-model="searchParams[col.columnName]"
            :placeholder="'请选择' + (col.columnLabel || col.columnName)"
            clearable
            style="width: 160px"
          >
            <el-option v-for="opt in getDictOptions(col)" :key="opt.value" :label="opt.label" :value="opt.value" />
          </el-select>
          <!-- 复选框（搜索时用下拉单选查询包含该值的记录） -->
          <el-select
            v-else-if="col.showType === 'checkbox'"
            v-model="searchParams[col.columnName]"
            :placeholder="'请选择' + (col.columnLabel || col.columnName)"
            clearable
            style="width: 160px"
          >
            <el-option v-for="opt in getDictOptions(col)" :key="opt.value" :label="opt.label" :value="opt.value" />
          </el-select>
          <!-- 日期范围 -->
          <template v-else-if="col.showType === 'date'">
            <el-date-picker
              v-model="searchParams[col.columnName + '_start']"
              type="date"
              placeholder="开始日期"
              value-format="YYYY-MM-DD"
              style="width: 130px"
            />
            <span style="margin: 0 5px">~</span>
            <el-date-picker
              v-model="searchParams[col.columnName + '_end']"
              type="date"
              placeholder="结束日期"
              value-format="YYYY-MM-DD"
              style="width: 130px"
            />
          </template>
          <!-- 时间范围 -->
          <template v-else-if="col.showType === 'time'">
            <el-time-picker
              v-model="searchParams[col.columnName + '_start']"
              placeholder="开始时间"
              value-format="HH:mm:ss"
              style="width: 130px"
            />
            <span style="margin: 0 5px">~</span>
            <el-time-picker
              v-model="searchParams[col.columnName + '_end']"
              placeholder="结束时间"
              value-format="HH:mm:ss"
              style="width: 130px"
            />
          </template>
          <!-- 日期时间范围 -->
          <template v-else-if="col.showType === 'datetime'">
            <el-date-picker
              v-model="searchParams[col.columnName + '_start']"
              type="datetime"
              placeholder="开始日期时间"
              value-format="YYYY-MM-DD HH:mm:ss"
              style="width: 170px"
            />
            <span style="margin: 0 5px">~</span>
            <el-date-picker
              v-model="searchParams[col.columnName + '_end']"
              type="datetime"
              placeholder="结束日期时间"
              value-format="YYYY-MM-DD HH:mm:ss"
              style="width: 170px"
            />
          </template>
          <!-- 兜底：文本框 -->
          <el-input
            v-else
            v-model="searchParams[col.columnName]"
            :placeholder="'请输入' + (col.columnLabel || col.columnName)"
            clearable
            style="width: 160px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card>
      <div style="margin-bottom: 16px; display: flex; align-items: center; justify-content: space-between">
        <div>
          <span style="font-size: 16px; font-weight: bold">{{ tableComment || tableName }}</span>
          <el-button v-if="!isFromMenu" type="warning" size="small" style="margin-left: 8px" v-hasPermission="'server:meta:table:edit'" @click="goConfig">配置页面</el-button>
          <el-button type="primary" style="margin-left: 8px" v-hasPermission="'meta:dynamic:' + tableId + ':add'" @click="openAdd">新增</el-button>
        </div>
        <div>
          <el-button :loading="exporting" v-hasPermission="'meta:dynamic:' + tableId + ':export'" @click="handleExport">导出</el-button>
          <el-button v-hasPermission="'meta:dynamic:' + tableId + ':import'" @click="importVisible = true">导入</el-button>
        </div>
      </div>

      <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
        <el-table-column v-for="col in visibleColumns" :key="col.columnId" :label="col.columnLabel || col.columnName" :width="col.width || undefined" :align="col.align || 'left'" show-overflow-tooltip>
          <template #default="{ row }">
            {{ renderCellValue(row, col) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" v-hasPermission="'meta:dynamic:' + tableId + ':edit'" @click="openEdit(row)">编辑</el-button>
            <el-button type="danger" link size="small" v-hasPermission="'meta:dynamic:' + tableId + ':delete'" @click="handleDel(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div style="margin-top: 16px; display: flex; justify-content: flex-end">
        <el-pagination
          v-model:current-page="pageParams.current"
          v-model:page-size="pageParams.size"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px" :close-on-click-modal="false">
      <el-form :model="formData" label-width="100px">
        <el-row>
          <el-col v-for="field in formFields" :key="field.columnId" :span="field.span || 12">
            <el-form-item :label="field.fieldLabel || field.columnComment || field.columnName">
              <!-- 文本框 -->
              <el-input
                v-if="field.showType === 'text'"
                v-model="formData[field.columnName]"
                :placeholder="field.placeholder || '请输入'"
                style="width: 90%"
              />
              <!-- 多行文本框 -->
              <el-input
                v-else-if="field.showType === 'textarea'"
                v-model="formData[field.columnName]"
                type="textarea"
                :rows="4"
                :placeholder="field.placeholder || '请输入'"
                style="width: 90%"
              />
              <!-- 下拉框 -->
              <el-select
                v-else-if="field.showType === 'select'"
                v-model="formData[field.columnName]"
                :placeholder="field.placeholder || '请选择'"
                style="width: 90%"
                clearable
                filterable
              >
                <el-option v-for="opt in getDictOptions(field)" :key="opt.value" :label="opt.label" :value="opt.value" />
              </el-select>
              <!-- 单选框 -->
              <el-radio-group v-else-if="field.showType === 'radio'" v-model="formData[field.columnName]">
                <el-radio v-for="opt in getDictOptions(field)" :key="opt.value" :value="opt.value">
                  {{ opt.label }}
                </el-radio>
              </el-radio-group>
              <!-- 复选框 -->
              <el-checkbox-group v-else-if="field.showType === 'checkbox'" v-model="formData[field.columnName]">
                <el-checkbox v-for="opt in getDictOptions(field)" :key="opt.value" :value="opt.value" :label="opt.label" />
              </el-checkbox-group>
              <!-- 日期选择器 -->
              <el-date-picker
                v-else-if="field.showType === 'date'"
                v-model="formData[field.columnName]"
                type="date"
                :placeholder="field.placeholder || '选择日期'"
                style="width: 90%"
                value-format="YYYY-MM-DD"
              />
              <!-- 时间选择器 -->
              <el-time-picker
                v-else-if="field.showType === 'time'"
                v-model="formData[field.columnName]"
                :placeholder="field.placeholder || '选择时间'"
                style="width: 90%"
                value-format="HH:mm:ss"
              />
              <!-- 日期时间选择器 -->
              <el-date-picker
                v-else-if="field.showType === 'datetime'"
                v-model="formData[field.columnName]"
                type="datetime"
                :placeholder="field.placeholder || '选择日期时间'"
                style="width: 90%"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
              <!-- 默认文本框 -->
              <el-input
                v-else
                v-model="formData[field.columnName]"
                :placeholder="field.placeholder || '请输入'"
                style="width: 90%"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">确定</el-button>
      </template>
    </el-dialog>

    <!-- 导入对话框 -->
    <el-dialog v-model="importVisible" title="导入数据" width="400px" :close-on-click-modal="false">
      <el-upload
        drag
        accept=".csv"
        :auto-upload="false"
        :limit="1"
        :on-change="(file: any) => onFileChange(file.raw)"
        :on-remove="() => onFileChange(undefined)"
      >
        <el-icon style="font-size: 48px; color: #909399"><UploadFilled /></el-icon>
        <div>将 CSV 文件拖到此处，或<em>点击选择</em></div>
        <template #tip>
          <div style="font-size: 12px; color: #909399; margin-top: 8px">
            仅支持 .csv 格式文件，第一行为列名
          </div>
        </template>
      </el-upload>
      <template #footer>
        <el-button @click="importVisible = false">取消</el-button>
        <el-button type="primary" :loading="importing" @click="handleImport">导入</el-button>
      </template>
    </el-dialog>
  </div>
</template>