// 类型验证工具函数

import type { DroolsRule, CreateRuleRequest, UpdateRuleRequest, TestRuleRequest } from '@/types/rule'

/**
 * 检查是否为有效的规则对象
 */
export const isValidRule = (obj: any): obj is DroolsRule => {
  return (
    obj &&
    typeof obj === 'object' &&
    typeof obj.ruleName === 'string' &&
    typeof obj.ruleContent === 'string' &&
    typeof obj.enabled === 'boolean' &&
    (obj.description === undefined || typeof obj.description === 'string') &&
    (obj.id === undefined || typeof obj.id === 'number') &&
    (obj.createTime === undefined || typeof obj.createTime === 'string') &&
    (obj.updateTime === undefined || typeof obj.updateTime === 'string') &&
    (obj.version === undefined || typeof obj.version === 'string')
  )
}

/**
 * 检查是否为有效的创建规则请求
 */
export const isValidCreateRuleRequest = (obj: any): obj is CreateRuleRequest => {
  return (
    obj &&
    typeof obj === 'object' &&
    typeof obj.ruleName === 'string' &&
    obj.ruleName.trim().length > 0 &&
    typeof obj.ruleContent === 'string' &&
    obj.ruleContent.trim().length > 0 &&
    typeof obj.enabled === 'boolean' &&
    (obj.description === undefined || typeof obj.description === 'string')
  )
}

/**
 * 检查是否为有效的更新规则请求
 */
export const isValidUpdateRuleRequest = (obj: any): obj is UpdateRuleRequest => {
  return (
    isValidCreateRuleRequest(obj) &&
    typeof obj.version === 'string' &&
    obj.version.trim().length > 0
  )
}

/**
 * 检查是否为有效的测试规则请求
 */
export const isValidTestRuleRequest = (obj: any): obj is TestRuleRequest => {
  return (
    obj &&
    typeof obj === 'object' &&
    typeof obj.testData === 'object' &&
    obj.testData !== null &&
    (obj.ruleContent === undefined || typeof obj.ruleContent === 'string') &&
    (obj.ruleIds === undefined || (Array.isArray(obj.ruleIds) && obj.ruleIds.every((id: any) => typeof id === 'number')))
  )
}

/**
 * 检查是否为有效的数字
 */
export const isValidNumber = (value: any): value is number => {
  return typeof value === 'number' && !isNaN(value) && isFinite(value)
}

/**
 * 检查是否为有效的正整数
 */
export const isValidPositiveInteger = (value: any): value is number => {
  return isValidNumber(value) && value > 0 && Number.isInteger(value)
}

/**
 * 检查是否为有效的字符串
 */
export const isValidString = (value: any, minLength = 0, maxLength = Infinity): value is string => {
  return (
    typeof value === 'string' &&
    value.length >= minLength &&
    value.length <= maxLength
  )
}

/**
 * 检查是否为有效的非空字符串
 */
export const isValidNonEmptyString = (value: any, maxLength = Infinity): value is string => {
  return isValidString(value, 1, maxLength) && value.trim().length > 0
}

/**
 * 检查是否为有效的布尔值
 */
export const isValidBoolean = (value: any): value is boolean => {
  return typeof value === 'boolean'
}

/**
 * 检查是否为有效的日期字符串
 */
export const isValidDateString = (value: any): value is string => {
  if (typeof value !== 'string') return false
  const date = new Date(value)
  return !isNaN(date.getTime())
}

/**
 * 检查是否为有效的数组
 */
export const isValidArray = <T>(value: any, itemValidator?: (item: any) => item is T): value is T[] => {
  if (!Array.isArray(value)) return false
  if (!itemValidator) return true
  return value.every(itemValidator)
}

/**
 * 检查是否为有效的对象
 */
export const isValidObject = (value: any): value is Record<string, any> => {
  return value !== null && typeof value === 'object' && !Array.isArray(value)
}

/**
 * 深度克隆对象并进行类型检查
 */
export const safeClone = <T>(obj: T): T => {
  if (obj === null || typeof obj !== 'object') return obj
  if (obj instanceof Date) return new Date(obj.getTime()) as T
  if (Array.isArray(obj)) return obj.map(safeClone) as T
  
  const cloned = {} as T
  for (const key in obj) {
    if (Object.prototype.hasOwnProperty.call(obj, key)) {
      cloned[key] = safeClone(obj[key])
    }
  }
  return cloned
}

/**
 * 安全地获取对象属性
 */
export const safeGet = <T>(obj: any, path: string, defaultValue?: T): T | undefined => {
  const keys = path.split('.')
  let current = obj
  
  for (const key of keys) {
    if (current === null || current === undefined || typeof current !== 'object') {
      return defaultValue
    }
    current = current[key]
  }
  
  return current !== undefined ? current : defaultValue
}