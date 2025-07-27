# Monaco Editor 功能详解

Monaco Editor 是 Visual Studio Code 的核心编辑器，我们将其集成到系统中，为 Drools DRL 规则编写提供专业的代码编辑体验。

## 编辑器特性

### 语法高亮

系统为 Drools DRL 语言提供了完整的语法高亮支持：

#### 关键字高亮
```drools
package com.example.rules    // package 关键字
import java.util.List        // import 关键字

rule "示例规则"              // rule 关键字
    when                     // when 关键字
        $person : Person(age > 18)
    then                     // then 关键字
        $person.setAdult(true);
end                          // end 关键字
```

#### 操作符高亮
```drools
when
    $order : Order(
        amount > 100,        // 比较操作符
        status == "PENDING", // 等于操作符
        customer != null     // 不等于操作符
    )
    and                      // 逻辑操作符
    $customer : Customer(
        vip == true
    )
    or                       // 逻辑操作符
    exists(                  // 存在操作符
        Discount(customerId == $customer.id)
    )
```

#### 变量和类型高亮
```drools
when
    $person : Person(        // 变量绑定高亮
        name : String,       // 类型高亮
        age : Integer        // 类型高亮
    )
    $orders : List()         // 集合类型高亮
```

### 智能提示

编辑器提供智能的代码自动完成功能：

#### 关键字提示
按 `Ctrl+Space` 触发自动完成，系统会提示：
- `rule` - 创建新规则
- `when` - 条件部分
- `then` - 动作部分
- `end` - 结束规则
- `and`, `or`, `not` - 逻辑操作符
- `exists`, `forall` - 量词操作符

#### 代码片段
系统提供预定义的代码片段：

**规则模板片段**：
```drools
rule "${1:规则名称}"
    when
        ${2:条件}
    then
        ${3:动作}
end
```

**条件判断片段**：
```drools
$${1:变量} : ${2:类型}(${3:条件})
```

**集合操作片段**：
```drools
$${1:item} : ${2:Type}() from ${3:collection}
```

### 错误检测

编辑器提供实时的语法错误检测：

#### 语法错误
```drools
rule "错误示例"
    when
        Person(age > 18
    then                     // 错误：缺少右括号
        // 动作
end
```

错误会以红色波浪线标出，鼠标悬停显示错误信息。

#### 警告提示
```drools
rule "警告示例"
    when
        $person : Person()
    then
        // 警告：变量 $person 未使用
        System.out.println("Hello");
end
```

警告会以黄色波浪线标出。

## 编辑器配置

### 主题设置

系统支持多种编辑器主题：

```typescript
interface EditorTheme {
  name: string;
  displayName: string;
  type: 'light' | 'dark';
}

const themes: EditorTheme[] = [
  { name: 'vs', displayName: '浅色主题', type: 'light' },
  { name: 'vs-dark', displayName: '深色主题', type: 'dark' },
  { name: 'hc-black', displayName: '高对比度', type: 'dark' }
];
```

### 编辑器选项

可以自定义编辑器的行为：

```typescript
const editorOptions = {
  // 基本设置
  fontSize: 14,
  fontFamily: 'Consolas, Monaco, monospace',
  lineHeight: 1.5,
  
  // 编辑行为
  wordWrap: 'on',
  automaticLayout: true,
  minimap: { enabled: true },
  
  // 代码折叠
  folding: true,
  foldingStrategy: 'indentation',
  
  // 行号和标尺
  lineNumbers: 'on',
  rulers: [80, 120],
  
  // 搜索和替换
  find: {
    autoFindInSelection: 'never',
    seedSearchStringFromSelection: 'always'
  }
};
```

## 高级功能

### 代码格式化

按 `Shift+Alt+F` 或点击格式化按钮自动格式化代码：

**格式化前**：
```drools
rule"示例规则"when $person:Person(age>18)then $person.setAdult(true);end
```

**格式化后**：
```drools
rule "示例规则"
    when
        $person : Person(age > 18)
    then
        $person.setAdult(true);
end
```

### 多光标编辑

支持多光标同时编辑：
- `Alt+Click` - 添加光标
- `Ctrl+Alt+Down` - 向下添加光标
- `Ctrl+Alt+Up` - 向上添加光标
- `Ctrl+D` - 选择下一个相同单词

### 查找和替换

强大的查找替换功能：

#### 基本查找
- `Ctrl+F` - 打开查找框
- `F3` / `Shift+F3` - 查找下一个/上一个
- `Ctrl+G` - 跳转到指定行

#### 高级替换
- `Ctrl+H` - 打开替换框
- 支持正则表达式匹配
- 支持大小写敏感搜索
- 支持全词匹配

### 代码折叠

支持代码块折叠：
```drools
rule "可折叠规则" ▼
    when
        $person : Person(age > 18)
    then
        $person.setAdult(true);
end

rule "另一个规则" ▶  // 已折叠
```

