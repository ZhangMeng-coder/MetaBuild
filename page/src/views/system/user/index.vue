<script setup lang="ts">
/**
 * 用户管理页面
 * 提供用户的增删改查、状态管理、密码重置及角色分配功能
 * 包含搜索栏、数据表格、新增/编辑对话框、分配角色对话框
 */
import { ref, reactive, onMounted } from 'vue'
import * as userApi from '@/api/user'
import * as roleApi from '@/api/role'
import {ElMessage, ElMessageBox} from "element-plus";

/** 用户信息接口 */
interface User {
  id: number          // 用户 ID
  username: string    // 用户名
  realName: string    // 真实姓名
  roleNames: string   // 角色名称（逗号分隔）
  email: string       // 邮箱
  phone: string       // 手机号
  status: number      // 状态：1 启用，0 停用
  createTime: string  // 创建时间
}

/** 表格数据 */
const tableData = ref<User[]>([])
/** 数据总条数（分页用） */
const total = ref(0)
/** 表格加载状态 */
const loading = ref(false)
/** 查询参数 */
const queryParams = reactive({
  username: '',                // 用户名搜索条件
  status: undefined as number | undefined, // 状态筛选条件
  pageNum: 1,                  // 当前页码
  pageSize: 10                 // 每页条数
})

/** 新增/编辑对话框可见性 */
const dialogVisible = ref(false)
/** 新增/编辑对话框标题 */
const dialogTitle = ref('新增用户')
/** 表单数据 */
const form = reactive({
  id: 0,            // 用户 ID（编辑时使用）
  username: '',     // 用户名
  realName: '',     // 真实姓名
  email: '',        // 邮箱
  phone: '',        // 手机号
  password: ''      // 密码（新增时必填）
})
/** 是否为编辑模式 */
const isEdit = ref(false)

/** 分配角色对话框可见性 */
const roleDialogVisible = ref(false)
/** 当前操作的用户 ID */
const currentUserId = ref(0)
/** 所有角色列表 */
const allRoles = ref<any[]>([])
/** 已选中的角色 ID 列表 */
const selectedRoles = ref<number[]>([])

/**
 * 获取用户列表数据
 * 根据查询参数分页查询用户数据
 */
