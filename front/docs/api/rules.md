# 规则管理 API

规则管理 API 提供了对 Drools 规则的完整 CRUD 操作，包括创建、查询、更新和删除规则。

## 获取规则列表

### 请求

```http
GET /api/rules
```

### 查询参数

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| `page` | number | ❌ | 0 | 页码（从 0 开始） |
| `size` | number | ❌ | 20 | 每页大小（最大 100） |
| `sort` | string | ❌ | createTime | 排序字段 |
| `direction` | string | ❌ | DESC | 排序方向（ASC/DESC） |
| `name` | string | ❌ | - | 按名称模糊搜索 |
| `enabled` | boolean | ❌ | - | 按状态筛选 |
| `version` | string | ❌ | - | 按版本筛选 |

### 响应

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "content": [
      {
        "id": 1,
        "ruleName": "VIP用户折扣规则",
        "ruleContent": "rule \"VIP用户折扣规则\"\nwhen\n    $customer : Customer(vip == true)\n    $order : Order(customerId == $customer.id, amount > 100)\nthen\n    $order.setDiscount(0.15);\n    update($order);\nend",
        "description": "为 VIP 用户提供 15% 的订单折扣",
        "enabled": true,
        "version": "1.2",
        "createTime": "2025-01-27T10:00:00.000Z",
        "updateTime": "2025-01-27T11:30:00.000Z",
        "createdBy": "admin",
        "updatedBy": "admin"
      }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "size": 20,
    "number": 0,
    "first": true,
    "last": true,
    "empty": false
  },
  "timestamp": "2025-01-27T12:00:00.000Z",
  "path": "/api/rules"
}
```

### 示例请求

```bash
# 获取第一页规则，每页 10 条
curl -X GET "http://localhost:8080/api/rules?page=0&size=10" \
  -H "Authorization: Bearer your-jwt-token"

# 搜索包含 "折扣" 的规则
curl -X GET "http://localhost:8080/api/rules?name=折扣" \
  -H "Authorization: Bearer your-jwt-token"

# 获取已启用的规则
curl -X GET "http://localhost:8080/api/rules?enabled=true" \
  -H "Authorization: Bearer your-jwt-token"
```

## 获取单个规则

### 请求

```http
GET /api/rules/{id}
```

### 路径参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `id` | number | ✅ | 规则 ID |

### 响应

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "id": 1,
    "ruleName": "VIP用户折扣规则",
    "ruleContent": "rule \"VIP用户折扣规则\"\nwhen\n    $customer : Customer(vip == true)\n    $order : Order(customerId == $customer.id, amount > 100)\nthen\n    $order.setDiscount(0.15);\n    update($order);\nend",
    "description": "为 VIP 用户提供 15% 的订单折扣",
    "enabled": true,
    "version": "1.2",
    "createTime": "2025-01-27T10:00:00.000Z",
    "updateTime": "2025-01-27T11:30:00.000Z",
    "createdBy": "admin",
    "updatedBy": "admin"
  },
  "timestamp": "2025-01-27T12:00:00.000Z",
  "path": "/api/rules/1"
}
```

### 错误响应

```json
{
  "code": 404,
  "message": "规则不存在",
  "timestamp": "2025-01-27T12:00:00.000Z",
  "path": "/api/rules/999"
}
```

### 示例请求

```bash
curl -X GET "http://localhost:8080/api/rules/1" \
  -H "Authorization: Bearer your-jwt-token"
```

## 创建规则

### 请求

```http
POST /api/rules
```

### 请求体

```json
{
  "ruleName": "新用户欢迎规则",
  "ruleContent": "rule \"新用户欢迎规则\"\nwhen\n    $customer : Customer(registrationDate > \"2025-01-01\", firstOrder == true)\nthen\n    $customer.setWelcomeBonus(50);\n    update($customer);\nend",
  "description": "为新注册用户提供欢迎奖励",
  "enabled": true,
  "version": "1.0"
}
```

### 请求体参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `ruleName` | string | ✅ | 规则名称（全局唯一，1-255 字符） |
| `ruleContent` | string | ✅ | 规则内容（DRL 格式，最大 100KB） |
| `description` | string | ❌ | 规则描述（最大 1000 字符） |
| `enabled` | boolean | ❌ | 是否启用（默认 true） |
| `version` | string | ❌ | 版本号（默认 1.0） |

### 响应

```json
{
  "code": 201,
  "message": "创建成功",
  "data": {
    "id": 2,
    "ruleName": "新用户欢迎规则",
    "ruleContent": "rule \"新用户欢迎规则\"\nwhen\n    $customer : Customer(registrationDate > \"2025-01-01\", firstOrder == true)\nthen\n    $customer.setWelcomeBonus(50);\n    update($customer);\nend",
    "description": "为新注册用户提供欢迎奖励",
    "enabled": true,
    "version": "1.0",
    "createTime": "2025-01-27T12:00:00.000Z",
    "updateTime": "2025-01-27T12:00:00.000Z",
    "createdBy": "admin",
    "updatedBy": "admin"
  },
  "timestamp": "2025-01-27T12:00:00.000Z",
  "path": "/api/rules"
}
```

