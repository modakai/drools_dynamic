# Drools 动态规则管理系统

基于 Drools 规则引擎的动态规则管理平台，支持通过 Web 界面创建、编辑、测试和管理业务规则，无需重启应用即可动态更新规则。

## ✨ 功能特性

### 🎯 核心功能
- **动态规则管理**: 支持规则的增删改查，实时同步到 Drools 容器
- **专业代码编辑**: 集成 Monaco Editor，提供 Drools DRL 语法高亮和智能提示
- **规则语法验证**: 实时语法检查，支持后端验证和错误提示
- **规则测试执行**: 支持自定义测试数据，验证规则执行结果
- **规则版本管理**: 支持规则版本控制和历史记录
- **状态管理**: 支持规则启用/禁用状态切换

### 🛠️ 技术特性
- **前后端分离**: RESTful API 设计，支持跨域访问
- **响应式设计**: 适配桌面和移动端设备
- **TypeScript 支持**: 完整的类型定义，提升开发体验
- **实时同步**: 数据库变更实时同步到 Drools 规则容器

## 🏗️ 技术栈

### 后端技术栈
- **框架**: Spring Boot 3.x
- **ORM**: MyBatis Plus
- **规则引擎**: Drools 7.x
- **数据库**: MySQL 8.0+
- **语言**: Java 17+
- **构建工具**: Maven

### 前端技术栈
- **框架**: Vue 3 + Composition API
- **语言**: TypeScript
- **UI 库**: Ant Design Vue 4.x
- **代码编辑器**: Monaco Editor
- **状态管理**: Pinia
- **路由**: Vue Router 4.x
- **构建工具**: Vite
- **HTTP 客户端**: Axios

## 📁 项目结构

```
drools_db_demo/
├── README.md                  # 项目说明文档
│
├── backend/                   # Spring Boot 后端应用
│   ├── .gitignore            # 后端 Git 忽略配置
│   ├── .idea/                # 后端 IDEA 配置
│   ├── pom.xml               # Maven 项目配置
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/drools/
│   │   │   │   ├── config/           # 配置类
│   │   │   │   ├── controller/       # REST API 控制器
│   │   │   │   ├── dto/             # 数据传输对象
│   │   │   │   ├── entity/          # 数据库实体
│   │   │   │   ├── exception/       # 异常处理
│   │   │   │   ├── mapper/          # MyBatis 映射器
│   │   │   │   ├── repository/      # 数据访问层
│   │   │   │   ├── service/         # 业务逻辑服务
│   │   │   │   ├── utils/           # 工具类
│   │   │   │   └── DroolsDynamicRulesApplication.java  # 启动类
│   │   │   └── resources/
│   │   │       ├── db/              # 数据库脚本
│   │   │       ├── application.yml  # 主配置文件
│   │   │       ├── application-dev.yml   # 开发环境配置
│   │   │       ├── application-prod.yml  # 生产环境配置
│   │   │       └── d.drl            # 示例规则文件
│
└── front/                     # Vue 3 前端应用
    ├── .eslintrc.cjs         # ESLint 配置
    ├── .gitignore            # 前端 Git 忽略配置
    ├── env.d.ts              # 环境类型定义
    ├── index.html            # HTML 入口文件
    ├── package.json          # NPM 依赖配置
    ├── package-lock.json     # NPM 锁定文件
    ├── tsconfig.json         # TypeScript 配置
    ├── vite.config.ts        # Vite 构建配置
    ├── public/               # 静态资源
    └── src/
        ├── components/       # 可复用组件
        │   ├── Layout/       # 布局组件
        │   ├── CodeEditor.vue        # 代码编辑器
        │   ├── MonacoCodeEditor.vue  # Monaco 编辑器
        │   ├── RuleForm.vue          # 规则表单
        │   ├── RuleList.vue          # 规则列表
        │   └── package-info.ts       # 组件包信息
        ├── composables/      # Vue 组合式函数
        │   ├── useConfirm.ts         # 确认对话框
        │   ├── useLoading.ts         # 加载状态
        │   └── index.ts              # 导出文件
        ├── config/           # 配置文件
        │   ├── api.ts                # API 配置
        │   └── env.ts                # 环境配置
        ├── constants/        # 常量定义
        │   └── api.ts                # API 常量
        ├── router/           # 路由配置
        │   └── index.ts              # 路由定义
        ├── services/         # API 服务
        │   ├── ruleService.ts        # 规则服务
        │   ├── index.ts              # 服务导出
        │   └── README.md             # 服务说明
        ├── types/            # TypeScript 类型定义
        │   ├── api.ts                # API 类型
        │   ├── common.ts             # 通用类型
        │   ├── components.ts         # 组件类型
        │   ├── rule.ts               # 规则类型
        │   ├── store.ts              # 状态类型
        │   └── index.ts              # 类型导出
        ├── utils/            # 工具函数
        │   ├── apiErrorHandler.ts    # API 错误处理
        │   ├── date.ts               # 日期工具
        │   ├── http.ts               # HTTP 工具
        │   ├── typeGuards.ts         # 类型守卫
        │   ├── typeValidation.ts     # 类型验证
        │   ├── validation.ts         # 表单验证
        │   └── index.ts              # 工具导出
        ├── views/            # 页面组件
        │   ├── RuleManagement.vue    # 规则管理页面
        │   ├── RuleEditor.vue        # 规则编辑页面
        │   └── RuleTest.vue          # 规则测试页面
        ├── App.vue           # 根组件
        └── main.ts           # 应用入口
```

