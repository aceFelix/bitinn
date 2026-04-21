-- =============================================
-- bitinn 交互表外键约束 SQL 脚本
-- 执行前请备份数据库！
-- =============================================

-- =============================================
-- 1. 修改列类型：INT → INT UNSIGNED，与 user.id / article.id 一致
-- =============================================

-- article_like
ALTER TABLE article_like MODIFY COLUMN article_id INT UNSIGNED NOT NULL;
ALTER TABLE article_like MODIFY COLUMN user_id INT UNSIGNED NOT NULL;

-- article_favorite
ALTER TABLE article_favorite MODIFY COLUMN article_id INT UNSIGNED NOT NULL;
ALTER TABLE article_favorite MODIFY COLUMN user_id INT UNSIGNED NOT NULL;

-- user_follow
ALTER TABLE user_follow MODIFY COLUMN follower_id INT UNSIGNED NOT NULL;
ALTER TABLE user_follow MODIFY COLUMN following_id INT UNSIGNED NOT NULL;

-- comment
ALTER TABLE comment MODIFY COLUMN article_id INT UNSIGNED NOT NULL;
ALTER TABLE comment MODIFY COLUMN user_id INT UNSIGNED NOT NULL;
-- parent_id 引用 comment.id（也是 INT），改为 UNSIGNED 保持一致
ALTER TABLE comment MODIFY COLUMN parent_id INT UNSIGNED DEFAULT NULL;

-- notification
ALTER TABLE notification MODIFY COLUMN user_id INT UNSIGNED NOT NULL;
ALTER TABLE notification MODIFY COLUMN source_user_id INT UNSIGNED NOT NULL;

-- =============================================
-- 2. 清理脏数据 + 添加外键约束
-- =============================================

-- article_like
DELETE FROM article_like WHERE article_id NOT IN (SELECT id FROM article);
DELETE FROM article_like WHERE user_id NOT IN (SELECT id FROM user);
ALTER TABLE article_like
    ADD CONSTRAINT fk_article_like_article FOREIGN KEY (article_id) REFERENCES article(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_article_like_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE;

-- article_favorite
DELETE FROM article_favorite WHERE article_id NOT IN (SELECT id FROM article);
DELETE FROM article_favorite WHERE user_id NOT IN (SELECT id FROM user);
ALTER TABLE article_favorite
    ADD CONSTRAINT fk_article_favorite_article FOREIGN KEY (article_id) REFERENCES article(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_article_favorite_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE;

-- user_follow
DELETE FROM user_follow WHERE follower_id NOT IN (SELECT id FROM user);
DELETE FROM user_follow WHERE following_id NOT IN (SELECT id FROM user);
ALTER TABLE user_follow
    ADD CONSTRAINT fk_user_follow_follower FOREIGN KEY (follower_id) REFERENCES user(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_user_follow_following FOREIGN KEY (following_id) REFERENCES user(id) ON DELETE CASCADE;

-- comment
DELETE FROM comment WHERE article_id NOT IN (SELECT id FROM article);
DELETE FROM comment WHERE user_id NOT IN (SELECT id FROM user);
ALTER TABLE comment
    ADD CONSTRAINT fk_comment_article FOREIGN KEY (article_id) REFERENCES article(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_comment_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE;

-- comment 自引用（parent_id → comment.id）
-- 注意：ON DELETE CASCADE 会递归删除所有子评论，如果需要保留子评论，改用 ON DELETE SET NULL
ALTER TABLE comment
    ADD CONSTRAINT fk_comment_parent FOREIGN KEY (parent_id) REFERENCES comment(id) ON DELETE CASCADE;

-- notification
DELETE FROM notification WHERE user_id NOT IN (SELECT id FROM user);
DELETE FROM notification WHERE source_user_id NOT IN (SELECT id FROM user);
ALTER TABLE notification
    ADD CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_notification_source_user FOREIGN KEY (source_user_id) REFERENCES user(id) ON DELETE CASCADE;



