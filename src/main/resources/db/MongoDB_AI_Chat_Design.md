# Bitinn AI 聊天存储设计 (MongoDB)

## 📋 设计概述

本文档定义 bitinn 项目 AI 助手功能的 MongoDB 数据存储方案。

---

## 🗄️ 数据库选择

| 存储类型 | 选用 | 原因 |
|---------|------|------|
| 结构化数据（用户、文章、互动） | MySQL | 事务支持、关联查询强 |
| 半结构化数据（聊天记录） | MongoDB | 灵活 schema、适合多轮对话 |

---

## 📁 MongoDB 集合设计

### 1. `conversations` - 对话会话集合

```javascript
{
  "_id": ObjectId,                    // MongoDB 自动生成 ID
  "userId": Number,                   // 关联的用户 ID (MySQL user.id)
  "title": String,                    // 对话标题（AI 生成或用户命名）
  "model": String,                    // 使用的 AI 模型
  "messageCount": Number,             // 消息总数
  "lastMessageAt": Date,              // 最后一条消息时间
  "tags": [String],                   // 对话标签，如 ["技术", "代码"]
  "isPinned": Boolean,                // 是否置顶
  "isDeleted": Boolean,               // 是否删除（软删除）
  "createdAt": Date,                  // 创建时间
  "updatedAt": Date                   // 更新时间
}
```

**索引设计：**
- `{ userId: 1, updatedAt: -1 }` - 用户会话列表，按更新时间倒序
- `{ userId: 1, isPinned: -1 }` - 置顶会话优先

---

### 2. `messages` - 聊天消息集合

```javascript
{
  "_id": ObjectId,                    // 消息 ID
  "conversationId": ObjectId,         // 所属对话 ID
  "userId": Number,                   // 所属用户 ID
  "role": String,                    // 角色: "user" | "assistant" | "system"
  "content": String,                  // 消息内容（Markdown）
  "rawContent": String,               // 原始内容（AI 返回的完整内容）

  // AI 相关字段
  "model": String,                    // 使用的模型
  "tokens": Number,                   // token 消耗
  "finishReason": String,             // 结束原因: "stop" | "length" | "content_filter"

  // 消息元数据
  "attachments": [                    // 附件（如有）
    {
      "type": String,                 // "image" | "file"
      "url": String,
      "name": String,
      "size": Number
    }
  ],
  "metadata": {                       // 扩展元数据
    "temperature": Number,
    "userAgent": String,
    "ip": String
  },

  // 反馈机制
  "rating": Number,                   // 用户评分: 1-5, null=未评分
  "feedback": String,                 // 用户反馈内容

  "isDeleted": Boolean,               // 软删除
  "createdAt": Date,                  // 创建时间
  "updatedAt": Date                   // 更新时间
}
```

**索引设计：**
- `{ conversationId: 1, createdAt: 1 }` - 对话内的消息，按时间正序
- `{ userId: 1, createdAt: -1 }` - 用户消息历史
- `{ conversationId: 1, role: 1 }` - 按角色筛选

---

### 3. `prompt_templates` - 提示词模板集合（可选扩展）

```javascript
{
  "_id": ObjectId,
  "name": String,                     // 模板名称
  "description": String,              // 模板描述
  "systemPrompt": String,             // 系统提示词
  "variables": [String],              // 变量列表
  "category": String,                 // 分类: "writing" | "code" | "analysis"
  "isPublic": Boolean,                // 是否公开
  "useCount": Number,                 // 使用次数
  "createdBy": Number,                // 创建者用户 ID
  "createdAt": Date,
  "updatedAt": Date
}
```

---

## 🔄 与 MySQL 的关联关系

```
MySQL (bitinn)
┌─────────────────────────────────────────────────────────────┐
│  user (id)  ───────────────┐                               │
│                             │  userId                      │
│                             ▼                               │
│  notification ──────────►  conversations (MongoDB)        │
│                             │                               │
│                             │ conversationId               │
│                             ▼                               │
│                             messages (MongoDB)              │
└─────────────────────────────────────────────────────────────┘
```

---

## 📊 查询示例

### 获取用户的会话列表（最近 20 条）
```javascript
db.conversations.find({ userId: 1, isDeleted: false })
  .sort({ isPinned: -1, updatedAt: -1 })
  .limit(20)
  .project({ title: 1, model: 1, messageCount: 1, lastMessageAt: 1, isPinned: 1 })
```

### 获取对话消息（带上下文，限制最近 20 条）
```javascript
db.messages.find({ conversationId: ObjectId("xxx"), isDeleted: false })
  .sort({ createdAt: -1 })
  .limit(20)
  .sort({ createdAt: 1 })  // 重排为正序
```

### 统计用户 AI 使用情况
```javascript
db.messages.aggregate([
  { $match: { userId: 1, role: "assistant" } },
  { $group: {
      _id: "$model",
      totalMessages: { $sum: 1 },
      totalTokens: { $sum: "$tokens" }
    }
  }
])
```

---

## ⚙️ Spring Boot 集成配置

### 1. 添加依赖 (pom.xml)
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```

### 2. 配置文件 (application.yml)
```yaml
spring:
  data:
    mongodb:
      host: ${MONGO_HOST:localhost}
      port: ${MONGO_PORT:27017}
      database: bitinn_ai
      authentication-database: admin
      username: ${MONGO_USERNAME:}
      password: ${MONGO_PASSWORD:}
```

### 3. 环境变量示例
```bash
# .env 或系统环境变量
MONGO_HOST=110.41.84.17
MONGO_PORT=27017
MONGO_DATABASE=bitinn_ai
MONGO_USERNAME=bitinn_ai
MONGO_PASSWORD=your_password_here
```

---

## 📝 下一步实施计划

1. **基础设施**：添加 MongoDB 依赖和配置
2. **实体类**：创建 Conversation、Message 实体
3. **Repository**：创建 MongoDB 数据访问层
4. **Service 层**：实现对话和消息服务
5. **Controller 层**：提供 REST API 给前端
6. **AI 集成**：接入 AI 模型（DeepSeek/OpenAI 等）

---

## 🔒 数据安全建议

1. **用户数据隔离**：所有查询必须带上 `userId` 条件
2. **软删除**：不物理删除消息，标记 `isDeleted = true`
3. **内容审核**：AI 返回内容建议接入审核服务
4. **Token 限制**：设置用户每日/每月 Token 配额
5. **日志脱敏**：生产环境日志需脱敏用户内容
