# API 文档

Drools 动态规则管理系统提供了完整的 RESTful API，支持规则的增删改查、测试执行和语法验证等功能。

## API 概览

### 基础信息

- **Base URL**: `http://localhost:8080/api`
- **Content-Type**: `application/json`
- **字符编码**: `UTF-8`
- **API 版本**: `v1`

### 认证方式

目前系统支持以下认证方式：

```http
# JWT Token 认证（推荐）
Authorization: Bearer <your-jwt-token>

# API Key 认证
X-API-Key: <your-api-key>
```

### 响应格式

所有 API 响应都遵循统一的格式：

```typescript
interface ApiResponse<T> {
  code: number;           // 状态码
  message: string;        // 响应消息
  data?: T;              // 响应数据
  timestamp: string;      // 时间戳
  path: string;          // 请求路径
}
```

#### 成功响应示例
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "ruleName": "示例规则",
    "ruleContent": "rule \"示例规则\"...",
    "enabled": true
  },
  "timestamp": "2025-01-27T10:00:00.000Z",
  "path": "/api/rules/1"
}
```

#### 错误响应示例
```json
{
  "code": 400,
  "message": "规则名称不能为空",
  "timestamp": "2025-01-27T10:00:00.000Z",
  "path": "/api/rules"
}
```

## 状态码说明

| 状态码 | 说明 | 示例场景 |
|--------|------|----------|
| 200 | 请求成功 | 获取规则列表成功 |
| 201 | 创建成功 | 新建规则成功 |
| 400 | 请求参数错误 | 规则名称为空 |
| 401 | 未授权 | Token 无效或过期 |
| 403 | 禁止访问 | 无权限操作规则 |
| 404 | 资源不存在 | 规则 ID 不存在 |
| 409 | 资源冲突 | 规则名称已存在 |
| 422 | 语法错误 | DRL 语法不正确 |
| 500 | 服务器内部错误 | 数据库连接失败 |

## 数据模型

### Rule 规则模型

```typescript
interface Rule {
  id: number;                    // 规则 ID
  ruleName: string;              // 规则名称（唯一）
  ruleContent: string;           // 规则内容（DRL 格式）
  description?: string;          // 规则描述
  enabled: boolean;              // 是否启用
  version: string;               // 版本号
  createTime: string;            // 创建时间（ISO 8601）
  updateTime: string;            // 更新时间（ISO 8601）
  createdBy?: string;            // 创建者
  updatedBy?: string;            // 最后更新者
}
```

### RuleTest 测试模型

```typescript
interface RuleTest {
  id: number;                    // 测试 ID
  ruleId: number;                // 关联的规则 ID
  testName: string;              // 测试名称
  inputData: string;             // 测试输入数据（JSON 格式）
  expectedOutput?: string;       // 期望输出
  actualOutput?: string;         // 实际输出
  status: 'PENDING' | 'PASSED' | 'FAILED'; // 测试状态
  executionTime: number;         // 执行时间（毫秒）
  createTime: string;            // 创建时间
}
```

### ValidationResult 验证结果模型

```typescript
interface ValidationResult {
  valid: boolean;                // 是否有效
  errors: ValidationError[];     // 错误列表
  warnings: ValidationWarning[]; // 警告列表
}

