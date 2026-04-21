-- =============================================
-- bitinn 用户扩展字段 + 通知系统 SQL 脚本
-- 执行前请备份数据库！
-- =============================================

-- =============================================
-- 1. 用户表添加扩展字段
-- =============================================
ALTER TABLE `user`
    ADD COLUMN `bio` VARCHAR(200) DEFAULT '' COMMENT '用户简介' AFTER `user_pic`,
    ADD COLUMN `phone` VARCHAR(20) DEFAULT '' COMMENT '手机号' AFTER `bio`,
    ADD COLUMN `role` VARCHAR(20) DEFAULT 'user' COMMENT '角色: user/admin' AFTER `phone`,
    ADD COLUMN `status` VARCHAR(20) DEFAULT 'active' COMMENT '账号状态: active/banned' AFTER `role`,
    ADD COLUMN `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间' AFTER `status`,
    ADD COLUMN `last_login_ip` VARCHAR(50) DEFAULT '' COMMENT '最后登录IP' AFTER `last_login_time`;

-- =============================================
-- 2. 创建通知表
-- =============================================
CREATE TABLE IF NOT EXISTS `notification` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `user_id` INT NOT NULL COMMENT '接收通知的用户ID',
    `type` VARCHAR(20) NOT NULL COMMENT '通知类型: like/favorite/comment/follow/repost',
    `title` VARCHAR(100) NOT NULL COMMENT '通知标题',
    `content` VARCHAR(500) DEFAULT NULL COMMENT '通知内容摘要',
    `source_user_id` INT NOT NULL COMMENT '触发通知的用户ID（谁干的）',
    `source_id` INT DEFAULT NULL COMMENT '关联资源ID（文章ID/评论ID等）',
    `source_type` VARCHAR(20) DEFAULT NULL COMMENT '资源类型: article/comment',
    `is_read` TINYINT DEFAULT 0 COMMENT '是否已读: 0-未读 1-已读',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_user_read` (`user_id`, `is_read`),
    KEY `idx_user_type` (`user_id`, `type`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知消息表';

-- =============================================
-- 3. 创建通知计数表（用于缓存未读数）
-- =============================================
CREATE TABLE IF NOT EXISTS `notification_count` (
    `user_id` INT NOT NULL COMMENT '用户ID',
    `unread_count` INT DEFAULT 0 COMMENT '未读通知数',
    `like_count` INT DEFAULT 0 COMMENT '点赞未读数',
    `favorite_count` INT DEFAULT 0 COMMENT '收藏未读数',
    `comment_count` INT DEFAULT 0 COMMENT '评论未读数',
    `follow_count` INT DEFAULT 0 COMMENT '关注未读数',
    `repost_count` INT DEFAULT 0 COMMENT '转发未读数',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知计数表';

-- =============================================
-- 4. 初始化通知计数数据（为现有用户创建记录）
-- =============================================
INSERT INTO `notification_count` (`user_id`, `unread_count`, `like_count`, `favorite_count`, `comment_count`, `follow_count`, `repost_count`)
SELECT id, 0, 0, 0, 0, 0, 0 FROM `user`
ON DUPLICATE KEY UPDATE update_time = NOW();
