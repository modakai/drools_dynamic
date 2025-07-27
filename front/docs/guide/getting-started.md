# 快速开始

本指南将帮助你在几分钟内搭建并运行 Drools 动态规则管理系统。

## 环境要求

在开始之前，请确保你的开发环境满足以下要求：

| 软件 | 版本要求 | 说明 |
|------|----------|------|
| **Java** | 17+ | 后端运行环境 |
| **Node.js** | 18+ | 前端构建环境 |
| **MySQL** | 8.0+ | 数据存储 |
| **Maven** | 3.6+ | 后端构建工具 |

## 安装步骤

### 1. 获取源码

```bash
# 克隆项目仓库
git clone https://github.com/modakai/drools_dynamic.git
cd drools-dynamic-rules
```

### 2. 数据库准备

创建 MySQL 数据库并执行初始化脚本：

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

-- 插入示例数据
INSERT INTO drools_rule (rule_name, rule_content, description) VALUES 
('示例规则', 'package com.example.rules\n\nrule "示例规则"\nwhen\n    $person : Person(age > 18)\nthen\n    $person.setAdult(true);\nend', '这是一个示例规则，用于演示系统功能');
```

### 3. 后端配置

编辑后端配置文件：

```bash
cd backend/src/main/resources
```

修改 `application.yml`：

```yaml
server:
  port: 8080

spring:
  application:
    name: drools-dynamic-rules
  
  datasource:
    url: jdbc:mysql://localhost:3306/drools_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username  # 替换为你的数据库用户名
    password: your_password  # 替换为你的数据库密码
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  profiles:
    active: dev

mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0

logging:
  level:
    com.example.drools: DEBUG
```

### 4. 启动后端服务

```bash
cd backend

# 使用 Maven 启动
./mvnw spring-boot:run

# 或者使用 IDE 运行 DroolsDynamicRulesApplication.java
```

启动成功后，你应该看到类似的日志输出：

```
2025-01-27 10:00:00.000  INFO 12345 --- [main] c.e.d.DroolsDynamicRulesApplication : Started DroolsDynamicRulesApplication in 3.456 seconds
```

后端服务将在 `http://localhost:8080` 启动。

### 5. 前端配置和启动

打开新的终端窗口：

```bash
cd front

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

启动成功后，你应该看到：

```
  VITE v5.0.0  ready in 1234 ms

  ➜  Local:   http://localhost:3000/
  ➜  Network: use --host to expose
```

前端应用将在 `http://localhost:3000` 启动。

### 6. 启动文档站点

如果你想查看完整的文档：

```bash
# 在前端目录下启动文档
npm run docs:dev
```

文档站点将在 `http://localhost:5173` 启动。

## 验证安装

### 1. 检查后端 API

在浏览器中访问：`http://localhost:8080/api/rules`

你应该看到 JSON 格式的规则列表响应。

### 2. 访问前端界面

在浏览器中访问：`http://localhost:3000`

你应该看到规则管理系统的主界面。

### 3. 访问文档站点

在浏览器中访问：`http://localhost:5173`

你应该看到完整的项目文档。

### 4. 测试基本功能

1. **查看规则列表**：在主界面应该能看到示例规则
2. **创建新规则**：点击"创建规则"按钮测试规则创建功能
3. **编辑规则**：点击规则列表中的编辑按钮测试编辑功能

## 常见问题

### 后端启动失败

**问题**：`java.sql.SQLException: Access denied for user`

**解决方案**：
- 检查数据库连接配置
- 确认 MySQL 服务已启动
- 验证用户名和密码是否正确

### 前端启动失败

**问题**：`Error: Cannot find module`

**解决方案**：
```bash
# 清除缓存并重新安装
rm -rf node_modules package-lock.json
npm install
```

### 端口冲突

**问题**：`Port 8080 is already in use`

**解决方案**：
- 修改 `application.yml` 中的端口配置
- 或者停止占用端口的其他服务

## 下一步

恭喜！你已经成功搭建了 Drools 动态规则管理系统。接下来你可以：

- 📖 阅读[用户指南](./rule-management.md)了解如何使用系统
- 🔧 查看[开发指南](./development.md)了解如何扩展功能
- 🚀 参考[部署指南](./deployment.md)了解生产环境部署

## 获取帮助

如果在安装过程中遇到问题：

- 📋 查看[故障排除指南](./troubleshooting.md)
- 💬 在 GitHub 上[提交 Issue](https://github.com/your-username/drools-dynamic-rules/issues)
- 📧 联系项目维护者