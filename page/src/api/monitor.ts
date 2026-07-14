import request from '@/utils/request'

/** 慢SQL分页查询 */
export function slowSqlPage(params: any) {
  return request.post('/api/system/monitor/slow-sql/page', params)
}

/** 接口耗时分页查询 */
export function apiTimingPage(params: any) {
  return request.post('/api/system/monitor/api-timing/page', params)
}
