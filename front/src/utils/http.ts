import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosResponse, type AxiosError } from 'axios'
import { message } from 'ant-design-vue'
import { ERROR_MESSAGES } from '@/constants/api'

export interface ApiResponse<T = any> {
  code: number
  data: T
  message: string
  success: boolean
  timestamp: number
}

// 请求计数器，用于显示加载状态
let requestCount = 0

// 显示加载状态的函数
const showLoading = () => {
  requestCount++
  // 这里可以集成全局加载状态管理
}

// 隐藏加载状态的函数
const hideLoading = () => {
  requestCount--
  if (requestCount <= 0) {
    requestCount = 0
    // 这里可以集成全局加载状态管理
  }
}

// 创建axios实例
const http: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 30000, // 增加超时时间以支持规则测试等耗时操作
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  },
  // 允许跨域请求携带凭证
  withCredentials: false
})

// 请求拦截器
http.interceptors.request.use(
  (config: AxiosRequestConfig) => {
    // 显示加载状态
    if (config.headers && config.headers['show-loading'] !== 'false') {
      showLoading()
    }

    // 添加请求时间戳，用于调试
    config.metadata = { startTime: new Date() }

    // 可以在这里添加token等认证信息
    const token = localStorage.getItem('auth_token')
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`
    }

    // 为特定的请求类型设置特殊的Content-Type
    if (config.data instanceof FormData) {
      // FormData会自动设置Content-Type，包括boundary
      delete config.headers['Content-Type']
    }

    // 添加请求ID用于追踪
    config.headers['X-Request-ID'] = `req_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`

    console.log(`[HTTP Request] ${config.method?.toUpperCase()} ${config.url}`, {
      headers: config.headers,
      params: config.params,
      data: config.data instanceof FormData ? '[FormData]' : config.data
    })

    return config
  },
  (error) => {
    hideLoading()
    console.error('[HTTP Request Error]', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
http.interceptors.response.use(
  (response: AxiosResponse): ApiResponse<any> | AxiosResponse<any> | Promise<any> | any => {
    // 隐藏加载状态
    hideLoading()

    // 检查业务层面的错误
    if (response.data && typeof response.data === 'object') {
      // 如果响应包含success字段且为false，视为业务错误
      if ('success' in response.data && response.data.success === false) {
        const errorMessage = response.data.message || ERROR_MESSAGES.SERVER_ERROR
        console.warn('[Business Error]', errorMessage, response.data)
        
        // 对于某些特定的业务错误，不显示全局提示
        const silentErrors = ['RULE_VALIDATION_ERROR', 'RULE_SYNTAX_ERROR']
        if (!silentErrors.includes(response.data.code)) {
          message.error(errorMessage)
        }
      }
    }

    return response.data
  },
  (error: AxiosError) => {
    // 隐藏加载状态
    hideLoading()

    console.error('[HTTP Response Error]', error)

    // 处理网络错误
    if (!error.response) {
      if (error.code === 'ECONNABORTED') {
        message.error('请求超时，请稍后重试')
      } else if (error.message === 'Network Error') {
        message.error(ERROR_MESSAGES.NETWORK_ERROR)
      } else {
        message.error('网络连接异常，请检查网络设置')
      }
      return Promise.reject(error)
    }

    // 处理HTTP状态码错误
    const { status, data } = error.response
    const requestId = error.config?.headers?.['X-Request-ID']
    
    console.error(`[HTTP Error ${status}] Request ID: ${requestId}`, {
      url: error.config?.url,
      method: error.config?.method,
      status,
      data
    })

    // 根据状态码进行不同的错误处理
    switch (status) {
      case 400:
        // 请求参数错误
        if (data?.errors && Array.isArray(data.errors)) {
          // 处理验证错误
          const errorMessages = data.errors.map((err: any) => err.message || err.field).join('; ')
          message.error(`请求参数错误: ${errorMessages}`)
        } else {
          message.error(data?.message || '请求参数错误')
        }
        break

      case 401:
        // 未授权
        message.error('登录已过期，请重新登录')
        // 清除本地存储的认证信息
        localStorage.removeItem('auth_token')
        // 可以在这里触发重新登录逻辑
        break

      case 403:
        // 禁止访问
        message.error('权限不足，无法访问该资源')
        break

      case 404:
        // 资源不存在
        message.error(data?.message || '请求的资源不存在')
        break

      case 409:
        // 冲突错误，通常是数据冲突
        message.error(data?.message || '数据冲突，请刷新后重试')
        break

      case 422:
        // 实体无法处理（通常是验证错误）
        if (data?.errors && Array.isArray(data.errors)) {
          const errorMessages = data.errors.map((err: any) => err.message).join('; ')
          message.error(`数据验证失败: ${errorMessages}`)
        } else {
          message.error(data?.message || '数据验证失败')
        }
        break

      case 429:
        // 请求过于频繁
        message.error('请求过于频繁，请稍后重试')
        break

      case 500:
        // 服务器内部错误
        message.error(data?.message || ERROR_MESSAGES.SERVER_ERROR)
        break

      case 502:
        // 网关错误
        message.error('服务暂时不可用，请稍后重试')
        break

      case 503:
        // 服务不可用
        message.error('服务维护中，请稍后重试')
        break

      case 504:
        // 网关超时
        message.error('服务响应超时，请稍后重试')
        break

      default:
        // 其他错误
        message.error(data?.message || `网络错误 (${status})，请稍后重试`)
    }
    
    return Promise.reject(error)
  }
)

// 导出HTTP实例和工具函数
export default http

// 导出请求状态管理函数
export { showLoading, hideLoading }

// 导出类型安全的请求方法
export const httpGet = <T = any>(url: string, config?: AxiosRequestConfig): Promise<AxiosResponse<T>> => {
  return http.get<T>(url, config)
}

export const httpPost = <T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<AxiosResponse<T>> => {
  return http.post<T>(url, data, config)
}

export const httpPut = <T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<AxiosResponse<T>> => {
  return http.put<T>(url, data, config)
}

export const httpDelete = <T = any>(url: string, config?: AxiosRequestConfig): Promise<AxiosResponse<T>> => {
  return http.delete<T>(url, config)
}

export const httpPatch = <T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<AxiosResponse<T>> => {
  return http.patch<T>(url, data, config)
}