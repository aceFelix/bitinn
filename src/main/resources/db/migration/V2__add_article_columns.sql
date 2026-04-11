-- 文章表添加新字段
ALTER TABLE article
    ADD COLUMN excerpt VARCHAR(500) DEFAULT '' COMMENT '文章摘要' AFTER cover_img,
    ADD COLUMN view_count INT UNSIGNED DEFAULT 0 COMMENT '阅读量' AFTER state,
    ADD COLUMN like_count INT UNSIGNED DEFAULT 0 COMMENT '点赞数' AFTER view_count,
    ADD COLUMN comment_count INT UNSIGNED DEFAULT 0 COMMENT '评论数' AFTER like_count,
    ADD COLUMN favorite_count INT UNSIGNED DEFAULT 0 COMMENT '收藏数' AFTER comment_count;
