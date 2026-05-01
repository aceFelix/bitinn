# BitInn - 技术博客系统后端

BitInn 是一个基于 Spring Boot 的技术博客社区后端服务，提供用户认证、文章管理、分类标签等核心功能。

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.x | 核心框架 |
| Spring Security | - | 安全认证 |
| MyBatis | - | 持久层框架 |
| MySQL | 8.x | 关系型数据库 |
| Redis | - | 缓存 & 分布式锁 |
| JWT | - | 无状态认证 |
| HikariCP | - | 数据库连接池 |

## 项目结构

```
bitinn/
├── src/main/java/com/itniuma/bitinn/
│   ├── config/          # 配置类
│   │   ├── SecurityConfig.java
│   │   └── WarmupConfig.java
│   ├── controller/      # 控制器层
│   │   ├── UserController.java
│   │   ├── ArticleController.java
│   │   ├── CategoryController.java
│   │   ├── TagController.java
│   │   └── FileUploadController.java
│   ├── service/         # 服务层
│   │   └── impl/
│   ├── mapper/          # 数据访问层
│   ├── pojo/            # 实体类
│   ├── utils/           # 工具类
│   ├── filter/          # 过滤器
│   ├── exception/       # 异常处理
│   └── validation/      # 参数校验
├── src/main/resources/
│   ├── mapper/          # MyBatis XML
│   ├── application.yml  # 配置文件
│   └── db/              # 数据库迁移
└── pom.xml
```

## 核心功能

### 用户模块
- 用户注册/登录
- JWT 无状态认证
- 用户信息管理
- 头像上传
- 密码修改

### 文章模块
- 文章 CRUD
- 文章分类管理
- 文章标签管理
- 文章封面图上传

### 安全特性
- BCrypt 密码加密
- 登录失败次数限制
- 注册幂等性设计
- 分布式锁防并发

### 性能优化
- Redis 用户信息缓存
- 数据库连接池预热
- HikariCP 连接池优化

## 快速开始

### 环境要求
- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

### 配置文件

创建 `application-local.yml`：

```yaml
datasource:
  host: localhost
  port: 3306
  database: bitinn
  username: root
  password: your_password
  path: ?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai

redis:
  host: localhost
  port: 6379
  password: your_redis_password

website:
  domain: http://localhost:5173

aliyun-oss:
  access-key: your_access_key
  secret-key: your_secret_key
  end-point: your_endpoint
  bucket-name: your_bucket
  domain: your_cdn_domain
  base-path: bitinn/

mail:
  host: smtp.example.com
  port: 465
  username: your_email
  password: your_email_password
```

### 运行项目

```bash
# 克隆项目
git clone https://github.com/your-username/bitinn.git

# 进入后端目录
cd bitinn/bitinn

# 编译项目
./mvnw clean package -DskipTests

# 运行项目
java -jar target/bitinn-0.0.1-SNAPSHOT.jar
```

## API 文档

### 用户接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/user/register | 用户注册 |
| POST | /api/user/login | 用户登录 |
| GET | /api/user/info | 获取用户信息 |
| PUT | /api/user/update | 更新用户信息 |
| PATCH | /api/user/updateAvatar | 更新头像 |
| PATCH | /api/user/updatePwd | 修改密码 |

### 文章接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/article | 创建文章 |
| GET | /api/article | 文章列表 |
| GET | /api/article/{id} | 文章详情 |
| PUT | /api/article | 更新文章 |
| DELETE | /api/article | 删除文章 |

### 分类接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/category | 添加分类 |
| GET | /api/category | 分类列表 |
| PUT | /api/category | 更新分类 |
| DELETE | /api/category | 删除分类 |

## 数据库设计

### 用户表 (user)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | INT | 主键 |
| username | VARCHAR(16) | 用户名 |
| password | VARCHAR(100) | 密码(BCrypt) |
| nickname | VARCHAR(32) | 昵称 |
| email | VARCHAR(64) | 邮箱 |
| user_pic | VARCHAR(256) | 头像URL |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

### 文章表 (article)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | INT | 主键 |
| title | VARCHAR(100) | 标题 |
| content | TEXT | 内容 |
| cover_img | VARCHAR(256) | 封面图 |
| state | VARCHAR(3) | 状态 |
| category_id | INT | 分类ID |
| create_user | INT | 作者ID |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

## 开发计划

- [ ] 文章评论系统
- [ ] 用户关注系统
- [ ] 文章点赞收藏
- [ ] 搜索功能 (Elasticsearch)
- [ ] 消息通知
- [ ] 管理后台

## 相关项目

- [bitinn-vue](../bitinn-vue) - 前端项目 (Vue 3)

## License

MIT License
