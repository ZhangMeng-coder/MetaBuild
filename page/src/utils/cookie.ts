/**
 * Cookie 操作工具
 * 提供 Cookie 的增删改查封装，以及 Token 的专用存取方法
 * Token 默认 7 天过期
 */
const TOKEN_KEY = 'token'       // Token 在 Cookie 中存储的键名
const DEFAULT_EXPIRES = 7       // 默认过期天数

/**
 * 设置 Cookie
 * @param name - Cookie 名称
 * @param value - Cookie 值（会进行 URI 编码）
 * @param days - 过期天数，默认 7 天
 */
export function setCookie(name: string, value: string, days: number = DEFAULT_EXPIRES) {
  const expires = new Date(Date.now() + days * 24 * 60 * 60 * 1000).toUTCString()
  document.cookie = `${name}=${encodeURIComponent(value)}; expires=${expires}; path=/`
}

/**
 * 获取 Cookie
 * @param name - Cookie 名称
 * @returns Cookie 值，如果不存在则返回空字符串
 */
export function getCookie(name: string): string {
  const match = document.cookie.match(new RegExp(`(^| )${name}=([^;]*)`))
  return match ? decodeURIComponent(match[2]) : ''
}

/**
 * 删除 Cookie
 * 通过将过期时间设置为过去的时间来删除指定 Cookie
 * @param name - Cookie 名称
 */
export function removeCookie(name: string) {
  document.cookie = `${name}=; expires=Thu, 01 Jan 1970 00:00:00 GMT; path=/`
}

/**
 * 获取登录 Token
 * @returns Token 字符串，不存在则返回空字符串
 */
export function getToken(): string {
  return getCookie(TOKEN_KEY)
}

/**
 * 设置登录 Token
 * @param token - Token 字符串
 */
export function setToken(token: string) {
  setCookie(TOKEN_KEY, token)
}

/**
 * 删除登录 Token
 */
export function removeToken() {
  removeCookie(TOKEN_KEY)
}