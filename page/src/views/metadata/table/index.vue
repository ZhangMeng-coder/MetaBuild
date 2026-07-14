<script setup lang="ts">
/**
 * 元数据表管理页面
 * 提供元数据表的增删改查功能，支持从数据库导入表结构并配置列展示属性
 */
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import * as metaApi from '@/api/meta'
import * as menuApi from '@/api/menu'
import { ElMessage, ElMessageBox } from 'element-plus'

/** 元数据表信息接口 */
interface MetaTable {
  id: number
  tableName: string
  tableComment: string
  status: number
  createTime: string
}

/** 表格数据 */
const tableData = ref<MetaTable[]>([])
/** 数据总条数 */
const total = ref(0)
/** 表格加载状态 */
const loading = ref(false)

/** 查询参数 */
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10
})

/** 新增对话框可见性 */
const dialogVisible = ref(false)
/** 对话框标题 */
const dialogTitle = ref('新增元数据表')

/** 数据库表列表 */
const dbTables = ref<{ tableName: string; tableComment: string }[]>([])
/** 选中的数据库表名 */
const selectedTable = ref('')
/** 选中表的列信息 */
const dbColumns = ref<any[]>([])
/** 表备注 */
const tableComment = ref('')
/** 加载数据库表列表状态 */
const loadingDbTables = ref(false)
/** 加载列信息状态 */
const loadingColumns = ref(false)

const router = useRouter()

/**
 * 获取元数据表列表
 */
