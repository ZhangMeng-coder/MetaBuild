/**
 * 操作日志 API
 * 提供操作日志的分页查询功能
 */
import request from '@/utils/request'

/**
 * 分页查询操作日志列表
 * @param params - 查询参数，包含分页信息和操作名称筛选
 * @returns Promise - 返回分页后的操作日志列表数据
 */
export function page(params: any) {
  return request.post('/api/system/log/page', params)
}
