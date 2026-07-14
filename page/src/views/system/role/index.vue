<script setup lang="ts">
/**
 * 角色管理页面
 * 提供角色的增删改查、状态管理及菜单权限分配功能
 * 包含搜索栏、数据表格、新增/编辑对话框、分配菜单对话框
 */
import { ref, reactive, onMounted, nextTick } from 'vue'
import * as roleApi from '@/api/role'
import * as menuApi from '@/api/menu'
import {ElMessage, ElMessageBox} from "element-plus";

/** 角色信息接口 */
interface Role {
  id: number          // 角色 ID
  roleName: string    // 角色名称
  roleCode: string    // 角色编码
  description: string // 描述
  status: number      // 状态：1 启用，0 停用
  createTime: string  // 创建时间
}

/** 表格数据 */
const tableData = ref<Role[]>([])
/** 数据总条数（分页用） */
const total = ref(0)
/** 表格加载状态 */
const loading = ref(false)
/** 查询参数 */
const queryParams = reactive({
  roleName: '',     // 角色名称搜索条件
  pageNum: 1,       // 当前页码
  pageSize: 10      // 每页条数
})

/** 新增/编辑对话框可见性 */
const dialogVisible = ref(false)
/** 新增/编辑对话框标题 */
const dialogTitle = ref('新增角色')
/** 表单引用 */
const formRef = ref()
/** 表单数据 */
const form = reactive({
  id: 0,              // 角色 ID（编辑时使用）
  roleName: '',       // 角色名称
  roleCode: '',       // 角色编码
  description: ''     // 描述
})
/** 是否为编辑模式 */
const isEdit = ref(false)

/** 分配菜单对话框可见性 */
const menuDialogVisible = ref(false)
/** 当前操作的角色 ID */
const currentRoleId = ref(0)
/** 菜单树数据 */
const menuTree = ref<any[]>([])
/** 树形组件引用 */
const treeRef = ref()

/**
 * 获取角色列表数据
 * 根据查询参数分页查询角色数据
 */
async function fetchData() {
  loading.value = true
  try {
    const res: any = await roleApi.page(queryParams)
    tableData.value = res.data.records   // 当前页数据
    total.value = res.data.total          // 总记录数
  } catch {
    // 错误由拦截器统一提示
  } finally {
    loading.value = false
  }
}

/**
 * 搜索按钮点击处理
 * 重置到第一页后重新查询
 */
function handleSearch() {
  queryParams.pageNum = 1
  fetchData()
}

/**
 * 分页页码变化处理
 * @param page - 新页码
 */
function handlePageChange(page: number) {
  queryParams.pageNum = page
  fetchData()
}

/**
 * 每页条数变化处理
 * @param size - 新的每页条数
 */
function handleSizeChange(size: number) {
  queryParams.pageSize = size
  queryParams.pageNum = 1
  fetchData()
}

/**
 * 打开新增角色对话框
 * 重置表单数据为初始状态
 */
function openAdd() {
  isEdit.value = false
  dialogTitle.value = '新增角色'
  form.id = 0
  form.roleName = ''
  form.roleCode = ''
  form.description = ''
  dialogVisible.value = true
}

/**
 * 打开编辑角色对话框
 * 将选中行的数据回填到表单
 * @param row - 选中的角色数据
 */
function openEdit(row: Role) {
  isEdit.value = true
  dialogTitle.value = '编辑角色'
  form.id = row.id
  form.roleName = row.roleName
  form.roleCode = row.roleCode
  form.description = row.description
  dialogVisible.value = true
}

/**
 * 保存角色（新增或编辑）
 * 根据 isEdit 状态判断调用新增或更新接口
 */
