-- 用户表唯一索引迁移脚本
-- 确保用户名和邮箱的唯一性

-- 添加用户名唯一索引
ALTER TABLE user ADD UNIQUE INDEX uk_username (username);

-- 添加邮箱唯一索引
ALTER TABLE user ADD UNIQUE INDEX uk_email (email);
