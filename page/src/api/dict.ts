/**
 * 字典管理 API
 * 提供字典类型和字典数据的增删改查功能
 */
import request from '@/utils/request'

// ==================== 字典类型 ====================

/**
 * 分页查询字典类型
 * @param data - 分页参数
 * @returns Promise - 返回分页数据
 */
export function dictTypePage(data: any) {
  return request.post('/api/system/dict/type/page', data)
}

/**
 * 查询字典类型详情
 * @param id - 字典类型 ID
 * @returns Promise - 返回字典类型信息
 */
export function dictTypeGet(id: number) {
  return request.get(`/api/system/dict/type/${id}`)
}

/**
 * 新增字典类型
 * @param data - 字典类型信息
 * @returns Promise - 返回新增结果
 */
export function dictTypeAdd(data: any) {
  return request.post('/api/system/dict/type', data)
}

/**
 * 修改字典类型
 * @param data - 字典类型信息
 * @returns Promise - 返回修改结果
 */
export function dictTypeUpdate(data: any) {
  return request.put('/api/system/dict/type', data)
}

/**
 * 删除字典类型
 * @param id - 字典类型 ID
 * @returns Promise - 返回删除结果
 */
export function dictTypeDel(id: number) {
  return request.delete(`/api/system/dict/type/${id}`)
}

// ==================== 字典数据 ====================

/**
 * 分页查询字典数据
 * @param data - 分页参数
 * @param dictType - 字典类型
 * @returns Promise - 返回分页数据
 */
export function dictDataPage(data: any, dictType?: string) {
  let url = '/api/system/dict/data/page'
  if (dictType) {
    url += `?dictType=${dictType}`
  }
  return request.post(url, data)
}

/**
 * 查询字典数据详情
 * @param id - 字典数据 ID
 * @returns Promise - 返回字典数据信息
 */
export function dictDataGet(id: number) {
  return request.get(`/api/system/dict/data/${id}`)
}

/**
 * 新增字典数据
 * @param data - 字典数据信息
 * @returns Promise - 返回新增结果
 */
export function dictDataAdd(data: any) {
  return request.post('/api/system/dict/data', data)
}

/**
 * 修改字典数据
 * @param data - 字典数据信息
 * @returns Promise - 返回修改结果
 */
export function dictDataUpdate(data: any) {
  return request.put('/api/system/dict/data', data)
}

/**
 * 删除字典数据
 * @param id - 字典数据 ID
 * @returns Promise - 返回删除结果
 */
export function dictDataDel(id: number) {
  return request.delete(`/api/system/dict/data/${id}`)
}

/**
 * 根据字典类型获取启用数据列表（供下拉框等使用）
 * @param dictType - 字典类型
 * @returns Promise - 返回字典数据列表
 */
export function getDictDataByType(dictType: string) {
  return request.get(`/api/system/dict/data/type/${dictType}`)
}