## 快捷键

### 编辑快捷键

| 快捷键 | 功能 |
|--------|------|
| `Ctrl+S` | 保存 |
| `Ctrl+Z` | 撤销 |
| `Ctrl+Y` | 重做 |
| `Ctrl+X` | 剪切 |
| `Ctrl+C` | 复制 |
| `Ctrl+V` | 粘贴 |
| `Ctrl+A` | 全选 |

### 导航快捷键

| 快捷键 | 功能 |
|--------|------|
| `Ctrl+G` | 跳转到行 |
| `Ctrl+P` | 快速打开文件 |
| `Ctrl+Shift+P` | 命令面板 |
| `F12` | 跳转到定义 |
| `Alt+F12` | 查看定义 |

### 选择快捷键

| 快捷键 | 功能 |
|--------|------|
| `Ctrl+L` | 选择当前行 |
| `Ctrl+D` | 选择当前单词 |
| `Ctrl+Shift+L` | 选择所有匹配项 |
| `Alt+Shift+I` | 在选择的每行末尾插入光标 |

## 自定义配置

### 语言配置

可以自定义 DRL 语言的配置：

```typescript
const drlLanguageConfig = {
  // 注释配置
  comments: {
    lineComment: '//',
    blockComment: ['/*', '*/']
  },
  
  // 括号配置
  brackets: [
    ['{', '}'],
    ['[', ']'],
    ['(', ')']
  ],
  
  // 自动闭合
  autoClosingPairs: [
    { open: '{', close: '}' },
    { open: '[', close: ']' },
    { open: '(', close: ')' },
    { open: '"', close: '"' },
    { open: "'", close: "'" }
  ],
  
  // 包围选择
  surroundingPairs: [
    { open: '{', close: '}' },
    { open: '[', close: ']' },
    { open: '(', close: ')' },
    { open: '"', close: '"' },
    { open: "'", close: "'" }
  ]
};
```

### 主题自定义

可以创建自定义主题：

```typescript
const customTheme = {
  base: 'vs-dark',
  inherit: true,
  rules: [
    { token: 'keyword.drools', foreground: '569cd6' },
    { token: 'string.drools', foreground: 'ce9178' },
    { token: 'comment.drools', foreground: '6a9955' },
    { token: 'variable.drools', foreground: '9cdcfe' }
  ],
  colors: {
    'editor.background': '#1e1e1e',
    'editor.foreground': '#d4d4d4',
    'editorLineNumber.foreground': '#858585'
  }
};
```

## 性能优化

### 大文件处理

对于大型规则文件，编辑器提供了优化：

```typescript
const performanceOptions = {
  // 虚拟化长文档
  enableVirtualization: true,
  
  // 延迟语法高亮
  deferredSyntaxHighlighting: true,
  
  // 限制建议数量
  maxSuggestions: 50,
  
  // 禁用不必要的功能
  minimap: { enabled: false },
  codeLens: false
};
```

### 内存管理

编辑器会自动管理内存：
- 自动释放未使用的模型
- 限制撤销历史记录数量
- 优化语法高亮缓存

## 扩展功能

### 自定义命令

可以添加自定义命令：

```typescript
editor.addCommand(monaco.KeyMod.CtrlCmd | monaco.KeyCode.KeyK, () => {
  // 自定义快捷键功能
  console.log('执行自定义命令');
});
```

### 自定义提示

可以添加自定义的智能提示：

```typescript
monaco.languages.registerCompletionItemProvider('drools', {
  provideCompletionItems: (model, position) => {
    return {
      suggestions: [
        {
          label: 'customRule',
          kind: monaco.languages.CompletionItemKind.Snippet,
          insertText: 'rule "${1:name}"\nwhen\n\t${2:condition}\nthen\n\t${3:action}\nend',
          insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
          documentation: '自定义规则模板'
        }
      ]
    };
  }
});
```

## 故障排除

### 常见问题

**编辑器无法加载**：
- 检查网络连接
- 确认 Monaco Editor 资源文件已正确加载
- 查看浏览器控制台错误信息

**语法高亮不工作**：
- 确认语言模式设置为 'drools'
- 检查主题配置是否正确
- 重新加载编辑器实例

**自动完成不触发**：
- 检查是否启用了智能提示
- 确认光标位置是否正确
- 尝试手动触发 `Ctrl+Space`

### 调试技巧

启用调试模式：

```typescript
const editor = monaco.editor.create(container, {
  ...options,
  // 启用调试
  debug: true,
  logLevel: monaco.LogLevel.Debug
});
```

## 下一步

掌握了 Monaco Editor 的使用后，你可以：

- 🧪 学习[规则测试](./rule-testing.md)功能
- 🔧 了解[项目结构](./project-structure.md)
- 🚀 查看[部署指南](./deployment.md)