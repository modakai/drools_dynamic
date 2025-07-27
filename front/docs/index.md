---
layout: home

hero:
  name: "Drools 动态规则管理系统"
  text: "智能业务规则管理平台"
  tagline: 基于 Drools 规则引擎，支持通过 Web 界面动态创建、编辑、测试和管理业务规则
  actions:
    - theme: brand
      text: 快速开始
      link: /guide/getting-started
features:
  - icon: 🎯
    title: 动态规则管理
    details: 支持规则的增删改查，实时同步到 Drools 容器，无需重启应用即可生效
  - icon: 💻
    title: 专业代码编辑
    details: 集成 Monaco Editor，提供 Drools DRL 语法高亮、智能提示和错误检查
  - icon: 🧪
    title: 规则测试执行
    details: 支持自定义测试数据，实时验证规则执行结果和业务逻辑
  - icon: 📊
    title: 版本管理
    details: 完整的规则版本控制和历史记录，支持规则回滚和对比
  - icon: 🔧
    title: 状态管理
    details: 灵活的规则启用/禁用状态切换，支持规则分组和批量操作
  - icon: 🚀
    title: 高性能架构
    details: 前后端分离设计，RESTful API，响应式界面，支持大规模规则管理
---

## 技术栈

<div class="tech-stack">
  <div class="tech-category">
    <h3>🔧 后端技术</h3>
    <ul>
      <li><strong>Spring Boot 3.x</strong> - 现代化 Java 框架</li>
      <li><strong>MyBatis Plus</strong> - 高效的 ORM 框架</li>
      <li><strong>Drools 7.x</strong> - 强大的规则引擎</li>
      <li><strong>MySQL 8.0+</strong> - 可靠的数据存储</li>
    </ul>
  </div>
  
  <div class="tech-category">
    <h3>🎨 前端技术</h3>
    <ul>
      <li><strong>Vue 3 + TypeScript</strong> - 现代化前端框架</li>
      <li><strong>Ant Design Vue</strong> - 企业级 UI 组件库</li>
      <li><strong>Monaco Editor</strong> - 专业代码编辑器</li>
      <li><strong>Vite</strong> - 极速构建工具</li>
    </ul>
  </div>
</div>

## 快速预览

```bash
# 克隆项目
git clone https://github.com/modakai/drools_dynamic.git
cd drools-dynamic-rules

# 启动后端服务
cd backend
./mvnw spring-boot:run

# 启动前端服务
cd ../front
npm install
npm run dev
```

访问 `http://localhost:3000` 开始使用！

<style>
.tech-stack {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 2rem;
  margin: 2rem 0;
}

.tech-category {
  background: var(--vp-c-bg-soft);
  padding: 1.5rem;
  border-radius: 8px;
  border: 1px solid var(--vp-c-divider);
}

.tech-category h3 {
  margin-top: 0;
  color: var(--vp-c-brand-1);
}

.tech-category ul {
  margin: 0;
  padding-left: 1rem;
}

.tech-category li {
  margin: 0.5rem 0;
}

@media (max-width: 768px) {
  .tech-stack {
    grid-template-columns: 1fr;
  }
}
</style>