## 🚀 快速开始

### 环境要求
- **Java**: 17+
- **Node.js**: 16+
- **MySQL**: 8.0+
- **Maven**: 3.6+

### 1. 数据库准备

```sql
-- 创建数据库
CREATE DATABASE drools_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE drools_db;

-- 创建规则表
CREATE TABLE drools_rule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rule_name VARCHAR(255) NOT NULL UNIQUE COMMENT '规则名称',
    rule_content TEXT NOT NULL COMMENT '规则内容',
    description TEXT COMMENT '规则描述',
    enabled BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    version VARCHAR(50) DEFAULT '1.0' COMMENT '版本号',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='Drools规则表';
```

### 2. 后端启动

```bash
# 进入后端目录
cd drools_db_demo/backend

# 修改数据库配置 (src/main/resources/application.yml)
# 配置你的 MySQL 连接信息

# 启动应用
./mvnw spring-boot:run

# 或者使用 IDE 运行 DroolsApplication.java
```

后端服务将在 `http://localhost:8080` 启动

### 3. 前端启动

```bash
# 进入前端目录
cd drools_db_demo/front

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端应用将在 `http://localhost:3000` 启动

### 4. 访问应用

打开浏览器访问 `http://localhost:3000`，即可开始使用规则管理系统。

## 📖 使用指南

### 规则管理
1. **创建规则**: 点击"创建规则"按钮，使用 Monaco Editor 编写 Drools DRL 规则
2. **编辑规则**: 在规则列表中点击编辑按钮，修改现有规则
3. **删除规则**: 在规则列表中点击删除按钮，确认后删除规则
4. **启用/禁用**: 点击状态标签切换规则的启用状态

### 代码编辑
- **语法高亮**: 自动识别 Drools DRL 语法并高亮显示
- **智能提示**: 按 `Ctrl+Space` 触发关键字自动完成
- **代码格式化**: 按 `Shift+Alt+F` 或点击格式化按钮
- **语法验证**: 实时检查语法错误并显示错误位置

### 规则测试
1. 选择要测试的规则
2. 配置测试数据（JSON 格式）
3. 点击"执行测试"查看结果
4. 查看规则执行日志和输出结果

## 🔧 开发指南

### 后端开发

#### API 接口
- `GET /api/rules` - 获取规则列表
- `POST /api/rules` - 创建新规则
- `PUT /api/rules/{id}` - 更新规则
- `DELETE /api/rules/{id}` - 删除规则
- `POST /api/rules/validate` - 验证规则语法
- `POST /api/rules/test` - 测试规则执行

#### 核心服务
- `DroolsRuleService` - 规则管理服务
- `DroolsContainerService` - Drools 容器管理
- `DroolsRuleTestService` - 规则测试服务

### 前端开发

#### 核心组件
- `MonacoCodeEditor` - Monaco 代码编辑器组件
- `RuleList` - 规则列表组件
- `RuleEditor` - 规则编辑页面
- `RuleTest` - 规则测试页面

#### 开发命令
```bash
# 开发服务器
npm run dev

# 生产构建
npm run build

# 预览构建结果
npm run preview

# 代码检查
npm run lint
```

## 🎨 Monaco Editor 功能

### 语法高亮支持
- **关键字**: `rule`, `when`, `then`, `end`, `package`, `import` 等
- **操作符**: `and`, `or`, `not`, `exists`, `forall` 等
- **比较操作符**: `==`, `!=`, `<`, `>`, `<=`, `>=`, `=~`, `!~`
- **变量绑定**: `$variable` 形式的变量
- **字符串和数字**: 支持字符串和数字高亮
- **注释**: 单行 `//` 和多行 `/* */` 注释

### 智能功能
- **自动完成**: 关键字和代码片段自动完成
- **代码片段**: `rule-template`、`package-import` 等模板
- **错误提示**: 实时显示语法错误
- **代码格式化**: 自动缩进和格式化
- **多光标编辑**: 支持多光标同时编辑

### 快捷键
- `Ctrl+S` - 保存
- `Ctrl+Space` - 自动完成
- `Shift+Alt+F` - 代码格式化
- `Ctrl+/` - 切换注释
- `Ctrl+F` - 查找
- `Ctrl+H` - 替换
- `Alt+Click` - 多光标编辑

## 🔍 故障排除

### 常见问题

1. **后端启动失败**
   - 检查 Java 版本是否为 17+
   - 确认 MySQL 服务已启动
   - 验证数据库连接配置

2. **前端启动失败**
   - 检查 Node.js 版本是否为 16+
   - 清除 node_modules 重新安装: `rm -rf node_modules && npm install`
   - 检查端口 3000 是否被占用

3. **规则语法错误**
   - 确保规则语法符合 Drools DRL 规范
   - 检查包名和导入语句是否正确
   - 使用语法验证功能检查错误

4. **规则测试失败**
   - 确认测试数据格式正确
   - 检查规则逻辑是否符合预期
   - 查看测试日志获取详细错误信息

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 🤝 贡献

欢迎提交 Issue 和 Pull Request 来改进项目。

## 📞 联系方式

如有问题或建议，请通过以下方式联系：
- 提交 GitHub Issue
- 发送邮件至项目维护者

---

**Drools 动态规则管理系统** - 让业务规则管理更简单、更高效！