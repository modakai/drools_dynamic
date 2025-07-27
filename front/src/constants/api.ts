// API endpoints constants
export const API_ENDPOINTS = {
  // 规则管理相关端点
  RULES: '/rules',
  RULE_BY_ID: (id: number) => `/rules/${id}`,
  RULE_STATUS: (id: number) => `/rules/${id}/status`,
  RULE_VALIDATE: '/rules/validate',
  RULE_TEST: '/rules/test',
  RULE_STATISTICS: '/rules/statistics',
  RULE_EXPORT: '/rules/export',
  RULE_IMPORT: '/rules/import',
  
  // 系统相关端点
  HEALTH: '/health',
  VERSION: '/version',
  
  // 认证相关端点（预留）
  AUTH_LOGIN: '/auth/login',
  AUTH_LOGOUT: '/auth/logout',
  AUTH_REFRESH: '/auth/refresh',
  
  // 用户相关端点（预留）
  USER_PROFILE: '/user/profile',
  USER_PREFERENCES: '/user/preferences'
} as const

// HTTP status codes
export const HTTP_STATUS = {
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
  CONFLICT: 409,
  UNPROCESSABLE_ENTITY: 422,
  TOO_MANY_REQUESTS: 429,
  
  // 服务器错误
  INTERNAL_SERVER_ERROR: 500,
  NOT_IMPLEMENTED: 501,
  BAD_GATEWAY: 502,
  SERVICE_UNAVAILABLE: 503,
  GATEWAY_TIMEOUT: 504
} as const

// Error messages
export const ERROR_MESSAGES = {
  // 规则相关错误
  RULE_SYNTAX_ERROR: '规则语法错误，请检查规则内容',
  RULE_NAME_DUPLICATE: '规则名称已存在，请使用其他名称',
  RULE_NOT_FOUND: '规则不存在或已被删除',
  RULE_VALIDATION_ERROR: '规则验证失败',
  RULE_EXECUTION_FAILED: '规则执行失败，请检查测试数据',
  RULE_IMPORT_FAILED: '规则导入失败，请检查文件格式',
  RULE_EXPORT_FAILED: '规则导出失败，请稍后重试',
  
  // 容器相关错误
  CONTAINER_SYNC_FAILED: '规则容器同步失败，请重试',
  CONTAINER_INIT_FAILED: '规则容器初始化失败',
  CONTAINER_UPDATE_FAILED: '规则容器更新失败',
  
  // 网络相关错误
  NETWORK_ERROR: '网络连接失败，请检查网络',
  TIMEOUT_ERROR: '请求超时，请稍后重试',
  CONNECTION_ERROR: '连接服务器失败',
  
  // 服务器相关错误
  SERVER_ERROR: '服务器错误，请稍后重试',
  SERVICE_UNAVAILABLE: '服务暂时不可用，请稍后重试',
  DATABASE_ERROR: '数据库操作失败',
  
  // 认证相关错误
  UNAUTHORIZED: '未授权访问，请先登录',
  FORBIDDEN: '权限不足，无法访问该资源',
  TOKEN_EXPIRED: '登录已过期，请重新登录',
  
  // 数据相关错误
  INVALID_REQUEST: '请求参数无效',
  MISSING_REQUIRED_FIELD: '缺少必填字段',
  INVALID_DATA_FORMAT: '数据格式不正确',
  DATA_CONFLICT: '数据冲突，请刷新后重试',
  
  // 文件相关错误
  FILE_TOO_LARGE: '文件大小超出限制',
  INVALID_FILE_TYPE: '不支持的文件类型',
  FILE_UPLOAD_FAILED: '文件上传失败',
  
  // 通用错误
  UNKNOWN_ERROR: '未知错误',
  OPERATION_FAILED: '操作失败',
  SYSTEM_BUSY: '系统繁忙，请稍后重试'
} as const

// Success messages
export const SUCCESS_MESSAGES = {
  RULE_CREATED: '规则创建成功',
  RULE_UPDATED: '规则更新成功',
  RULE_DELETED: '规则删除成功',
  RULE_ENABLED: '规则已启用',
  RULE_DISABLED: '规则已禁用',
  RULE_VALIDATED: '规则验证通过',
  RULE_TEST_SUCCESS: '规则测试成功',
  RULE_EXPORTED: '规则导出成功',
  RULE_IMPORTED: '规则导入成功',
  OPERATION_SUCCESS: '操作成功'
} as const

// Loading messages
export const LOADING_MESSAGES = {
  LOADING_RULES: '正在加载规则列表...',
  CREATING_RULE: '正在创建规则...',
  UPDATING_RULE: '正在更新规则...',
  DELETING_RULE: '正在删除规则...',
  VALIDATING_RULE: '正在验证规则...',
  TESTING_RULE: '正在测试规则...',
  EXPORTING_RULES: '正在导出规则...',
  IMPORTING_RULES: '正在导入规则...',
  SYNCING_CONTAINER: '正在同步规则容器...'
} as const

// API request timeout values (in milliseconds)
export const TIMEOUT_VALUES = {
  DEFAULT: 30000,
  QUICK: 5000,
  LONG: 60000,
  UPLOAD: 120000,
  DOWNLOAD: 180000
} as const

// Pagination constants
export const PAGINATION = {
  DEFAULT_PAGE_SIZE: 20,
  PAGE_SIZE_OPTIONS: [10, 20, 50, 100],
  MAX_PAGE_SIZE: 100,
  MIN_PAGE_SIZE: 5
} as const

// File upload constants
export const FILE_UPLOAD = {
  MAX_SIZE: 10 * 1024 * 1024, // 10MB
  ALLOWED_TYPES: ['.drl', '.txt', '.json', '.xml'],
  CHUNK_SIZE: 1024 * 1024 // 1MB
} as const