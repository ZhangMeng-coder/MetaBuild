<script setup lang="ts">
/**
 * 菜单管理页面
 * 提供菜单的增删改查功能，以树形表格展示菜单层级结构
 * 包含操作栏、树形表格、新增/编辑对话框
 */
import { ref, reactive, onMounted } from 'vue'
import * as menuApi from '@/api/menu'

/** 菜单信息接口 */
interface Menu {
  id: number           // 菜单 ID
  parentId: number     // 父级菜单 ID
  menuName: string     // 菜单名称
  menuType: number     // 菜单类型：0 目录，1 菜单，2 按钮
  path: string         // 路由路径
  component: string    // 组件路径
  perms: string        // 权限标识
  icon: string         // 图标类名
  sort: number         // 排序号
  status: number       // 状态：1 启用，0 停用
  children?: Menu[]    // 子菜单列表
}

/** 树形表格数据 */
const tableData = ref<Menu[]>([])
/** 表格加载状态 */
const loading = ref(false)

/** 新增/编辑对话框可见性 */
const dialogVisible = ref(false)
/** 新增/编辑对话框标题 */
const dialogTitle = ref('新增菜单')
/** 表单引用 */
const formRef = ref()
/** 表单数据 */
const form = reactive({
  id: 0,            // 菜单 ID（编辑时使用）
  parentId: 0,      // 父级菜单 ID
  menuName: '',     // 菜单名称
  menuType: 1,      // 菜单类型，默认菜单
  path: '',         // 路由路径
  component: '',    // 组件路径
  perms: '',        // 权限标识
  icon: '',         // 图标类名
  sort: 0,          // 排序号
  status: 1         // 状态，默认启用
})
/** 是否为编辑模式 */
const isEdit = ref(false)

/** 父级菜单树（用于对话框中的树形选择器） */
const menuTree = ref<any[]>([])

/**
 * 获取菜单树数据
 * 加载树形结构的菜单列表用于表格展示
 */
async function fetchData() {
  loading.value = true
  try {
    const res: any = await menuApi.tree()
    tableData.value = res.data || []
  } catch {
    // 错误由拦截器统一提示
  } finally {
    loading.value = false
  }
}

/**
 * 加载菜单树（用于对话框中的父级菜单选择器）
 */
async function loadMenuTree() {
  try {
    const res: any = await menuApi.tree()
    menuTree.value = res.data || []
  } catch {
    menuTree.value = []
  }
}

/**
 * 打开新增菜单对话框
 * @param parentId - 父级菜单 ID，默认为 0（顶级菜单）
 */
function openAdd(parentId = 0) {
  isEdit.value = false
  dialogTitle.value = parentId ? '新增子菜单' : '新增菜单'
  form.id = 0
  form.parentId = parentId
  form.menuName = ''
  form.menuType = 1
  form.path = ''
  form.component = ''
  form.perms = ''
  form.icon = ''
  form.sort = 0
  form.status = 1
  dialogVisible.value = true
  loadMenuTree()  // 加载父级菜单树选择器数据
}

/**
 * 打开编辑菜单对话框
 * 将选中行的数据回填到表单
 * @param row - 选中的菜单数据
 */
function openEdit(row: Menu) {
  isEdit.value = true
  dialogTitle.value = '编辑菜单'
  form.id = row.id
  form.parentId = row.parentId
  form.menuName = row.menuName
  form.menuType = row.menuType
  form.path = row.path
  form.component = row.component
  form.perms = row.perms
  form.icon = row.icon
  form.sort = row.sort
  form.status = row.status
  dialogVisible.value = true
  loadMenuTree()  // 加载父级菜单树选择器数据
}

/**
 * 保存菜单（新增或编辑）
 * 根据 isEdit 状态判断调用新增或更新接口
 */