### 错误响应

```json
{
  "code": 409,
  "message": "规则名称已存在",
  "errors": [
    {
      "type": "BUSINESS_ERROR",
      "code": "RULE_NAME_EXISTS",
      "message": "规则名称 '新用户欢迎规则' 已存在",
      "field": "ruleName",
      "value": "新用户欢迎规则"
    }
  ],
  "timestamp": "2025-01-27T12:00:00.000Z",
  "path": "/api/rules"
}
```

### 示例请求

```bash
curl -X POST "http://localhost:8080/api/rules" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your-jwt-token" \
  -d '{
    "ruleName": "新用户欢迎规则",
    "ruleContent": "rule \"新用户欢迎规则\"\nwhen\n    $customer : Customer(registrationDate > \"2025-01-01\", firstOrder == true)\nthen\n    $customer.setWelcomeBonus(50);\n    update($customer);\nend",
    "description": "为新注册用户提供欢迎奖励",
    "enabled": true
  }'
```

## 更新规则

### 请求

```http
PUT /api/rules/{id}
```

### 路径参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `id` | number | ✅ | 规则 ID |

### 请求体

```json
{
  "ruleName": "新用户欢迎规则",
  "ruleContent": "rule \"新用户欢迎规则\"\nwhen\n    $customer : Customer(registrationDate > \"2025-01-01\", firstOrder == true)\n    $order : Order(customerId == $customer.id)\nthen\n    $customer.setWelcomeBonus(100);\n    $order.setDiscount(0.1);\n    update($customer);\n    update($order);\nend",
  "description": "为新注册用户提供欢迎奖励和首单折扣",
  "enabled": true,
  "version": "1.1"
}
```

### 响应

```json
{
  "code": 200,
  "message": "更新成功",
  "data": {
    "id": 2,
    "ruleName": "新用户欢迎规则",
    "ruleContent": "rule \"新用户欢迎规则\"\nwhen\n    $customer : Customer(registrationDate > \"2025-01-01\", firstOrder == true)\n    $order : Order(customerId == $customer.id)\nthen\n    $customer.setWelcomeBonus(100);\n    $order.setDiscount(0.1);\n    update($customer);\n    update($order);\nend",
    "description": "为新注册用户提供欢迎奖励和首单折扣",
    "enabled": true,
    "version": "1.1",
    "createTime": "2025-01-27T12:00:00.000Z",
    "updateTime": "2025-01-27T12:30:00.000Z",
    "createdBy": "admin",
    "updatedBy": "admin"
  },
  "timestamp": "2025-01-27T12:30:00.000Z",
  "path": "/api/rules/2"
}
```

### 示例请求

```bash
curl -X PUT "http://localhost:8080/api/rules/2" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your-jwt-token" \
  -d '{
    "ruleName": "新用户欢迎规则",
    "ruleContent": "rule \"新用户欢迎规则\"\nwhen\n    $customer : Customer(registrationDate > \"2025-01-01\", firstOrder == true)\n    $order : Order(customerId == $customer.id)\nthen\n    $customer.setWelcomeBonus(100);\n    $order.setDiscount(0.1);\n    update($customer);\n    update($order);\nend",
    "description": "为新注册用户提供欢迎奖励和首单折扣",
    "version": "1.1"
  }'
```

## 删除规则

### 请求

```http
DELETE /api/rules/{id}
```

### 路径参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `id` | number | ✅ | 规则 ID |

### 查询参数

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| `force` | boolean | ❌ | false | 是否强制删除（物理删除） |

### 响应

```json
{
  "code": 200,
  "message": "删除成功",
  "timestamp": "2025-01-27T12:45:00.000Z",
  "path": "/api/rules/2"
}
```

### 示例请求

```bash
# 软删除（推荐）
curl -X DELETE "http://localhost:8080/api/rules/2" \
  -H "Authorization: Bearer your-jwt-token"

# 强制删除（物理删除）
curl -X DELETE "http://localhost:8080/api/rules/2?force=true" \
  -H "Authorization: Bearer your-jwt-token"
```

## 批量操作

### 批量创建规则

```http
POST /api/rules/batch
```

### 请求体

```json
{
  "operations": [
    {
      "operation": "CREATE",
      "data": {
        "ruleName": "规则1",
        "ruleContent": "rule \"规则1\"...",
        "description": "第一个规则"
      }
    },
    {
      "operation": "CREATE",
      "data": {
        "ruleName": "规则2",
        "ruleContent": "rule \"规则2\"...",
        "description": "第二个规则"
      }
    }
  ]
}
```

### 响应

