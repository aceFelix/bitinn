-- =============================================
-- bitinn 数据库补充字段 SQL 脚本
-- 执行前请备份数据库！
-- =============================================

-- 1. 添加转发数字段到 article 表
ALTER TABLE article ADD COLUMN share_count INT UNSIGNED DEFAULT 0 COMMENT '转发数' AFTER favorite_count;

-- 2. 更新 article 插入语句中的字段（如果有需要）
-- 注意：ALTER TABLE 已添加字段，INSERT 语句会自动包含此字段

-- 3. 添加 share_count 字段到 MyBatis Mapper（如果需要显式指定）
-- 已通过 resultMap 自动映射，无需手动修改

-- 4. 验证字段是否添加成功
-- 执行以下语句查看表结构：
-- DESC article;
-- SELECT id, title, share_count FROM article LIMIT 5;
