# API服务层文档

## 概述

API服务层提供了与后端API交互的完整解决方案，包括类型安全的请求方法、错误处理、请求重试和响应验证。

## 核心组件

### RuleService

规则管理的主要API服务类，提供所有规则相关的操作。

#### 主要功能

1. **CRUD操作**
   - `getAllRules()` - 获取所有规则
   - `getRulesPaginated(params)` - 分页获取规则
   - `getRuleById(id)` - 根据ID获取规则
   - `createRule(request)` - 创建新规则
   - `updateRule(id, request)` - 更新规则
   - `deleteRule(id)` - 删除单个规则
   - `deleteRules(ids)` - 批量删除规则

2. **规则管理**
   - `toggleRuleStatus(id, enabled)` - 启用/禁用规则
   - `validateRule(ruleContent)` - 验证规则语法
   - `testRule(request)` - 测试规则执行

3. **数据导入导出**
   - `exportRules(ids?)` - 导出规则
   - `importRules(file)` - 导入规则

4. **统计信息**
   - `getRuleStatistics()` - 获取规则统计信息

#### 使用示例

```typescript
import { ruleService } from '@/services'

// 获取所有规则
const rules = await ruleService.getAllRules()

// 创建新规则
const newRule = await ruleService.createRule({
  ruleName: 'Test Rule',
  ruleContent: 'rule "Test" when then end',
  description: 'Test description',
  enabled: true
})

// 测试规则
const testResult = await ruleService.testRule({
  ruleContent: 'rule "Test" when $data: TestData() then end',
  testData: { value: 100 }
})
```

### HTTP客户端

基于Axios的HTTP客户端，提供了请求/响应拦截器、错误处理和重试机制。

#### 特性

1. **请求拦截器**
   - 自动添加认证头
   - 请求日志记录
   - 加载状态管理
   - 请求ID追踪

2. **响应拦截器**
   - 统一错误处理
   - 响应时间记录
   - 业务错误检测
   - 自动重试机制

3. **错误处理**
   - 网络错误处理
   - HTTP状态码错误处理
   - 业务逻辑错误处理
   - 用户友好的错误消息

#### 配置

```typescript
// HTTP客户端配置
const http = axios.create({
  baseURL: '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
})
```

### 错误处理

统一的API错误处理机制，提供标准化的错误格式和处理流程。

#### 错误类型

```typescript
enum ApiErrorType {
  NETWORK_ERROR = 'NETWORK_ERROR',
  TIMEOUT_ERROR = 'TIMEOUT_ERROR',
  VALIDATION_ERROR = 'VALIDATION_ERROR',
  BUSINESS_ERROR = 'BUSINESS_ERROR',
  SERVER_ERROR = 'SERVER_ERROR',
  UNKNOWN_ERROR = 'UNKNOWN_ERROR'
}
```

#### 使用示例

```typescript
import { handleApiError, safeApiCall } from '@/utils/apiErrorHandler'

// 手动错误处理
try {
  const result = await apiCall()
} catch (error) {
  handleApiError(error, {
    showMessage: true,
    logError: true,
    throwError: false
  })
}

// 安全API调用
const result = await safeApiCall(
  () => ruleService.getAllRules(),
  { showMessage: false }
)
```

## 类型安全

所有API方法都提供完整的TypeScript类型支持，包括：

### 请求类型

```typescript
interface CreateRuleRequest {
  ruleName: string
  ruleContent: string
  description?: string
  enabled: boolean
}

interface UpdateRuleRequest extends CreateRuleRequest {
  version: string
}

interface TestRuleRequest {
  ruleContent?: string
  ruleIds?: number[]
  testData: Record<string, any>
}
```

### 响应类型

```typescript
interface ApiResponse<T> {
  success: boolean
  data: T
  message?: string
  code?: string
  timestamp?: string
}

interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  page: number
  size: number
  first: boolean
  last: boolean
}
```

### 实体类型

```typescript
interface DroolsRule {
  id?: number
  ruleName: string
  ruleContent: string
  description?: string
  enabled: boolean
  createTime?: string
  updateTime?: string
  version?: string
}
```

