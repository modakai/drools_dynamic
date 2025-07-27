// API错误处理工具

import { message } from 'ant-design-vue'
import { ERROR_MESSAGES } from '@/constants/api'
import type { AxiosError } from 'axios'
import type { ApiResponse, ValidationResult } from '@/types/rule'

/**
 * API错误类型枚举
 */
export enum ApiErrorType {
  NETWORK_ERROR = 'NETWORK_ERROR',
  TIMEOUT_ERROR = 'TIMEOUT_ERROR',
  VALIDATION_ERROR = 'VALIDATION_ERROR',
  BUSINESS_ERROR = 'BUSINESS_ERROR',
  SERVER_ERROR = 'SERVER_ERROR',
  UNKNOWN_ERROR = 'UNKNOWN_ERROR'
}

/**
 * 标准化的API错误接口
 */
export interface StandardApiError {
  type: ApiErrorType
  code?: string
  message: string
  details?: any
  originalError?: any
}

/**
 * 错误处理配置
 */
export interface ErrorHandlerConfig {
  showMessage?: boolean
  logError?: boolean
  throwError?: boolean
  customHandler?: (error: StandardApiError) => void
}

/**
 * 默认错误处理配置
 */
const defaultErrorConfig: ErrorHandlerConfig = {
  showMessage: true,
  logError: true,
  throwError: true
}

/**
 * 将Axios错误转换为标准化错误
 */
export const normalizeApiError = (error: any): StandardApiError => {
  // 处理Axios错误
  if (error.isAxiosError || error.response) {
    const axiosError = error as AxiosError
    
    // 网络错误
    if (!axiosError.response) {
      if (axiosError.code === 'ECONNABORTED') {
        return {
          type: ApiErrorType.TIMEOUT_ERROR,
          message: '请求超时，请稍后重试',
          originalError: error
        }
      } else if (axiosError.message === 'Network Error') {
        return {
          type: ApiErrorType.NETWORK_ERROR,
          message: ERROR_MESSAGES.NETWORK_ERROR,
          originalError: error
        }
      } else {
        return {
          type: ApiErrorType.NETWORK_ERROR,
          message: '网络连接异常',
          originalError: error
        }
      }
    }

    // HTTP状态码错误
    const { status, data } = axiosError.response
    
    switch (status) {
      case 400:
        return {
          type: ApiErrorType.VALIDATION_ERROR,
          code: data?.code || 'BAD_REQUEST',
          message: data?.message || '请求参数错误',
          details: data?.errors || data?.details,
          originalError: error
        }

      case 422:
        return {
          type: ApiErrorType.VALIDATION_ERROR,
          code: data?.code || 'VALIDATION_ERROR',
          message: data?.message || '数据验证失败',
          details: data?.errors || data?.details,
          originalError: error
        }

      case 409:
        return {
          type: ApiErrorType.BUSINESS_ERROR,
          code: data?.code || 'CONFLICT',
          message: data?.message || '数据冲突',
          details: data?.details,
          originalError: error
        }

      case 500:
      case 502:
      case 503:
      case 504:
        return {
          type: ApiErrorType.SERVER_ERROR,
          code: data?.code || 'SERVER_ERROR',
          message: data?.message || ERROR_MESSAGES.SERVER_ERROR,
          details: data?.details,
          originalError: error
        }

      default:
        return {
          type: ApiErrorType.UNKNOWN_ERROR,
          code: data?.code || `HTTP_${status}`,
          message: data?.message || `HTTP错误 (${status})`,
          details: data?.details,
          originalError: error
        }
    }
  }

  // 处理业务逻辑错误
  if (error.success === false) {
    return {
      type: ApiErrorType.BUSINESS_ERROR,
      code: error.code,
      message: error.message || '业务处理失败',
      details: error.details,
      originalError: error
    }
  }

  // 处理其他类型的错误
  if (error instanceof Error) {
    return {
      type: ApiErrorType.UNKNOWN_ERROR,
      message: error.message,
      originalError: error
    }
  }

  // 处理字符串错误
  if (typeof error === 'string') {
    return {
      type: ApiErrorType.UNKNOWN_ERROR,
      message: error,
      originalError: error
    }
  }

  // 未知错误
  return {
    type: ApiErrorType.UNKNOWN_ERROR,
    message: '未知错误',
    originalError: error
  }
}

/**
 * 处理API错误
 */
