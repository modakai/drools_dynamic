// API客户端配置

import type { ApiClientConfig } from '@/types/api'

/**
 * API客户端默认配置
 */
export const defaultApiConfig: ApiClientConfig = {
  baseURL: '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  },
  withCredentials: false
}

/**
 * 开发环境API配置
 */
export const developmentApiConfig: ApiClientConfig = {
  ...defaultApiConfig,
  baseURL: process.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  timeout: 60000 // 开发环境增加超时时间
}

/**
 * 生产环境API配置
 */
export const productionApiConfig: ApiClientConfig = {
  ...defaultApiConfig,
  timeout: 15000 // 生产环境减少超时时间
}

/**
 * 测试环境API配置
 */
export const testApiConfig: ApiClientConfig = {
  ...defaultApiConfig,
  baseURL: '/api',
  timeout: 5000 // 测试环境使用较短超时
}

/**
 * 根据环境获取API配置
 */
export const getApiConfig = (): ApiClientConfig => {
  const env = process.env.NODE_ENV || 'development'
  
  switch (env) {
    case 'production':
      return productionApiConfig
    case 'test':
      return testApiConfig
    case 'development':
    default:
      return developmentApiConfig
  }
}

/**
 * API端点配置
 */
export const API_CONFIG = {
  // 基础配置
  BASE_URL: getApiConfig().baseURL,
  TIMEOUT: getApiConfig().timeout,
  
  // 重试配置
  RETRY: {
    MAX_RETRIES: 3,
    RETRY_DELAY: 1000,
    RETRY_MULTIPLIER: 2
  },
  
  // 缓存配置
  CACHE: {
    DEFAULT_TTL: 5 * 60 * 1000, // 5分钟
    RULES_LIST_TTL: 2 * 60 * 1000, // 规则列表缓存2分钟
    RULE_DETAIL_TTL: 10 * 60 * 1000, // 规则详情缓存10分钟
    STATISTICS_TTL: 30 * 1000 // 统计信息缓存30秒
  },
  
  // 分页配置
  PAGINATION: {
    DEFAULT_PAGE_SIZE: 20,
    MAX_PAGE_SIZE: 100,
    MIN_PAGE_SIZE: 5
  },
  
  // 文件上传配置
  UPLOAD: {
    MAX_FILE_SIZE: 10 * 1024 * 1024, // 10MB
    ALLOWED_TYPES: ['.drl', '.txt', '.json'],
    CHUNK_SIZE: 1024 * 1024 // 1MB chunks
  },
  
  // 请求头配置
  HEADERS: {
    CONTENT_TYPE: {
      JSON: 'application/json',
      FORM_DATA: 'multipart/form-data',
      FORM_URLENCODED: 'application/x-www-form-urlencoded',
      OCTET_STREAM: 'application/octet-stream'
    },
    ACCEPT: {
      JSON: 'application/json',
      BLOB: 'application/octet-stream',
      TEXT: 'text/plain'
    }
  }
} as const

/**
 * API错误码映射
 */
export const API_ERROR_CODES = {
  // 通用错误
  UNKNOWN_ERROR: 'UNKNOWN_ERROR',
  NETWORK_ERROR: 'NETWORK_ERROR',
  TIMEOUT_ERROR: 'TIMEOUT_ERROR',
  
  // 认证错误
  UNAUTHORIZED: 'UNAUTHORIZED',
  FORBIDDEN: 'FORBIDDEN',
  TOKEN_EXPIRED: 'TOKEN_EXPIRED',
  
  // 业务错误
  RULE_NOT_FOUND: 'RULE_NOT_FOUND',
  RULE_NAME_DUPLICATE: 'RULE_NAME_DUPLICATE',
  RULE_SYNTAX_ERROR: 'RULE_SYNTAX_ERROR',
  RULE_VALIDATION_ERROR: 'RULE_VALIDATION_ERROR',
  CONTAINER_SYNC_FAILED: 'CONTAINER_SYNC_FAILED',
  RULE_EXECUTION_FAILED: 'RULE_EXECUTION_FAILED',
  
  // 数据错误
  INVALID_REQUEST: 'INVALID_REQUEST',
  MISSING_REQUIRED_FIELD: 'MISSING_REQUIRED_FIELD',
  INVALID_DATA_FORMAT: 'INVALID_DATA_FORMAT',
  DATA_CONFLICT: 'DATA_CONFLICT',
  
  // 系统错误
  SERVER_ERROR: 'SERVER_ERROR',
  DATABASE_ERROR: 'DATABASE_ERROR',
  SERVICE_UNAVAILABLE: 'SERVICE_UNAVAILABLE'
} as const

/**
 * HTTP状态码常量
 */
export const HTTP_STATUS_CODES = {
  // 成功响应
  OK: 200,
  CREATED: 201,
  ACCEPTED: 202,
  NO_CONTENT: 204,
  
  // 重定向
  MOVED_PERMANENTLY: 301,
  FOUND: 302,
  NOT_MODIFIED: 304,
  
  // 客户端错误
  BAD_REQUEST: 400,
  UNAUTHORIZED: 401,
  FORBIDDEN: 403,
  NOT_FOUND: 404,
  METHOD_NOT_ALLOWED: 405,
  NOT_ACCEPTABLE: 406,
  CONFLICT: 409,
  GONE: 410,
  UNPROCESSABLE_ENTITY: 422,
  TOO_MANY_REQUESTS: 429,
  
  // 服务器错误
  INTERNAL_SERVER_ERROR: 500,
  NOT_IMPLEMENTED: 501,
  BAD_GATEWAY: 502,
  SERVICE_UNAVAILABLE: 503,
  GATEWAY_TIMEOUT: 504
} as const

/**
 * 请求方法常量
 */
export const HTTP_METHODS = {
  GET: 'GET',
  POST: 'POST',
  PUT: 'PUT',
  DELETE: 'DELETE',
  PATCH: 'PATCH',
  HEAD: 'HEAD',
  OPTIONS: 'OPTIONS'
} as const

/**
 * API版本配置
 */
export const API_VERSIONS = {
  V1: 'v1',
  V2: 'v2',
  CURRENT: 'v1'
} as const

/**
 * 环境特定的API配置
 */
export const ENVIRONMENT_CONFIG = {
  development: {
    enableMocking: false,
    enableLogging: true,
    enableDebug: true,
    mockDelay: 1000
  },
  production: {
    enableMocking: false,
    enableLogging: false,
    enableDebug: false,
    mockDelay: 0
  },
  test: {
    enableMocking: true,
    enableLogging: false,
    enableDebug: false,
    mockDelay: 0
  }
} as const

/**
 * 获取当前环境配置
 */
export const getCurrentEnvironmentConfig = () => {
  const env = process.env.NODE_ENV as keyof typeof ENVIRONMENT_CONFIG || 'development'
  return ENVIRONMENT_CONFIG[env] || ENVIRONMENT_CONFIG.development
}