## 验证机制

### 请求验证

所有API方法在发送请求前都会进行参数验证：

```typescript
// 验证规则ID
if (!isValidId(id)) {
  throw new Error('Invalid rule ID')
}

// 验证创建请求
if (!isValidCreateRuleRequest(request)) {
  throw new Error('Invalid create rule request')
}
```

### 响应验证

API响应会进行格式验证和类型检查：

```typescript
// 验证API响应格式
if (!isApiResponse<DroolsRule[]>(response.data)) {
  throw new Error('Invalid API response format')
}

// 验证业务成功状态
if (!response.data.success) {
  throw new Error(response.data.message || ERROR_MESSAGES.SERVER_ERROR)
}
```

## 配置管理

### API端点配置

```typescript
export const API_ENDPOINTS = {
  RULES: '/rules',
  RULE_BY_ID: (id: number) => `/rules/${id}`,
  RULE_STATUS: (id: number) => `/rules/${id}/status`,
  RULE_VALIDATE: '/rules/validate',
  RULE_TEST: '/rules/test',
  RULE_STATISTICS: '/rules/statistics',
  RULE_EXPORT: '/rules/export',
  RULE_IMPORT: '/rules/import'
} as const
```

### 错误消息配置

```typescript
export const ERROR_MESSAGES = {
  RULE_SYNTAX_ERROR: '规则语法错误，请检查规则内容',
  RULE_NAME_DUPLICATE: '规则名称已存在，请使用其他名称',
  CONTAINER_SYNC_FAILED: '规则容器同步失败，请重试',
  RULE_EXECUTION_FAILED: '规则执行失败，请检查测试数据',
  NETWORK_ERROR: '网络连接失败，请检查网络',
  SERVER_ERROR: '服务器错误，请稍后重试'
} as const
```

## 测试



## 最佳实践

### 1. 错误处理

```typescript
// 推荐：使用统一的错误处理
try {
  const result = await ruleService.createRule(request)
  message.success('规则创建成功')
  return result
} catch (error) {
  // 错误已经在拦截器中处理，这里只需要处理业务逻辑
  console.error('Create rule failed:', error)
  return null
}
```

### 2. 类型安全

```typescript
// 推荐：使用类型守卫进行运行时检查
const rules = await ruleService.getAllRules()
const validRules = rules.filter(isValidRule)
```

### 3. 异步操作

```typescript
// 推荐：使用async/await而不是Promise链
const createRuleAsync = async (request: CreateRuleRequest) => {
  try {
    const rule = await ruleService.createRule(request)
    await ruleService.getRuleStatistics() // 刷新统计信息
    return rule
  } catch (error) {
    handleApiError(error)
    throw error
  }
}
```

### 4. 资源清理

```typescript
// 推荐：在组件卸载时取消未完成的请求
import { ref, onUnmounted } from 'vue'

const abortController = ref<AbortController>()

const fetchRules = async () => {
  abortController.value = new AbortController()
  try {
    const rules = await ruleService.getAllRules()
    return rules
  } catch (error) {
    if (error.name !== 'AbortError') {
      handleApiError(error)
    }
  }
}

onUnmounted(() => {
  abortController.value?.abort()
})
```

## 扩展指南

### 添加新的API方法

1. 在相应的服务类中添加方法
2. 添加必要的类型定义
3. 实现参数验证
4. 添加错误处理
5. 编写单元测试
6. 更新文档

### 自定义错误处理

```typescript
// 创建自定义错误处理器
const customErrorHandler = (error: StandardApiError) => {
  if (error.code === 'RULE_SYNTAX_ERROR') {
    // 自定义语法错误处理
    showSyntaxErrorDialog(error.details)
  }
}

// 使用自定义错误处理器
await ruleService.validateRule(content, {
  customHandler: customErrorHandler
})
```

这个API服务层提供了完整的类型安全、错误处理和测试覆盖，为前端应用提供了可靠的数据访问基础。