interface ValidationError {
  line: number;                  // 错误行号
  column: number;                // 错误列号
  message: string;               // 错误消息
  severity: 'ERROR' | 'WARNING'; // 严重程度
}
```

## 分页和排序

### 分页参数

```typescript
interface PageRequest {
  page: number;      // 页码（从 0 开始）
  size: number;      // 每页大小（默认 20，最大 100）
  sort?: string;     // 排序字段
  direction?: 'ASC' | 'DESC'; // 排序方向
}
```

### 分页响应

```typescript
interface PageResponse<T> {
  content: T[];           // 当前页数据
  totalElements: number;  // 总记录数
  totalPages: number;     // 总页数
  size: number;          // 每页大小
  number: number;        // 当前页码
  first: boolean;        // 是否首页
  last: boolean;         // 是否末页
  empty: boolean;        // 是否为空
}
```

### 使用示例

```http
GET /api/rules?page=0&size=10&sort=createTime&direction=DESC
```

## 错误处理

### 错误类型

系统定义了以下错误类型：

```typescript
enum ErrorType {
  VALIDATION_ERROR = 'VALIDATION_ERROR',     // 参数验证错误
  BUSINESS_ERROR = 'BUSINESS_ERROR',         // 业务逻辑错误
  SYSTEM_ERROR = 'SYSTEM_ERROR',             // 系统错误
  DROOLS_ERROR = 'DROOLS_ERROR'              // Drools 引擎错误
}
```

### 详细错误信息

```typescript
interface ErrorDetail {
  type: ErrorType;
  code: string;
  message: string;
  field?: string;        // 错误字段
  value?: any;           // 错误值
  suggestion?: string;   // 修复建议
}
```

### 错误响应示例

```json
{
  "code": 422,
  "message": "规则语法验证失败",
  "errors": [
    {
      "type": "DROOLS_ERROR",
      "code": "SYNTAX_ERROR",
      "message": "第 5 行缺少右括号",
      "field": "ruleContent",
      "line": 5,
      "column": 25,
      "suggestion": "请检查括号是否匹配"
    }
  ],
  "timestamp": "2025-01-27T10:00:00.000Z",
  "path": "/api/rules/validate"
}
```

## 限流和配额

### 请求限制

为了保护系统稳定性，API 实施了以下限制：

| 限制类型 | 限制值 | 说明 |
|----------|--------|------|
| 每分钟请求数 | 100 | 单个 IP 每分钟最多 100 次请求 |
| 并发连接数 | 10 | 单个 IP 最多 10 个并发连接 |
| 请求体大小 | 1MB | 单个请求体最大 1MB |
| 规则内容大小 | 100KB | 单个规则内容最大 100KB |

### 限流响应

当触发限流时，API 会返回 429 状态码：

```json
{
  "code": 429,
  "message": "请求过于频繁，请稍后再试",
  "retryAfter": 60,
  "timestamp": "2025-01-27T10:00:00.000Z",
  "path": "/api/rules"
}
```

## 批量操作

### 批量请求格式

```typescript
interface BatchRequest<T> {
  operations: BatchOperation<T>[];
}

interface BatchOperation<T> {
  operation: 'CREATE' | 'UPDATE' | 'DELETE';
  data: T;
  id?: number;  // UPDATE 和 DELETE 操作需要
}
```

### 批量响应格式

```typescript
interface BatchResponse<T> {
  results: BatchResult<T>[];
  successCount: number;
  failureCount: number;
}

interface BatchResult<T> {
  success: boolean;
  data?: T;
  error?: string;
  index: number;  // 对应请求中的索引
}
```

## WebSocket 支持

系统提供 WebSocket 连接用于实时通知：

### 连接地址
```
ws://localhost:8080/ws/notifications
```

### 消息格式

```typescript
interface WebSocketMessage {
  type: 'RULE_CREATED' | 'RULE_UPDATED' | 'RULE_DELETED' | 'RULE_TESTED';
  data: any;
  timestamp: string;
  userId?: string;
}
```

### 使用示例

```javascript
const ws = new WebSocket('ws://localhost:8080/ws/notifications');

ws.onmessage = (event) => {
  const message = JSON.parse(event.data);
  console.log('收到通知:', message);
  
  if (message.type === 'RULE_UPDATED') {
    // 处理规则更新通知
    refreshRuleList();
  }
};
```

## SDK 和客户端库

### JavaScript/TypeScript SDK

```bash
npm install @drools/dynamic-rules-sdk
```

```typescript
import { DroolsClient } from '@drools/dynamic-rules-sdk';

const client = new DroolsClient({
  baseURL: 'http://localhost:8080/api',
  apiKey: 'your-api-key'
});

// 获取规则列表
const rules = await client.rules.list();

// 创建规则
const newRule = await client.rules.create({
  ruleName: '新规则',
  ruleContent: 'rule "新规则"...',
  description: '这是一个新规则'
});
```

### Java SDK

```xml
<dependency>
  <groupId>com.example</groupId>
  <artifactId>drools-dynamic-rules-client</artifactId>
  <version>1.0.0</version>
</dependency>
```

```java
DroolsClient client = DroolsClient.builder()
    .baseUrl("http://localhost:8080/api")
    .apiKey("your-api-key")
    .build();

// 获取规则列表
List<Rule> rules = client.rules().list();

// 创建规则
Rule newRule = client.rules().create(Rule.builder()
    .ruleName("新规则")
    .ruleContent("rule \"新规则\"...")
    .description("这是一个新规则")
    .build());
```

## API 版本管理

### 版本策略

- **URL 版本控制**：`/api/v1/rules`
- **向后兼容**：旧版本 API 至少保持 6 个月的兼容性
- **弃用通知**：通过响应头通知 API 弃用信息

### 版本响应头

```http
API-Version: v1
API-Deprecated: false
API-Sunset: 2025-12-31T23:59:59Z
```

## 下一步

了解了 API 基础信息后，你可以查看具体的 API 端点文档：

- 📋 [规则管理 API](./rules.md)
- 🧪 [测试执行 API](./testing.md)
- ✅ [语法验证 API](./validation.md)