async function fetchData() {
  loading.value = true
  try {
    const res: any = await metaApi.metaTablePage({
      current: queryParams.pageNum,
      size: queryParams.pageSize
    })
    tableData.value = res.data.records
    total.value = res.data.total
  } catch {
    // 错误由拦截器统一提示
  } finally {
    loading.value = false
  }
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
async function openAdd() {
  dialogTitle.value = '新增元数据表'
  selectedTable.value = ''
  tableComment.value = ''
  dbColumns.value = []
  dialogVisible.value = true
  // 加载数据库表列表
  await loadDbTables()
}

/** 加载数据库表列表 */
async function loadDbTables() {
  loadingDbTables.value = true
  try {
    const res: any = await metaApi.getDbTables()
    dbTables.value = res.data
  } catch {
    // 错误由拦截器统一提示
  } finally {
    loadingDbTables.value = false
  }
}

/** 选中数据库表后，查询其列信息 */
async function onTableSelect(tableName: string) {
  if (!tableName) {
    dbColumns.value = []
    tableComment.value = ''
    return
  }
  loadingColumns.value = true
  try {
    const res: any = await metaApi.getDbColumns(tableName)
    dbColumns.value = res.data
    // 自动填充表备注
    const found = dbTables.value.find(t => t.tableName === tableName)
    tableComment.value = found?.tableComment || ''
  } catch {
    // 错误由拦截器统一提示
  } finally {
    loadingColumns.value = false
  }
}

/** 保存元数据表 */
async function handleSave() {
  if (!selectedTable.value) {
    ElMessage.warning('请选择数据库表')
    return
  }
  try {
    await metaApi.metaTableAdd({
      tableName: selectedTable.value,
      tableComment: tableComment.value,
      columns: dbColumns.value
    })
    ElMessage.success('新增成功')
    dialogVisible.value = false
    fetchData()
  } catch {
    // 错误由拦截器统一提示
  }
}

/** 打开编辑对话框 */
async function openEdit(row: MetaTable) {
  try {
    const res: any = await metaApi.metaTableGet(row.id)
    const data = res.data as MetaTable
    tableComment.value = data.tableComment || ''
    // 使用 ElMessageBox 的 prompt 方式编辑
    const { value } = await ElMessageBox.prompt('请输入表备注名称', '编辑元数据表', {
      inputValue: data.tableComment || '',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    await metaApi.metaTableUpdate({
      id: row.id,
      tableComment: value
    })
    ElMessage.success('修改成功')
    fetchData()
  } catch {
    // 用户取消，不做处理
  }
}

/** 删除元数据表 */
async function handleDel(id: number) {
  try {
    await ElMessageBox.confirm('确认删除该元数据表吗？删除后列定义也将一并删除。', '提示', { type: 'warning' })
    await metaApi.metaTableDel(id)
    ElMessage.success('删除成功')
    fetchData()
  } catch {
    // 用户取消，不做处理
  }
}

/** 跳转到列管理页面 */
function goColumnManage(row: MetaTable) {
  router.push({ path: '/metadata/column', query: { tableId: String(row.id), tableName: row.tableName } })
}

/** 跳转到配置页面 */
function goConfig(row: MetaTable) {
  router.push({ path: '/metadata/config', query: { tableId: String(row.id), tableName: row.tableName } })
}

/** 跳转到动态数据管理页面 */
function goDynamic(row: MetaTable) {
  router.push({ path: '/metadata/dynamic', query: { tableId: String(row.id), tableName: row.tableName, tableComment: row.tableComment } })
}

// ==================== 创建菜单 ====================

/** 创建菜单对话框可见性 */
const menuDialogVisible = ref(false)
/** 创建菜单的当前行数据 */
const currentMenuRow = ref<MetaTable | null>(null)
/** 菜单树（用于父级菜单选择） */
const menuTree = ref<any[]>([])
/** 创建菜单表单 */
const menuForm = reactive({
  parentId: 32, // 默认选中"功能配置"目录
  menuName: '',
  path: '',
  component: 'views/metadata/dynamic/index.vue',
  perms: 'server:meta:table:list',
  icon: 'Collection',
  sort: 0,
  status: 1
})

/** 打开创建菜单对话框 */
async function openCreateMenu(row: MetaTable) {
  currentMenuRow.value = row
  menuForm.menuName = row.tableComment || row.tableName
  menuForm.path = '/metadata/dynamic/' + row.id
  menuForm.sort = 0
  try {
    const res: any = await menuApi.tree()
    menuTree.value = res.data || []
  } catch {
    menuTree.value = []
  }
  menuDialogVisible.value = true
}

/** 保存创建菜单（自动生成增删改导出导入权限子菜单） */
async function handleCreateMenu() {
  if (!currentMenuRow.value) return
  const row = currentMenuRow.value
  const tableId = row.id
  try {
    // 1. 创建主菜单
    const res: any = await menuApi.add({
      parentId: menuForm.parentId,
      menuName: menuForm.menuName,
      menuType: 1,
      path: `/metadata/dynamic/${tableId}`,
      component: menuForm.component,
      perms: `meta:dynamic:${tableId}:list`,
      icon: menuForm.icon,
      sort: menuForm.sort,
      status: menuForm.status
    })
    const parentMenuId = res.data

    // 2. 创建子菜单按钮权限
    const children = [
      { menuName: '新增', perms: `meta:dynamic:${tableId}:add`, sort: 1 },
      { menuName: '修改', perms: `meta:dynamic:${tableId}:edit`, sort: 2 },
      { menuName: '删除', perms: `meta:dynamic:${tableId}:delete`, sort: 3 },
      { menuName: '导出', perms: `meta:dynamic:${tableId}:export`, sort: 4 },
      { menuName: '导入', perms: `meta:dynamic:${tableId}:import`, sort: 5 }
    ]
    for (const child of children) {
      await menuApi.add({
        parentId: parentMenuId,
        menuName: child.menuName,
        menuType: 2,
        perms: child.perms,
        sort: child.sort,
        status: 1
      })
    }
    ElMessage.success('菜单创建成功')
    menuDialogVisible.value = false
  } catch {
    // 错误由拦截器统一提示
  }
}

onMounted(() => {
  fetchData()
})
</script>

<template>
  <div>
    <!-- 数据表格 -->
    <el-card>
      <div style="margin-bottom: 16px">
        <el-button type="primary" v-hasPermission="'server:meta:table:add'" @click="openAdd">新增</el-button>
      </div>

      <el-table
        :data="tableData"
        v-loading="loading"
        border stripe
        style="width: 100%"
      >
        <el-table-column prop="tableName" label="表名" min-width="150" />
        <el-table-column prop="tableComment" label="备注名称" min-width="200" show-overflow-tooltip />
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="460" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" v-hasPermission="'server:meta:table:list'" @click="goColumnManage(row)">列管理</el-button>
            <el-button type="success" link size="small" v-hasPermission="'server:meta:table:edit'" @click="goConfig(row)">配置</el-button>
            <el-button type="primary" link size="small" v-hasPermission="'server:meta:table:list'" @click="goDynamic(row)">数据管理</el-button>
            <el-button type="primary" link size="small" v-hasPermission="'system:menu:add'" @click="openCreateMenu(row)">创建菜单</el-button>
            <el-button type="warning" link size="small" v-hasPermission="'server:meta:table:edit'" @click="openEdit(row)">编辑</el-button>
            <el-button type="danger" link size="small" v-hasPermission="'server:meta:table:delete'" @click="handleDel(row.id)">删除</el-button>
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

    <!-- 新增对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px" :close-on-click-modal="false">
      <el-form label-width="100px">
        <el-form-item label="选择数据库表" required>
          <el-select
            v-model="selectedTable"
            placeholder="请选择数据库表"
            style="width: 100%"
            :loading="loadingDbTables"
            filterable
            @change="onTableSelect"
          >
            <el-option
              v-for="item in dbTables"
              :key="item.tableName"
              :label="item.tableName + (item.tableComment ? ' (' + item.tableComment + ')' : '')"
              :value="item.tableName"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="表备注名称">
          <el-input v-model="tableComment" placeholder="请输入表备注名称" />
        </el-form-item>
        <el-form-item label="列信息">
          <el-table
            :data="dbColumns"
            v-loading="loadingColumns"
            border stripe
            style="width: 100%"
            max-height="400"
            size="small"
          >
            <el-table-column prop="columnName" label="列名" min-width="120" />
            <el-table-column prop="columnComment" label="备注" min-width="150" show-overflow-tooltip />
            <el-table-column prop="dataType" label="数据类型" width="100" />
            <el-table-column label="必填" width="60" align="center">
              <template #default="{ row }">
                {{ row.isNullable === 'NO' ? '是' : '否' }}
              </template>
            </el-table-column>
            <el-table-column label="主键" width="60" align="center">
              <template #default="{ row }">
                <el-tag v-if="row.columnKey === 'PRI'" type="danger" size="small">PK</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :disabled="!selectedTable" @click="handleSave">确定</el-button>
      </template>
    </el-dialog>

    <!-- 创建菜单对话框 -->
    <el-dialog v-model="menuDialogVisible" title="创建菜单" width="500px" :close-on-click-modal="false">
      <el-form :model="menuForm" label-width="100px">
        <el-form-item label="菜单名称" required>
          <el-input v-model="menuForm.menuName" placeholder="请输入菜单名称" />
        </el-form-item>
        <el-form-item label="父级目录" required>
          <el-tree-select
            v-model="menuForm.parentId"
            :data="menuTree"
            :props="{ children: 'children', label: 'menuName', value: 'id' }"
            placeholder="请选择父级目录"
            clearable
            check-strictly
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="路由路径">
          <el-input v-model="menuForm.path" disabled />
        </el-form-item>
        <el-form-item label="组件路径">
          <el-input v-model="menuForm.component" disabled />
        </el-form-item>
        <el-form-item label="权限标识">
          <el-input v-model="menuForm.perms" disabled />
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="menuForm.icon" placeholder="请输入图标类名" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="menuForm.sort" :min="0" :max="999" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="menuDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreateMenu">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>