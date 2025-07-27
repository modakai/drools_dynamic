// 状态管理相关类型定义

import type { DroolsRule, TestResult, RuleStatistics } from './rule'
import type { OperationStatus } from './common'

/**
 * 规则状态类型
 */
export interface RuleState {
  // 规则列表
  rules: DroolsRule[]
  
  // 当前选中的规则
  currentRule: DroolsRule | null
  
  // 加载状态
  loading: boolean
  
  // 操作状态
  operationStatus: OperationStatus
  
  // 错误信息
  error: string | null
  
  // 分页信息
  pagination: {
    current: number
    pageSize: number
    total: number
  }
  
  // 搜索参数
  searchParams: {
    ruleName?: string
    enabled?: boolean
    createTimeStart?: string
    createTimeEnd?: string
  }
  
  // 统计信息
  statistics: RuleStatistics | null
}

/**
 * 测试状态类型
 */
export interface TestState {
  // 测试结果
  result: TestResult | null
  
  // 测试历史
  history: TestResult[]
  
  // 当前测试的规则
  testingRule: DroolsRule | null
  
  // 测试数据
  testData: Record<string, any>
  
  // 测试状态
  testing: boolean
  
  // 错误信息
  error: string | null
}

/**
 * 应用状态类型
 */
export interface AppState {
  // 应用标题
  title: string
  
  // 当前路由
  currentRoute: string
  
  // 侧边栏折叠状态
  sidebarCollapsed: boolean
  
  // 主题设置
  theme: 'light' | 'dark'
  
  // 语言设置
  locale: 'zh-CN' | 'en-US'
  
  // 全局加载状态
  globalLoading: boolean
  
  // 全局错误信息
  globalError: string | null
  
  // 用户偏好设置
  preferences: {
    pageSize: number
    autoSave: boolean
    codeTheme: string
    showLineNumbers: boolean
  }
}

/**
 * 根状态类型
 */
export interface RootState {
  rule: RuleState
  test: TestState
  app: AppState
}

/**
 * 状态更新动作类型
 */
export interface StateAction<T = any> {
  type: string
  payload?: T
}

/**
 * 异步动作类型
 */
export interface AsyncAction<T = any> {
  type: string
  payload?: T
  meta?: {
    loading?: boolean
    error?: string
  }
}

/**
 * 状态变更监听器类型
 */
export interface StateListener<T = any> {
  (state: T, prevState: T): void
}

/**
 * 状态中间件类型
 */
export interface StateMiddleware {
  (action: StateAction, state: RootState, next: (action: StateAction) => void): void
}

/**
 * 状态持久化配置类型
 */
export interface PersistConfig {
  key: string
  storage: Storage
  whitelist?: string[]
  blacklist?: string[]
  transforms?: Array<{
    in: (state: any) => any
    out: (state: any) => any
  }>
}

/**
 * 状态快照类型
 */
export interface StateSnapshot {
  timestamp: number
  state: RootState
  action?: StateAction
}

/**
 * 状态历史类型
 */
export interface StateHistory {
  past: StateSnapshot[]
  present: StateSnapshot
  future: StateSnapshot[]
}

/**
 * 状态管理器配置类型
 */
export interface StoreConfig {
  initialState?: Partial<RootState>
  middleware?: StateMiddleware[]
  persist?: PersistConfig
  devtools?: boolean
  strict?: boolean
}