async function handleSave() {
  try {
    if (isEdit.value) {
      // 编辑模式：更新菜单信息
      await menuApi.update({
        id: form.id,
        parentId: form.parentId,
        menuName: form.menuName,
        menuType: form.menuType,
        path: form.path,
        component: form.component,
        perms: form.perms,
        icon: form.icon,
        sort: form.sort,
        status: form.status
      })
      ElMessage.success('更新成功')
    } else {
      // 新增模式：创建新菜单
      await menuApi.add({
        parentId: form.parentId,
        menuName: form.menuName,
        menuType: form.menuType,
        path: form.path,
        component: form.component,
        perms: form.perms,
        icon: form.icon,
        sort: form.sort,
        status: form.status
      })
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchData()  // 刷新树形表格
  } catch {
    // 错误由拦截器统一提示
  }
}

/**
 * 删除菜单
 * 弹出确认框，确认后调用删除接口
 * @param id - 菜单 ID
 */
async function handleDel(id: number) {
  try {
    await ElMessageBox.confirm('确认删除该菜单吗？', '提示', { type: 'warning' })
    await menuApi.del(id)
    ElMessage.success('删除成功')
    fetchData()
  } catch {
    // 用户取消操作，不做处理
  }
}

/**
 * 递归查找父级菜单名称
 * 用于在表格中显示父级菜单的名称（目前未在模板中使用，保留供扩展）
 * @param tree - 菜单树数据
 * @param id - 要查找的菜单 ID
 * @returns 菜单名称，未找到则返回"顶级菜单"
 */
function findParentName(tree: any[], id: number): string {
  for (const item of tree) {
    if (item.id === id) return item.menuName
    if (item.children) {
      const name = findParentName(item.children, id)
      if (name) return name
    }
  }
  return '顶级菜单'
}

onMounted(() => {
  fetchData()  // 页面加载时获取菜单树
})
</script>

<template>
  <div>
    <!-- 操作栏 -->
    <el-card>
      <div style="margin-bottom: 16px">
        <el-button type="primary" @click="openAdd(0)">新增</el-button>
      </div>

      <!-- 树形表格 -->
      <el-table
        :data="tableData"
        v-loading="loading"
        border stripe
        style="width: 100%"
        row-key="id"
        :tree-props="{ children: 'children' }"
        default-expand-all
      >
        <el-table-column prop="menuName" label="菜单名称" min-width="180" />
        <el-table-column label="类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.menuType === 0" type="" size="small">目录</el-tag>
            <el-tag v-else-if="row.menuType === 1" type="primary" size="small">菜单</el-tag>
            <el-tag v-else type="info" size="small">按钮</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="路由路径" width="180" />
        <el-table-column prop="component" label="组件路径" width="200" show-overflow-tooltip />
        <el-table-column prop="perms" label="权限标识" width="180" />
        <el-table-column prop="icon" label="图标" width="100" />
        <el-table-column prop="sort" label="排序" width="70" align="center" />
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openAdd(row.id)">新增</el-button>
            <el-button type="warning" link size="small" @click="openEdit(row)">编辑</el-button>
            <el-button type="danger" link size="small" @click="handleDel(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="form" label-width="100px">
        <el-form-item label="父级菜单">
          <el-tree-select
            v-model="form.parentId"
            :data="menuTree"
            :props="{ children: 'children', label: 'menuName', value: 'id' }"
            placeholder="请选择父级菜单"
            clearable
            check-strictly
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="菜单名称" required>
          <el-input v-model="form.menuName" placeholder="请输入菜单名称" />
        </el-form-item>
        <el-form-item label="菜单类型" required>
          <el-radio-group v-model="form.menuType">
            <el-radio :value="0">目录</el-radio>
            <el-radio :value="1">菜单</el-radio>
            <el-radio :value="2">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="路由路径" v-if="form.menuType !== 2">
          <el-input v-model="form.path" placeholder="请输入路由路径" />
        </el-form-item>
        <el-form-item label="组件路径" v-if="form.menuType === 1">
          <el-input v-model="form.component" placeholder="请输入组件路径" />
        </el-form-item>
        <el-form-item label="权限标识">
          <el-input v-model="form.perms" placeholder="请输入权限标识" />
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="form.icon" placeholder="请输入图标类名" />
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
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>
