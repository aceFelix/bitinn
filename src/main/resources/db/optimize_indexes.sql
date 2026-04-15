-- =============================================
-- bitinn 数据库性能优化 SQL 脚本
-- 执行前请备份数据库！
-- =============================================

-- 1. 添加 hot_score 字段（用于预计算热分值排序）
ALTER TABLE article ADD COLUMN hot_score DOUBLE DEFAULT 0 COMMENT '预计算热分值';

-- 2. 为 hot_score 添加索引（推荐排序核心索引）
ALTER TABLE article ADD INDEX idx_hot_score (hot_score DESC);

-- 3. 为 article 表添加复合索引（Feed 查询优化：按状态+排序）
ALTER TABLE article ADD INDEX idx_state_hot_score (state, hot_score DESC);
ALTER TABLE article ADD INDEX idx_state_create_time (state, create_time DESC);

-- 4. 为 article 表添加分类+状态复合索引
ALTER TABLE article ADD INDEX idx_category_state (category_id, state);

-- 5. 为 article 表添加用户+状态复合索引（我的文章查询）
ALTER TABLE article ADD INDEX idx_create_user_state (create_user, state);

-- 6. 为 article_tag 关联表添加索引
ALTER TABLE article_tag ADD INDEX idx_article_id (article_id);
ALTER TABLE article_tag ADD INDEX idx_tag_id (tag_id);

-- 7. 为评论表添加索引
ALTER TABLE comment ADD INDEX idx_article_id (article_id);
ALTER TABLE comment ADD INDEX idx_user_id (user_id);

-- 8. 为点赞表添加索引
ALTER TABLE article_like ADD INDEX idx_article_user (article_id, user_id);
ALTER TABLE article_like ADD INDEX idx_user_id (user_id);

-- 9. 为收藏表添加索引
ALTER TABLE article_favorite ADD INDEX idx_article_user (article_id, user_id);
ALTER TABLE article_favorite ADD INDEX idx_user_id (user_id);

-- 10. 为关注表添加索引
ALTER TABLE user_follow ADD INDEX idx_follower (follower_id);
ALTER TABLE user_follow ADD INDEX idx_following (following_id);
ALTER TABLE user_follow ADD INDEX idx_follower_following (follower_id, following_id);

-- 11. 为用户表添加索引
ALTER TABLE user ADD UNIQUE INDEX idx_username (username);
ALTER TABLE user ADD UNIQUE INDEX idx_email (email);

-- 12. 初始化 hot_score 数据（一次性计算）
UPDATE article
SET hot_score = (like_count * 2 + favorite_count * 3 + comment_count * 1.5)
                / POWER(TIMESTAMPDIFF(HOUR, create_time, NOW()) + 2, 1.5)
WHERE state = '已发布';
