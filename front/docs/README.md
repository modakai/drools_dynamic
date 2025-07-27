# Drools 动态规则管理系统文档

这是基于 VitePress 构建的 Drools 动态规则管理系统完整使用说明文档，集成在前端项目中。

## 📚 文档内容

### 🚀 快速开始
- [项目介绍](./guide/introduction.md) - 了解系统概述和核心价值
- [快速开始](./guide/getting-started.md) - 5分钟搭建运行环境

### 👥 用户指南
- [规则管理](./guide/rule-management.md) - 规则的增删改查操作
- [规则测试](./guide/rule-testing.md) - 完整的规则测试流程
- [Monaco Editor](./guide/monaco-editor.md) - 代码编辑器详细功能

### 📖 API 文档
- [API 概览](./api/index.md) - API 基础信息和规范
- [规则管理 API](./api/rules.md) - 规则 CRUD 操作接口

## 🛠️ 本地运行文档

### 环境要求
- Node.js 18+
- npm 或 yarn

### 快速启动

#### 方式一：使用启动脚本（推荐）
在前端项目根目录下，双击运行 `docs/start-docs.bat` 文件，或在命令行中执行：

```bash
# 在 front 目录下执行
docs/start-docs.bat
```

#### 方式二：手动启动
```bash
# 确保在 front 目录下
cd front

# 安装依赖（如果还没有安装）
npm install

# 启动文档开发服务器
npm run docs:dev

# 构建文档生产版本
npm run docs:build

# 预览文档构建结果
npm run docs:preview
```

### 访问地址
- 开发服务器：http://localhost:5173
- 构建预览：http://localhost:4173

## 📝 项目集成说明

### 集成结构
```
front/                          # Vue 3 前端项目
├── src/                        # 前端应用源码
├── docs/                       # VitePress 文档
│   ├── .vitepress/
│   │   └── config.ts          # VitePress 配置
│   ├── guide/                 # 用户指南
│   ├── api/                   # API 文档
│   ├── index.md              # 文档首页
│   ├── start-docs.bat        # 启动脚本
│   └── README.md             # 文档说明
├── package.json              # 包含文档相关脚本
└── ...
```

### 脚本配置

在 `front/package.json` 中已添加以下脚本：

```json
{
  "scripts": {
    "dev": "vite",                    // 启动前端应用
    "docs:dev": "vitepress dev docs", // 启动文档开发服务器
    "docs:build": "vitepress build docs", // 构建文档
    "docs:preview": "vitepress preview docs" // 预览文档
  }
}
```

### 依赖管理

VitePress 已作为开发依赖添加到项目中：

```json
{
  "devDependencies": {
    "vitepress": "^1.0.0-rc.31"
  }
}
```

## 🎨 文档特色

### 专业设计
- 响应式布局，支持桌面和移动端
- 深色/浅色主题切换
- 专业的代码高亮和语法支持

### 丰富内容
- 详细的 API 文档和示例
- 完整的用户操作指南
- 项目架构和开发指南
- 故障排除和最佳实践

### 开发友好
- TypeScript 类型定义
- Mermaid 图表支持
- 代码片段和模板
- 搜索和导航功能

## 🔧 自定义配置

### 修改配置
编辑 `docs/.vitepress/config.ts` 文件可以自定义：
- 网站标题和描述
- 导航菜单
- 侧边栏结构
- 主题颜色
- 社交链接

### 添加新页面
1. 在相应目录下创建 `.md` 文件
2. 在 `config.ts` 中添加导航配置
3. 更新侧边栏结构

### 导航配置示例
```typescript
// docs/.vitepress/config.ts
export default defineConfig({
  themeConfig: {
    nav: [
      { text: '首页', link: '/' },
      { text: '返回应用', link: '../' } // 返回前端应用
    ],
    sidebar: {
      '/guide/': [
        {
          text: '用户指南',
          items: [
            { text: '新页面', link: '/guide/new-page' }
          ]
        }
      ]
    }
  }
})
```

## 🚀 部署文档

### 与前端应用一起部署
```bash
# 构建前端应用
npm run build

# 构建文档
npm run docs:build

# 部署时包含两个构建输出
# - dist/          (前端应用)
# - docs/.vitepress/dist/  (文档站点)
```

### 独立部署文档
```bash
# 只构建文档
npm run docs:build

# 部署文档到 Web 服务器
cp -r docs/.vitepress/dist/* /var/www/html/docs/
```

### Nginx 配置示例
```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    # 前端应用
    location / {
        root /var/www/html/dist;
        try_files $uri $uri/ /index.html;
    }
    
    # 文档站点
    location /docs/ {
        alias /var/www/html/docs/;
        try_files $uri $uri/ /docs/index.html;
    }
}
```

## 📚 相关资源

### VitePress 官方文档
- [VitePress 官网](https://vitepress.dev/)
- [配置参考](https://vitepress.dev/reference/site-config)
- [主题定制](https://vitepress.dev/guide/custom-theme)

### Markdown 扩展
- [Markdown-it](https://markdown-it.github.io/) - Markdown 解析器
- [Mermaid](https://mermaid-js.github.io/) - 图表绘制
- [Shiki](https://shiki.matsu.io/) - 代码高亮

## 🤝 贡献指南

### 文档贡献
1. 在 `front/docs/` 目录下编辑或添加 Markdown 文件
2. 更新 `docs/.vitepress/config.ts` 中的导航配置
3. 本地测试：`npm run docs:dev`
4. 提交更改

### 问题反馈
- 通过 GitHub Issues 报告问题
- 提供详细的问题描述和复现步骤
- 建议改进方案

## 📄 许可证

本文档采用 MIT 许可证，详见项目根目录的 LICENSE 文件。

---

**让文档与应用完美集成，提供一站式开发体验！** 🚀