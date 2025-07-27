// 类型守卫工具函数

import type { ApiResponse, PageResponse, ValidationResult } from '@/types/rule'

/**
 * 检查是否为API响应格式
 */
export const isApiResponse = <T>(obj: any): obj is ApiResponse<T> => {
  return (
    obj &&
    typeof obj === 'object' &&
    typeof obj.success === 'boolean' &&
    obj.data !== undefined &&
    (obj.message === undefined || typeof obj.message === 'string') &&
    (obj.code === undefined || typeof obj.code === 'string') &&
    (obj.timestamp === undefined || typeof obj.timestamp === 'string')
  )
}

/**
 * 检查是否为分页响应格式
 */
export const isPageResponse = <T>(obj: any): obj is PageResponse<T> => {
  return (
    obj &&
    typeof obj === 'object' &&
    Array.isArray(obj.content) &&
    typeof obj.totalElements === 'number' &&
    typeof obj.totalPages === 'number' &&
    typeof obj.page === 'number' &&
    typeof obj.size === 'number' &&
    typeof obj.first === 'boolean' &&
    typeof obj.last === 'boolean'
  )
}

/**
 * 检查是否为验证结果格式
 */
export const isValidationResult = (obj: any): obj is ValidationResult => {
  return (
    obj &&
    typeof obj === 'object' &&
    typeof obj.valid === 'boolean' &&
    Array.isArray(obj.errors) &&
    obj.errors.every((error: any) => 
      error &&
      typeof error === 'object' &&
      typeof error.field === 'string' &&
      typeof error.message === 'string' &&
      (error.code === undefined || typeof error.code === 'string')
    )
  )
}

/**
 * 检查是否为错误对象
 */
export const isError = (obj: any): obj is Error => {
  return obj instanceof Error || (
    obj &&
    typeof obj === 'object' &&
    typeof obj.message === 'string' &&
    typeof obj.name === 'string'
  )
}

/**
 * 检查是否为Promise对象
 */
export const isPromise = <T>(obj: any): obj is Promise<T> => {
  return obj && typeof obj.then === 'function'
}

/**
 * 检查是否为函数
 */
export const isFunction = (obj: any): obj is Function => {
  return typeof obj === 'function'
}

/**
 * 检查是否为空值（null或undefined）
 */
export const isNullOrUndefined = (obj: any): obj is null | undefined => {
  return obj === null || obj === undefined
}

/**
 * 检查是否为非空值
 */
export const isNotNullOrUndefined = <T>(obj: T | null | undefined): obj is T => {
  return obj !== null && obj !== undefined
}

/**
 * 检查是否为空字符串或空白字符串
 */
export const isEmptyOrWhitespace = (str: any): boolean => {
  return typeof str !== 'string' || str.trim().length === 0
}

/**
 * 检查是否为有效的ID
 */
export const isValidId = (id: any): id is number => {
  return typeof id === 'number' && id > 0 && Number.isInteger(id)
}

/**
 * 类型断言辅助函数
 */
export const assertType = <T>(obj: any, guard: (obj: any) => obj is T, errorMessage?: string): T => {
  if (!guard(obj)) {
    throw new Error(errorMessage || 'Type assertion failed')
  }
  return obj
}

/**
 * 安全的类型转换
 */
export const safeCast = <T>(obj: any, guard: (obj: any) => obj is T, defaultValue: T): T => {
  return guard(obj) ? obj : defaultValue
}