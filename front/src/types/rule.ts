// TypeScript type definitions for rules

/**
 * Drools规则实体类型
 */
export interface DroolsRule {
  id?: number
  ruleName: string
  ruleContent: string
  description?: string
  enabled: boolean
  createTime?: string
  updateTime?: string
  version?: string
}

/**
 * 创建规则请求类型
 */
export interface CreateRuleRequest {
  ruleName: string
  ruleContent: string
  description?: string
  enabled: boolean
}

/**
 * 更新规则请求类型
 */
export interface UpdateRuleRequest extends CreateRuleRequest {
  version: string
}

/**
 * 规则 Fact 配置类型
 */
export interface RuleFactConfig {
  factName: string        // 事实名称（变量名，如$student）
  className: string       // 全类名（如com.sakura.drools.domain.Student）
  fieldValues: Record<string, any> // 字段名和值的映射
}

/**
 * 规则测试请求类型
 */
export interface TestRuleRequest {
  ruleContent?: string
  ruleIds?: number[]
  testData?: Record<string, any>
  ruleConfigs?: RuleFactConfig[]
  verbose?: boolean
  options?: {
    showExecutionDetails?: boolean
    saveToHistory?: boolean
    validateInput?: boolean
  }
}

/**
 * 规则测试结果类型
 */
export interface TestResult {
  success: boolean
  executionTime: number
  firedRules?: Array<{
    ruleName: string
    description?: string
  }>
  resultData: Record<string, any>
  firedRulesCount: number
  errorMessage?: string
  errorDetails?: string
  executionDetails?: {
    logs: string
    facts: Record<string, any>
  }
}

/**
 * 规则验证结果类型
 */
export interface ValidationResult {
  valid: boolean
  errors: ValidationError[]
  errorMessage: string
  warningMessage: string
}

/**
 * 验证错误类型
 */
export interface ValidationError {
  field: string
  message: string
  code?: string
}

/**
 * API响应基础类型
 */
export interface ApiResponse<T> {
  success: boolean
  data: T
  message?: string
  code?: string
  timestamp?: string
}

/**
 * 分页请求参数类型
 */
export interface PageRequest {
  page: number
  size: number
  sort?: string
  order?: 'asc' | 'desc'
}

/**
 * 分页响应类型
 */
export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  page: number
  size: number
  first: boolean
  last: boolean
}

/**
 * 规则查询参数类型
 */
export interface RuleQueryParams extends PageRequest {
  ruleName?: string
  enabled?: boolean
  createTimeStart?: string
  createTimeEnd?: string
}

/**
 * 规则统计信息类型
 */
export interface RuleStatistics {
  totalRules: number
  enabledRules: number
  disabledRules: number
  recentlyCreated: number
  recentlyUpdated: number
}

/**
 * 测试历史项类型
 */
export interface TestHistoryItem {
  id: number
  ruleIds?: number[]
  testData: Record<string, any>
  success: boolean
  executionTime: number
  firedRules?: Array<{
    ruleName: string
    description?: string
  }>
  resultData: Record<string, any>
  errorMessage?: string
  errorDetails?: string
  executionDetails?: {
    logs: string
    facts: Record<string, any>
  }
  createTime: string
}