```json
{
  "code": 200,
  "message": "批量操作完成",
  "data": {
    "results": [
      {
        "success": true,
        "data": {
          "id": 3,
          "ruleName": "规则1",
          "ruleContent": "rule \"规则1\"...",
          "description": "第一个规则",
          "enabled": true,
          "version": "1.0",
          "createTime": "2025-01-27T13:00:00.000Z",
          "updateTime": "2025-01-27T13:00:00.000Z"
        },
        "index": 0
      },
      {
        "success": false,
        "error": "规则名称已存在",
        "index": 1
      }
    ],
    "successCount": 1,
    "failureCount": 1
  },
  "timestamp": "2025-01-27T13:00:00.000Z",
  "path": "/api/rules/batch"
}
```

## 规则状态管理

### 启用/禁用规则

```http
PATCH /api/rules/{id}/status
```

### 请求体

```json
{
  "enabled": false
}
```

### 响应

```json
{
  "code": 200,
  "message": "状态更新成功",
  "data": {
    "id": 1,
    "enabled": false,
    "updateTime": "2025-01-27T13:15:00.000Z"
  },
  "timestamp": "2025-01-27T13:15:00.000Z",
  "path": "/api/rules/1/status"
}
```

### 批量状态更新

```http
PATCH /api/rules/status
```

### 请求体

```json
{
  "ruleIds": [1, 2, 3],
  "enabled": true
}
```

## 规则版本管理

### 获取规则版本历史

```http
GET /api/rules/{id}/versions
```

### 响应

```json
{
  "code": 200,
  "message": "获取成功",
  "data": [
    {
      "id": "v1.2",
      "ruleId": 1,
      "version": "1.2",
      "content": "rule \"VIP用户折扣规则\"...",
      "description": "增加了最小订单金额限制",
      "createdAt": "2025-01-27T11:30:00.000Z",
      "createdBy": "admin"
    },
    {
      "id": "v1.1",
      "ruleId": 1,
      "version": "1.1",
      "content": "rule \"VIP用户折扣规则\"...",
      "description": "调整了折扣比例",
      "createdAt": "2025-01-27T10:30:00.000Z",
      "createdBy": "admin"
    }
  ],
  "timestamp": "2025-01-27T13:30:00.000Z",
  "path": "/api/rules/1/versions"
}
```

### 回滚到指定版本

```http
POST /api/rules/{id}/rollback
```

### 请求体

```json
{
  "version": "1.1"
}
```

## 规则导入导出

### 导出规则

```http
GET /api/rules/export
```

### 查询参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `format` | string | ❌ | 导出格式（json/xml/drl），默认 json |
| `ruleIds` | number[] | ❌ | 指定规则 ID，不指定则导出全部 |

### 响应

```json
{
  "code": 200,
  "message": "导出成功",
  "data": {
    "format": "json",
    "exportTime": "2025-01-27T14:00:00.000Z",
    "rules": [
      {
        "ruleName": "VIP用户折扣规则",
        "ruleContent": "rule \"VIP用户折扣规则\"...",
        "description": "为 VIP 用户提供 15% 的订单折扣",
        "version": "1.2"
      }
    ]
  },
  "timestamp": "2025-01-27T14:00:00.000Z",
  "path": "/api/rules/export"
}
```

### 导入规则

```http
POST /api/rules/import
```

### 请求体

```json
{
  "format": "json",
  "overwrite": false,
  "rules": [
    {
      "ruleName": "导入的规则",
      "ruleContent": "rule \"导入的规则\"...",
      "description": "从外部系统导入的规则",
      "version": "1.0"
    }
  ]
}
```

## 错误处理

### 常见错误码

| 错误码 | HTTP 状态 | 说明 | 解决方案 |
|--------|-----------|------|----------|
| `RULE_NAME_EXISTS` | 409 | 规则名称已存在 | 使用不同的规则名称 |
| `RULE_NOT_FOUND` | 404 | 规则不存在 | 检查规则 ID 是否正确 |
| `INVALID_DRL_SYNTAX` | 422 | DRL 语法错误 | 检查规则语法 |
| `RULE_NAME_EMPTY` | 400 | 规则名称为空 | 提供有效的规则名称 |
| `RULE_CONTENT_TOO_LARGE` | 413 | 规则内容过大 | 减少规则内容大小 |

### 语法验证错误

当规则内容包含语法错误时，API 会返回详细的错误信息：

```json
{
  "code": 422,
  "message": "规则语法验证失败",
  "errors": [
    {
      "type": "DROOLS_ERROR",
      "code": "SYNTAX_ERROR",
      "message": "第 5 行第 25 列：缺少右括号",
      "line": 5,
      "column": 25,
      "suggestion": "请检查括号是否匹配"
    },
    {
      "type": "DROOLS_ERROR",
      "code": "UNKNOWN_VARIABLE",
      "message": "第 8 行：未定义的变量 '$unknownVar'",
      "line": 8,
      "suggestion": "请确保变量已正确绑定"
    }
  ],
  "timestamp": "2025-01-27T14:30:00.000Z",
  "path": "/api/rules"
}
```

## 下一步

了解了规则管理 API 后，你可以继续学习：

- 🧪 [测试执行 API](./testing.md) - 学习如何测试规则
- ✅ [语法验证 API](./validation.md) - 了解规则语法验证