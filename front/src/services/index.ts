// API服务统一导出

export { RuleService, ruleService } from './ruleService'

// 重新导出类型定义
export type {
  DroolsRule,
  CreateRuleRequest,
  UpdateRuleRequest,
  TestRuleRequest,
  TestResult,
  ValidationResult,
  ApiResponse,
  PageResponse,
  RuleQueryParams,
  RuleStatistics
} from '@/types/rule'

// 重新导出HTTP工具
export { default as http, showLoading, hideLoading, httpGet, httpPost, httpPut, httpDelete, httpPatch } from '@/utils/http'

// 重新导出常量
export { API_ENDPOINTS, ERROR_MESSAGES } from '@/constants/api'