export const handleApiError = (error: any, config: ErrorHandlerConfig = {}): StandardApiError => {
  const finalConfig = { ...defaultErrorConfig, ...config }
  const standardError = normalizeApiError(error)

  // 记录错误日志
  if (finalConfig.logError) {
    console.error('[API Error]', {
      type: standardError.type,
      code: standardError.code,
      message: standardError.message,
      details: standardError.details,
      originalError: standardError.originalError
    })
  }

  // 显示错误消息
  if (finalConfig.showMessage) {
    // 对于验证错误，显示详细的错误信息
    if (standardError.type === ApiErrorType.VALIDATION_ERROR && standardError.details) {
      if (Array.isArray(standardError.details)) {
        const errorMessages = standardError.details
          .map((err: any) => err.message || err.field)
          .filter(Boolean)
          .join('; ')
        
        if (errorMessages) {
          message.error(`${standardError.message}: ${errorMessages}`)
        } else {
          message.error(standardError.message)
        }
      } else {
        message.error(standardError.message)
      }
    } else {
      message.error(standardError.message)
    }
  }

  // 执行自定义错误处理
  if (finalConfig.customHandler) {
    try {
      finalConfig.customHandler(standardError)
    } catch (handlerError) {
      console.error('[Custom Error Handler Failed]', handlerError)
    }
  }

  // 是否抛出错误
  if (finalConfig.throwError) {
    throw standardError
  }

  return standardError
}

/**
 * 创建错误处理装饰器
 */
export const withErrorHandler = (config?: ErrorHandlerConfig) => {
  return <T extends (...args: any[]) => Promise<any>>(
    target: any,
    propertyName: string,
    descriptor: TypedPropertyDescriptor<T>
  ) => {
    const method = descriptor.value!

    descriptor.value = (async function (this: any, ...args: any[]) {
      try {
        return await method.apply(this, args)
      } catch (error) {
        return handleApiError(error, config)
      }
    }) as T
  }
}

/**
 * 验证API响应格式
 */
export const validateApiResponse = <T>(response: any): ApiResponse<T> => {
  if (!response || typeof response !== 'object') {
    throw new Error('Invalid API response format')
  }

  if (typeof response.success !== 'boolean') {
    throw new Error('API response missing success field')
  }

  if (!response.success && !response.message) {
    throw new Error('Failed API response missing error message')
  }

  return response as ApiResponse<T>
}

/**
 * 安全的API调用包装器
 */
export const safeApiCall = async <T>(
  apiCall: () => Promise<T>,
  errorConfig?: ErrorHandlerConfig
): Promise<T | null> => {
  try {
    return await apiCall()
  } catch (error) {
    handleApiError(error, { ...errorConfig, throwError: false })
    return null
  }
}

/**
 * 批量API调用错误处理
 */
export const handleBatchApiErrors = (
  results: Array<{ success: boolean; data?: any; error?: any }>,
  config?: ErrorHandlerConfig
): void => {
  const errors = results.filter(result => !result.success)
  
  if (errors.length === 0) return

  if (errors.length === 1) {
    handleApiError(errors[0].error, config)
  } else {
    const errorMessage = `批量操作失败: ${errors.length}个操作失败`
    handleApiError(new Error(errorMessage), config)
  }
}

/**
 * 重试机制包装器
 */
export const withRetry = async <T>(
  apiCall: () => Promise<T>,
  maxRetries: number = 3,
  delay: number = 1000,
  retryCondition?: (error: StandardApiError) => boolean
): Promise<T> => {
  let lastError: StandardApiError

  for (let attempt = 1; attempt <= maxRetries; attempt++) {
    try {
      return await apiCall()
    } catch (error) {
      lastError = normalizeApiError(error)
      
      // 检查是否应该重试
      if (retryCondition && !retryCondition(lastError)) {
        throw lastError
      }

      // 如果是最后一次尝试，直接抛出错误
      if (attempt === maxRetries) {
        throw lastError
      }

      // 等待后重试
      console.log(`[API Retry] Attempt ${attempt}/${maxRetries} failed, retrying in ${delay}ms...`)
      await new Promise(resolve => setTimeout(resolve, delay * attempt))
    }
  }

  throw lastError!
}

/**
 * 默认重试条件：只对网络错误和服务器错误重试
 */
export const defaultRetryCondition = (error: StandardApiError): boolean => {
  return error.type === ApiErrorType.NETWORK_ERROR || 
         error.type === ApiErrorType.TIMEOUT_ERROR ||
         error.type === ApiErrorType.SERVER_ERROR
}