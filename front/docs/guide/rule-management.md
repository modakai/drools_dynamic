# 规则管理

规则管理是系统的核心功能，允许用户通过 Web 界面对 Drools 规则进行完整的生命周期管理。

## 规则列表

### 查看规则

在主界面中，你可以看到所有已创建的规则列表，包括：

- **规则名称**：规则的唯一标识符
- **描述**：规则的功能说明
- **状态**：启用/禁用状态
- **版本**：当前规则版本
- **创建时间**：规则创建的时间戳
- **最后更新**：规则最后修改的时间

### 规则筛选和搜索

系统提供多种方式来快速找到目标规则：

```typescript
// 支持的筛选条件
interface RuleFilter {
  name?: string;        // 按名称搜索
  status?: boolean;     // 按状态筛选
  version?: string;     // 按版本筛选
  dateRange?: [Date, Date]; // 按时间范围筛选
}
```

**使用方法**：
1. 在搜索框中输入规则名称关键字
2. 使用状态筛选器选择启用/禁用的规则
3. 选择时间范围查看特定时期创建的规则

## 创建规则

### 基本信息

创建新规则时需要填写以下基本信息：

| 字段 | 必填 | 说明 |
|------|------|------|
| 规则名称 | ✅ | 全局唯一，建议使用有意义的命名 |
| 描述 | ❌ | 规则的功能说明，便于团队协作 |
| 版本 | ❌ | 默认为 1.0，支持语义化版本号 |
| 状态 | ❌ | 默认启用，可选择创建后立即生效 |

### 规则内容编写

规则内容使用 Drools DRL (Drools Rule Language) 语法编写：

```drools
package com.example.rules

import com.example.model.Person
import com.example.model.Order

rule "成人用户折扣规则"
    when
        $person : Person(age >= 18)
        $order : Order(customerId == $person.id, amount > 100)
    then
        $order.setDiscount(0.1); // 10% 折扣
        update($order);
        System.out.println("应用成人用户折扣: " + $person.getName());
end
```

### 规则模板

系统提供常用的规则模板，帮助快速创建规则：

#### 条件判断模板
```drools
rule "条件判断模板"
    when
        $obj : YourObject(condition == true)
    then
        // 执行动作
        $obj.setResult("满足条件");
        update($obj);
end
```

#### 数值比较模板
```drools
rule "数值比较模板"
    when
        $obj : YourObject(value > 100)
    then
        $obj.setLevel("高");
        update($obj);
end
```

#### 集合操作模板
```drools
rule "集合操作模板"
    when
        $list : List(size > 0)
        $item : Object() from $list
    then
        // 处理集合中的每个元素
        System.out.println("处理项目: " + $item);
end
```

## 编辑规则

### 在线编辑

点击规则列表中的"编辑"按钮，进入规则编辑界面：

1. **代码编辑器**：使用 Monaco Editor 提供专业的编辑体验
2. **语法高亮**：自动识别 DRL 语法并高亮显示
3. **实时验证**：编辑过程中实时检查语法错误
4. **自动保存**：支持自动保存草稿，防止意外丢失

### 版本管理

每次保存规则时，系统会自动创建新版本：

```typescript
interface RuleVersion {
  id: string;
  ruleId: string;
  version: string;
  content: string;
  description?: string;
  createdAt: Date;
  createdBy: string;
}
```

**版本操作**：
- **查看历史**：查看规则的所有历史版本
- **版本对比**：对比不同版本之间的差异
- **版本回滚**：将规则回滚到指定的历史版本

### 协作编辑

系统支持多用户协作编辑：

- **编辑锁定**：当有用户正在编辑时，其他用户只能查看
- **变更通知**：规则被修改时通知相关用户
- **审批流程**：可配置规则变更需要审批才能生效

## 删除规则

### 软删除机制

系统采用软删除机制，确保数据安全：

1. **标记删除**：规则被标记为已删除，但数据仍保留
2. **停止执行**：已删除的规则立即停止在规则引擎中执行
3. **恢复功能**：管理员可以恢复误删的规则

### 删除确认

删除规则时需要确认操作：

```typescript
interface DeleteConfirmation {
  ruleName: string;
  affectedSystems: string[];  // 受影响的系统
  backupCreated: boolean;     // 是否已创建备份
  userConfirmed: boolean;     // 用户确认
}
```

### 批量操作

支持批量删除规则：

1. 选择多个规则
2. 点击"批量删除"按钮
3. 确认删除操作
4. 系统自动处理所有选中的规则

## 规则状态管理

### 状态类型

规则支持以下状态：

| 状态 | 说明 | 图标 |
|------|------|------|
| 启用 | 规则正常执行 | 🟢 |
| 禁用 | 规则暂停执行 | 🔴 |
| 草稿 | 规则尚未发布 | 🟡 |
| 已删除 | 规则已被删除 | ⚫ |

### 状态切换

**启用规则**：
```typescript
async function enableRule(ruleId: string): Promise<void> {
  await ruleService.updateRuleStatus(ruleId, true);
  // 规则立即在 Drools 容器中生效
}
```

**禁用规则**：
```typescript
async function disableRule(ruleId: string): Promise<void> {
  await ruleService.updateRuleStatus(ruleId, false);
  // 规则立即在 Drools 容器中停止执行
}
```

### 批量状态管理

支持批量启用或禁用规则：

1. 选择多个规则
2. 选择目标状态（启用/禁用）
3. 确认操作
4. 系统批量更新规则状态

## 最佳实践

### 命名规范

建议采用以下命名规范：

```
[业务域]_[功能]_[条件]_规则

示例：
- 订单_折扣_VIP用户_规则
- 风控_限额_单日交易_规则
- 审批_自动通过_小额申请_规则
```

### 规则组织

1. **按业务域分组**：将相关业务的规则放在同一分组
2. **版本控制**：使用语义化版本号管理规则版本
3. **文档完善**：为每个规则添加详细的描述和使用说明
4. **定期清理**：定期清理不再使用的规则

### 性能优化

1. **条件优化**：将最可能为 false 的条件放在前面
2. **避免复杂计算**：在规则中避免复杂的计算逻辑
3. **合理使用索引**：为经常查询的字段添加数据库索引
4. **监控性能**：定期监控规则执行性能

## 下一步

了解了规则管理的基本操作后，你可以：

- 🧪 掌握[规则测试](./rule-testing.md)的方法
- 📊 查看[Monaco Editor](./monaco-editor.md)的详细功能