async function handleSave() {
  try {
    if (isEdit.value) {
      // 编辑模式：更新角色信息
      await roleApi.update({
        id: form.id,
        roleName: form.roleName,
        roleCode: form.roleCode,
        description: form.description
      })
      ElMessage.success('更新成功')
    } else {
      // 新增模式：创建新角色
      await roleApi.add({
        roleName: form.roleName,
        roleCode: form.roleCode,
        description: form.description
      })
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchData()  // 刷新列表
  } catch {
    // 错误由拦截器统一提示
  }
}

/**
 * 删除角色
 * 弹出确认框，确认后调用删除接口
 * @param id - 角色 ID
 */
async function handleDel(id: number) {
  try {
    await ElMessageBox.confirm('确认删除该角色吗？', '提示', { type: 'warning' })
    await roleApi.del(id)
    ElMessage.success('删除成功')
    fetchData()
  } catch {
    // 用户取消操作，不做处理
  }
}

/**
 * 切换角色状态（启用/停用）
 * 自动计算目标状态并与当前状态相反
 * @param row - 要操作的角色数据
 */
async function handleSetStatus(row: Role) {
  const newStatus = row.status === 1 ? 0 : 1  // 切换状态
  try {
    await roleApi.setStatus({ id: row.id, status: newStatus })
    ElMessage.success(newStatus === 1 ? '已启用' : '已停用')
    fetchData()
  } catch {
    // 错误由拦截器统一提示
  }
}

/**
 * 打开分配菜单对话框
 * 加载菜单树并设置角色已分配的菜单勾选状态
 * @param row - 要分配菜单的角色数据
 */
async function openAssignMenu(row: Role) {
  currentRoleId.value = row.id
  try {
    // 获取菜单树
    const treeRes: any = await menuApi.tree()
    menuTree.value = treeRes.data || []
    // 获取角色当前已分配的菜单 ID
    const menuRes: any = await roleApi.getMenus(row.id)
    menuDialogVisible.value = true
    // 在对话框渲染完成后，设置树形组件的勾选状态
    await nextTick()
    treeRef.value?.setCheckedKeys(menuRes.data || [])
  } catch {
    // 错误由拦截器统一提示
  }
}

/**
 * 保存菜单分配
 * 从树形组件获取勾选的菜单 ID，调用接口为角色设置菜单权限
 */
async function handleSaveMenus() {
  try {
    const checkedKeys = treeRef.value?.getCheckedKeys() || []
    await roleApi.setMenus(currentRoleId.value, checkedKeys)
    ElMessage.success('分配菜单成功')
    menuDialogVisible.value = false
  } catch {
    // 错误由拦截器统一提示
  }
}

onMounted(() => {
  fetchData()  // 页面加载时获取角色列表
})
</script>

<template>
  <div>
    <!-- 搜索栏 -->
    <el-card style="margin-bottom: 16px">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="角色名称">
          <el-input v-model="queryParams.roleName" placeholder="角色名称" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="queryParams.roleName = ''; handleSearch()">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作栏 -->
    <el-card>
      <div style="margin-bottom: 16px">
        <el-button type="primary" @click="openAdd">新增</el-button>
      </div>

      <!-- 表格 -->
      <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" align="center" />
        <el-table-column prop="roleName" label="角色名称" min-width="140" />
        <el-table-column prop="roleCode" label="角色编码" min-width="140" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="320" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openEdit(row)">编辑</el-button>
            <el-button type="danger" link size="small" @click="handleDel(row.id)">删除</el-button>
            <el-button :type="row.status === 1 ? 'warning' : 'success'" link size="small" @click="handleSetStatus(row)">
              {{ row.status === 1 ? '停用' : '启用' }}
            </el-button>
            <el-button type="success" link size="small" @click="openAssignMenu(row)">分配菜单</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div style="margin-top: 16px; display: flex; justify-content: flex-end">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="form" label-width="100px">
        <el-form-item label="角色名称" required>
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" required>
          <el-input v-model="form.roleCode" placeholder="请输入角色编码" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" placeholder="请输入描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">确定</el-button>
      </template>
    </el-dialog>

    <!-- 分配菜单对话框 -->
    <el-dialog v-model="menuDialogVisible" title="分配菜单" width="450px" :close-on-click-modal="false" destroy-on-close>
      <el-tree
        ref="treeRef"
        :data="menuTree"
        show-checkbox
        node-key="id"
        :props="{ children: 'children', label: 'menuName' }"
        :check-strictly="true"
        default-expand-all
      />
      <template #footer>
        <el-button @click="menuDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveMenus">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>