async function fetchData() {
  loading.value = true
  try {
    const res: any = await userApi.page(queryParams)
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
 * 打开新增用户对话框
 * 重置表单数据为初始状态
 */
function openAdd() {
  isEdit.value = false
  dialogTitle.value = '新增用户'
  form.id = 0
  form.username = ''
  form.realName = ''
  form.email = ''
  form.phone = ''
  form.password = ''
  dialogVisible.value = true
}

/**
 * 打开编辑用户对话框
 * 将选中行的数据回填到表单
 * @param row - 选中的用户数据
 */
function openEdit(row: User) {
  isEdit.value = true
  dialogTitle.value = '编辑用户'
  form.id = row.id
  form.username = row.username
  form.realName = row.realName
  form.email = row.email
  form.phone = row.phone
  form.password = ''  // 编辑时密码置空，不显示
  dialogVisible.value = true
}

/**
 * 保存用户（新增或编辑）
 * 根据 isEdit 状态判断调用新增或更新接口
 */
async function handleSave() {
  try {
    if (isEdit.value) {
      // 编辑模式：更新用户信息（不包含密码）
      await userApi.update({
        id: form.id,
        username: form.username,
        realName: form.realName,
        email: form.email,
        phone: form.phone
      })
      ElMessage.success('更新成功')
    } else {
      // 新增模式：创建新用户
      await userApi.add({
        username: form.username,
        realName: form.realName,
        email: form.email,
        phone: form.phone,
        password: form.password
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
 * 删除用户
 * 弹出确认框，确认后调用删除接口
 * @param id - 用户 ID
 */
async function handleDel(id: number) {
  try {
    await ElMessageBox.confirm('确认删除该用户吗？', '提示', { type: 'warning' })
    await userApi.del(id)
    ElMessage.success('删除成功')
    fetchData()
  } catch {
    // 用户取消操作，不做处理
  }
}

/**
 * 重置用户密码
 * 弹出确认框，确认后调用重置密码接口
 * @param id - 用户 ID
 */
async function handleResetPassword(id: number) {
  try {
    await ElMessageBox.confirm('确认重置该用户密码吗？', '提示', { type: 'warning' })
    await userApi.resetPassword(id)
    ElMessage.success('密码重置成功')
  } catch {
    // 用户取消操作，不做处理
  }
}

/**
 * 切换用户状态（启用/停用）
 * 自动计算目标状态并与当前状态相反
 * @param row - 要操作的用户数据
 */
async function handleSetStatus(row: User) {
  const newStatus = row.status === 1 ? 0 : 1  // 切换状态
  try {
    await userApi.setStatus({ id: row.id, status: newStatus })
    ElMessage.success(newStatus === 1 ? '已启用' : '已停用')
    fetchData()
  } catch {
    // 错误由拦截器统一提示
  }
}

/**
 * 打开分配角色对话框
 * 加载所有角色列表和用户已分配的角色
 * @param row - 要分配角色的用户数据
 */
async function openAssignRole(row: User) {
  currentUserId.value = row.id
  try {
    // 获取所有角色列表
    const roleRes: any = await roleApi.page({ pageNum: 1, pageSize: 999 })
    allRoles.value = roleRes.data.records || []
    // 获取用户当前已分配的角色
    const roleRes2: any = await userApi.getRoles(row.id)
    selectedRoles.value = roleRes2.data || []
    roleDialogVisible.value = true
  } catch {
    // 错误由拦截器统一提示
  }
}

/**
 * 保存角色分配
 * 调用接口为用户设置选中的角色
 */
async function handleSaveRoles() {
  try {
    await userApi.setRoles(currentUserId.value, selectedRoles.value)
    ElMessage.success('分配角色成功')
    roleDialogVisible.value = false
  } catch {
    // 错误由拦截器统一提示
  }
}

onMounted(() => {
  fetchData()  // 页面加载时获取用户列表
})
</script>

<template>
  <div>
    <!-- 搜索栏 -->
    <el-card style="margin-bottom: 16px">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="用户名">
          <el-input v-model="queryParams.username" placeholder="用户名" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 120px">
            <el-option :value="1" label="正常" />
            <el-option :value="0" label="停用" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="queryParams.username = ''; queryParams.status = undefined; handleSearch()">重置</el-button>
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
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="realName" label="真实姓名" min-width="120" />
        <el-table-column label="角色" min-width="160">
          <template #default="{ row }">
            <template v-for="role in ((row.roleNames || '').split(', ').filter((r: string) => r))" :key="role">
              <el-tag size="small" style="margin-right: 4px; margin-bottom: 2px">{{ role }}</el-tag>
            </template>
          </template>
        </el-table-column>
        <el-table-column prop="email" label="邮箱" min-width="160" />
        <el-table-column prop="phone" label="手机号" min-width="130" />
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openEdit(row)">编辑</el-button>
            <el-button type="danger" link size="small" @click="handleDel(row.id)">删除</el-button>
            <el-button type="warning" link size="small" @click="handleResetPassword(row.id)">重置密码</el-button>
            <el-button :type="row.status === 1 ? 'warning' : 'success'" link size="small" @click="handleSetStatus(row)">
              {{ row.status === 1 ? '停用' : '启用' }}
            </el-button>
            <el-button type="info" link size="small" @click="openAssignRole(row)">分配角色</el-button>
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
        <el-form-item label="用户名" required>
          <el-input v-model="form.username" placeholder="请输入用户名" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="form.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="密码" v-if="!isEdit" required>
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">确定</el-button>
      </template>
    </el-dialog>

    <!-- 分配角色对话框 -->
    <el-dialog v-model="roleDialogVisible" title="分配角色" width="500px" :close-on-click-modal="false">
      <el-checkbox-group v-model="selectedRoles">
        <el-checkbox v-for="role in allRoles" :key="role.id" :label="role.id" style="margin-bottom: 8px">
          {{ role.roleName }}
        </el-checkbox>
      </el-checkbox-group>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveRoles">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>
