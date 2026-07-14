/**
 * Axios 请求封装
 * 统一配置请求基地址、超时时间，并添加请求/响应拦截器处理 Token 鉴权和错误提示
 */
import axios from 'axios'
import { getToken, removeToken } from '@/utils/cookie'
import { ElMessage } from 'element-plus'

/** 创建 Axios 实例，配置基础 URL 和超时时间 */
const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL as string, // API 基础路径，从环境变量读取
  timeout: 10000, // 请求超时时间（毫秒）
})

/**
 * 请求拦截器
 * 在发送请求前，从 Cookie 中获取 Token 并添加到请求头的 Authorization 字段
 */
request.interceptors.request.use(
  (config) => {
    const token = getToken()
    if (token) {
      // 将 Token 以 Bearer 格式放入请求头
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    // 请求配置出错，直接拒绝
    return Promise.reject(error)
  },
)

/**
 * 响应拦截器
 * 统一处理响应数据，对业务错误码和 HTTP 状态码进行集中处理
 */
request.interceptors.response.use(
  (response) => {
    const res = response.data
    // HTTP 200 但业务 code 不为 200，视为业务错误，统一提示
    if (res.code !== 200) {
      ElMessage.error(res.msg || '请求失败')
      return Promise.reject(new Error(res.msg || '请求失败'))
    }
    return res
  },
  (error) => {
    // 处理 HTTP 错误状态码
    if (error.response) {
      const { status, data } = error.response
      if (status === 401) {
        // 未授权：无 token / token 过期 / token 无效 → 清除 token 并跳转登录页
        removeToken()
        window.location.hash = '#/login'
        ElMessage.error('登录已过期，请重新登录')
      } else if (status === 403) {
        // 已登录但无操作权限
        ElMessage.error(data?.msg || '权限不足')
      } else {
        // 其他 HTTP 错误
        ElMessage.error(data?.msg || '请求失败')
      }
    } else {
      // 网络层错误（如断网、DNS 解析失败等）
      ElMessage.error('网络错误，请检查连接')
    }
    return Promise.reject(error)
  },
)

export default request