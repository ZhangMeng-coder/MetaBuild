/**
 * 元数据管理 API
 * 提供元数据表的增删改查、数据库表/列信息查询等功能
 */
import request from '@/utils/request'

// ==================== 数据库元数据查询 ====================

/**
 * 查询当前数据库中所有表（排除 sys_ 和 tb_ 开头的表）
 * @returns Promise - 返回表列表
 */
export function getDbTables() {
  return request.get('/api/server/meta/table/db-tables')
}

/**
 * 查询指定表的列信息
 * @param tableName - 表名
 * @returns Promise - 返回列信息列表
 */
export function getDbColumns(tableName: string) {
  return request.get(`/api/server/meta/table/db-columns/${tableName}`)
}

// ==================== 元数据表 CRUD ====================

/**
 * 分页查询元数据表
 * @param data - 分页参数
 * @returns Promise - 返回分页数据
 */
export function metaTablePage(data: any) {
  return request.post('/api/server/meta/table/page', data)
}

/**
 * 查询元数据表详情
 * @param id - 元数据表 ID
 * @returns Promise - 返回元数据表信息
 */
export function metaTableGet(id: number) {
  return request.get(`/api/server/meta/table/${id}`)
}

/**
 * 新增元数据表
 * @param data - 元数据表信息（含列定义）
 * @returns Promise - 返回新增结果
 */
export function metaTableAdd(data: any) {
  return request.post('/api/server/meta/table', data)
}

/**
 * 修改元数据表
 * @param data - 元数据表信息
 * @returns Promise - 返回修改结果
 */
export function metaTableUpdate(data: any) {
  return request.put('/api/server/meta/table', data)
}

/**
 * 删除元数据表
 * @param id - 元数据表 ID
 * @returns Promise - 返回删除结果
 */
export function metaTableDel(id: number) {
  return request.delete(`/api/server/meta/table/${id}`)
}

// ==================== 元数据列管理 ====================

/**
 * 查询指定元数据表的所有列
 * @param tableId - 元数据表 ID
 * @returns Promise - 返回列列表
 */
export function metaColumnList(tableId: number) {
  return request.get(`/api/server/meta/table/${tableId}/columns`)
}

/**
 * 修改元数据列
 * @param data - 元数据列信息
 * @returns Promise - 返回修改结果
 */
export function metaColumnUpdate(data: any) {
  return request.put('/api/server/meta/table/column', data)
}

/**
 * 执行字典 SQL 查询
 * @param sql - SQL 语句
 * @returns Promise - 返回查询结果
 */
export function executeDictSql(sql: string) {
  return request.post('/api/server/meta/table/column/dict-sql', { sql })
}

// ==================== 列表/表单配置 ====================

/**
 * 获取列表配置
 * @param tableId - 元数据表 ID
 * @returns Promise - 返回列表配置
 */
export function getListConfig(tableId: number) {
  return request.get(`/api/server/meta/config/${tableId}/list`)
}

/**
 * 保存列表配置
 * @param tableId - 元数据表 ID
 * @param data - 配置列表
 * @returns Promise
 */
export function saveListConfig(tableId: number, data: any[]) {
  return request.post(`/api/server/meta/config/${tableId}/list`, data)
}

/**
 * 获取表单配置
 * @param tableId - 元数据表 ID
 * @returns Promise - 返回表单配置
 */
export function getFormConfig(tableId: number) {
  return request.get(`/api/server/meta/config/${tableId}/form`)
}

/**
 * 保存表单配置
 * @param tableId - 元数据表 ID
 * @param data - 配置列表
 * @returns Promise
 */
export function saveFormConfig(tableId: number, data: any[]) {
  return request.post(`/api/server/meta/config/${tableId}/form`, data)
}

// ==================== 动态数据 CRUD ====================

/**
 * 动态数据分页查询
 * @param tableId - 元数据表 ID
 * @param data - 分页参数
 * @returns Promise
 */
export function dynamicPage(tableId: number, data: any) {
  return request.post(`/api/server/meta/dynamic/${tableId}/page`, data)
}

/**
 * 查询单条动态数据
 * @param tableId - 元数据表 ID
 * @param id - 数据 ID
 * @returns Promise
 */
export function dynamicGet(tableId: number, id: number) {
  return request.get(`/api/server/meta/dynamic/${tableId}/${id}`)
}

/**
 * 新增动态数据
 * @param tableId - 元数据表 ID
 * @param data - 数据
 * @returns Promise
 */
export function dynamicAdd(tableId: number, data: any) {
  return request.post(`/api/server/meta/dynamic/${tableId}`, data)
}

/**
 * 修改动态数据
 * @param tableId - 元数据表 ID
 * @param data - 数据
 * @returns Promise
 */
export function dynamicUpdate(tableId: number, data: any) {
  return request.put(`/api/server/meta/dynamic/${tableId}`, data)
}

/**
 * 删除动态数据
 * @param tableId - 元数据表 ID
 * @param id - 数据 ID
 * @returns Promise
 */
export function dynamicDel(tableId: number, id: number) {
  return request.delete(`/api/server/meta/dynamic/${tableId}/${id}`)
}

/**
 * 导出动态数据
 * @param tableId - 元数据表 ID
 * @param params - 搜索参数
 * @returns Promise
 */
export function dynamicExport(tableId: number, params?: any) {
  return request.post(`/api/server/meta/dynamic/${tableId}/export`, params || {})
}

/**
 * 导入动态数据
 * @param tableId - 元数据表 ID
 * @param file - CSV 文件
 * @returns Promise
 */
export function dynamicImport(tableId: number, file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post(`/api/server/meta/dynamic/${tableId}/import`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}