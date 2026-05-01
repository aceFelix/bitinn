-- =============================================
-- bitinn 完整数据库脚本
-- 合并所有迁移脚本（V1-V7 + interaction_tables + V5/V6）
-- =============================================

CREATE DATABASE IF NOT EXISTS bitinn DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_general_ci;
USE bitinn;

-- =============================================
-- 1. 用户表
-- =============================================
CREATE TABLE IF NOT EXISTS `user` (
    `id` INT UNSIGNED AUTO_INCREMENT COMMENT 'ID' PRIMARY KEY,
    `username` VARCHAR(20) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NULL COMMENT '密码',
    `nickname` VARCHAR(10) DEFAULT '' NULL COMMENT '昵称',
    `email` VARCHAR(128) DEFAULT '' NULL COMMENT '邮箱',
    `user_pic` VARCHAR(128) DEFAULT '' NULL COMMENT '头像',
    `bio` VARCHAR(200) DEFAULT '' COMMENT '用户简介',
    `phone` VARCHAR(20) DEFAULT '' COMMENT '手机号',
    `role` VARCHAR(20) DEFAULT 'user' COMMENT '角色: user/admin',
    `status` VARCHAR(20) DEFAULT 'active' COMMENT '账号状态: active/banned',
    `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `last_login_ip` VARCHAR(50) DEFAULT '' COMMENT '最后登录IP',
    `create_time` DATETIME NOT NULL COMMENT '创建时间',
    `update_time` DATETIME NOT NULL COMMENT '修改时间',
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- =============================================
-- 2. 分类表
-- =============================================
CREATE TABLE IF NOT EXISTS `category` (
    `id` INT UNSIGNED AUTO_INCREMENT COMMENT 'ID' PRIMARY KEY,
    `category_name` VARCHAR(32) NOT NULL COMMENT '分类名称',
    `category_alias` VARCHAR(32) NOT NULL COMMENT '分类别名',
    `create_user` INT UNSIGNED NOT NULL COMMENT '创建人ID',
    `create_time` DATETIME NOT NULL COMMENT '创建时间',
    `update_time` DATETIME NOT NULL COMMENT '修改时间',
    CONSTRAINT `fk_category_user` FOREIGN KEY (`create_user`) REFERENCES `user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类表';

-- =============================================
-- 3. 文章表
-- =============================================
CREATE TABLE IF NOT EXISTS `article` (
    `id` INT UNSIGNED AUTO_INCREMENT COMMENT 'ID' PRIMARY KEY,
    `title` VARCHAR(30) NOT NULL COMMENT '文章标题',
    `content` VARCHAR(10000) NOT NULL COMMENT '文章内容',
    `cover_img` VARCHAR(512) NOT NULL DEFAULT '' COMMENT '文章封面',
    `excerpt` VARCHAR(500) DEFAULT '' COMMENT '文章摘要',
    `state` VARCHAR(3) DEFAULT '草稿' NULL COMMENT '文章状态',
    `view_count` INT UNSIGNED DEFAULT 0 COMMENT '阅读量',
    `like_count` INT UNSIGNED DEFAULT 0 COMMENT '点赞数',
    `comment_count` INT UNSIGNED DEFAULT 0 COMMENT '评论数',
    `favorite_count` INT UNSIGNED DEFAULT 0 COMMENT '收藏数',
    `share_count` INT UNSIGNED DEFAULT 0 COMMENT '转发数',
    `category_id` INT UNSIGNED NULL COMMENT '文章分类ID',
    `create_user` INT UNSIGNED NOT NULL COMMENT '创建人ID',
    `create_time` DATETIME NOT NULL COMMENT '创建时间',
    `update_time` DATETIME NOT NULL COMMENT '修改时间',
    CONSTRAINT `fk_article_category` FOREIGN KEY (`category_id`) REFERENCES `category`(`id`),
    CONSTRAINT `fk_article_user` FOREIGN KEY (`create_user`) REFERENCES `user`(`id`),
    INDEX `idx_article_user_state_time` (`create_user`, `state`, `create_time` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章表';

-- =============================================
-- 4. 标签表
-- =============================================
CREATE TABLE IF NOT EXISTS `tag` (
    `id` INT UNSIGNED AUTO_INCREMENT COMMENT 'ID' PRIMARY KEY,
    `tag_name` VARCHAR(32) NOT NULL COMMENT '标签名称',
    `tag_color` VARCHAR(16) DEFAULT '#409EFF' NULL COMMENT '标签颜色',
    `create_user` INT UNSIGNED NOT NULL COMMENT '创建人ID',
    `create_time` DATETIME NOT NULL COMMENT '创建时间',
    `update_time` DATETIME NOT NULL COMMENT '修改时间',
    UNIQUE KEY `uk_tag_name` (`tag_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签表';

-- =============================================
-- 5. 文章标签关联表
-- =============================================
CREATE TABLE IF NOT EXISTS `article_tag` (
    `id` INT UNSIGNED AUTO_INCREMENT COMMENT 'ID' PRIMARY KEY,
    `article_id` INT UNSIGNED NOT NULL COMMENT '文章ID',
    `tag_id` INT UNSIGNED NOT NULL COMMENT '标签ID',
    `create_time` DATETIME NOT NULL COMMENT '创建时间',
    CONSTRAINT `fk_article_tag_article` FOREIGN KEY (`article_id`) REFERENCES `article`(`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_article_tag_tag` FOREIGN KEY (`tag_id`) REFERENCES `tag`(`id`) ON DELETE CASCADE,
    UNIQUE KEY `uk_article_tag` (`article_id`, `tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章标签关联表';

-- =============================================
-- 6. 点赞记录表
-- =============================================
CREATE TABLE IF NOT EXISTS `article_like` (
    `id` INT UNSIGNED AUTO_INCREMENT COMMENT 'ID' PRIMARY KEY,
    `article_id` INT UNSIGNED NOT NULL COMMENT '文章ID',
    `user_id` INT UNSIGNED NOT NULL COMMENT '用户ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_article_user` (`article_id`, `user_id`),
    KEY `idx_user_id` (`user_id`),
    CONSTRAINT `fk_article_like_article` FOREIGN KEY (`article_id`) REFERENCES `article`(`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_article_like_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞记录表';

-- =============================================
-- 7. 收藏记录表
-- =============================================
CREATE TABLE IF NOT EXISTS `article_favorite` (
    `id` INT UNSIGNED AUTO_INCREMENT COMMENT 'ID' PRIMARY KEY,
    `article_id` INT UNSIGNED NOT NULL COMMENT '文章ID',
    `user_id` INT UNSIGNED NOT NULL COMMENT '用户ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_article_user` (`article_id`, `user_id`),
    KEY `idx_user_id` (`user_id`),
    CONSTRAINT `fk_article_favorite_article` FOREIGN KEY (`article_id`) REFERENCES `article`(`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_article_favorite_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏记录表';

-- =============================================
-- 8. 用户关注表
-- =============================================
CREATE TABLE IF NOT EXISTS `user_follow` (
    `id` INT UNSIGNED AUTO_INCREMENT COMMENT 'ID' PRIMARY KEY,
    `follower_id` INT UNSIGNED NOT NULL COMMENT '关注者ID',
    `following_id` INT UNSIGNED NOT NULL COMMENT '被关注者ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_follower_following` (`follower_id`, `following_id`),
    KEY `idx_following_id` (`following_id`),
    CONSTRAINT `fk_user_follow_follower` FOREIGN KEY (`follower_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_user_follow_following` FOREIGN KEY (`following_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户关注表';

-- =============================================
-- 9. 评论表
-- =============================================
CREATE TABLE IF NOT EXISTS `comment` (
    `id` INT UNSIGNED AUTO_INCREMENT COMMENT 'ID' PRIMARY KEY,
    `article_id` INT UNSIGNED NOT NULL COMMENT '文章ID',
    `user_id` INT UNSIGNED NOT NULL COMMENT '用户ID',
    `content` TEXT NOT NULL COMMENT '评论内容',
    `parent_id` INT UNSIGNED DEFAULT NULL COMMENT '父评论ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    KEY `idx_article_id` (`article_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_parent_id` (`parent_id`),
    CONSTRAINT `fk_comment_article` FOREIGN KEY (`article_id`) REFERENCES `article`(`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_comment_parent` FOREIGN KEY (`parent_id`) REFERENCES `comment`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- =============================================
-- 10. 通知消息表
-- =============================================
CREATE TABLE IF NOT EXISTS `notification` (
    `id` INT UNSIGNED AUTO_INCREMENT COMMENT 'ID' PRIMARY KEY,
    `user_id` INT UNSIGNED NOT NULL COMMENT '接收通知的用户ID',
    `type` VARCHAR(20) NOT NULL COMMENT '通知类型: like/favorite/comment/follow/repost',
    `title` VARCHAR(100) NOT NULL COMMENT '通知标题',
    `content` VARCHAR(500) DEFAULT NULL COMMENT '通知内容摘要',
    `source_user_id` INT UNSIGNED NOT NULL COMMENT '触发通知的用户ID',
    `source_id` INT UNSIGNED DEFAULT NULL COMMENT '关联资源ID（文章ID/评论ID等）',
    `source_type` VARCHAR(20) DEFAULT NULL COMMENT '资源类型: article/comment',
    `is_read` TINYINT DEFAULT 0 COMMENT '是否已读: 0-未读 1-已读',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY `idx_user_id` (`user_id`),
    KEY `idx_user_read` (`user_id`, `is_read`),
    KEY `idx_user_type` (`user_id`, `type`),
    KEY `idx_create_time` (`create_time`),
    CONSTRAINT `fk_notification_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_notification_source_user` FOREIGN KEY (`source_user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知消息表';

-- =============================================
-- 11. 通知计数表
-- =============================================
CREATE TABLE IF NOT EXISTS `notification_count` (
    `user_id` INT UNSIGNED NOT NULL COMMENT '用户ID',
    `unread_count` INT DEFAULT 0 COMMENT '未读通知数',
    `like_count` INT DEFAULT 0 COMMENT '点赞未读数',
    `favorite_count` INT DEFAULT 0 COMMENT '收藏未读数',
    `comment_count` INT DEFAULT 0 COMMENT '评论未读数',
    `follow_count` INT DEFAULT 0 COMMENT '关注未读数',
    `repost_count` INT DEFAULT 0 COMMENT '转发未读数',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知计数表';

-- =============================================
-- 12. 初始数据
-- =============================================

-- 默认管理员用户（密码: 123456 的 BCrypt 加密，需根据实际注册接口生成）
INSERT INTO `user` (`username`, `password`, `nickname`, `email`, `role`, `status`, `create_time`, `update_time`)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '管理员', 'admin@bitinn.com', 'admin', 'active', NOW(), NOW());

-- 默认文章分类
INSERT INTO `category` (`category_name`, `category_alias`, `create_user`, `create_time`, `update_time`) VALUES
('技术文章', 'tech', 1, NOW(), NOW()),
('学习笔记', 'note', 1, NOW(), NOW()),
('问题讨论', 'discussion', 1, NOW(), NOW()),
('项目分享', 'project', 1, NOW(), NOW()),
('经验分享', 'experience', 1, NOW(), NOW()),
('教程指南', 'tutorial', 1, NOW(), NOW());

-- 默认标签
INSERT IGNORE INTO tag (tag_name, tag_color, create_time, update_time) VALUES
('JavaScript', '#F7DF1E', NOW(), NOW()),
('Python', '#3776AB', NOW(), NOW()),
('Java', '#007396', NOW(), NOW()),
('C/C++', '#00599C', NOW(), NOW()),
('PHP', '#777BB4', NOW(), NOW()),
('C#', '#512BD4', NOW(), NOW()),
('TypeScript', '#3178C6', NOW(), NOW()),
('Vue', '#42B883', NOW(), NOW()),
('React', '#61DAFB', NOW(), NOW()),
('Go', '#00ADD8', NOW(), NOW()),
('Rust', '#DEA584', NOW(), NOW()),
('Docker', '#2496ED', NOW(), NOW()),
('Linux', '#FCC624', NOW(), NOW()),
('Django', '#092E20', NOW(), NOW()),
('FastAPI', '#009688', NOW(), NOW()),
('Flask', '#000000', NOW(), NOW()),
('SpringBoot', '#6DB33F', NOW(), NOW()),
('SpringCloud', '#6DB33F', NOW(), NOW()),
('SpringAI', '#6DB33F', NOW(), NOW()),
('Langchain', '#1C3C3C', NOW(), NOW()),
('Langchain4j', '#4B5563', NOW(), NOW()),
('PyTorch', '#EE4C2C', NOW(), NOW()),
('TensorFlow', '#FF6F00', NOW(), NOW()),
('Scikit-learn', '#F7931E', NOW(), NOW()),
('Taro', '#3CB371', NOW(), NOW()),
('Uni-app', '#2B9939', NOW(), NOW()),
('Android', '#3DDC84', NOW(), NOW()),
('iOS', '#007AFF', NOW(), NOW()),
('HarmonyOS', '#E85446', NOW(), NOW()),
('MySQL', '#4479A1', NOW(), NOW()),
('Redis', '#DC382D', NOW(), NOW()),
('微服务', '#9B59B6', NOW(), NOW()),
('算法', '#E74C3C', NOW(), NOW());


-- 初始化通知计数数据（为现有用户创建记录）
INSERT INTO `notification_count` (`user_id`, `unread_count`, `like_count`, `favorite_count`, `comment_count`, `follow_count`, `repost_count`)
SELECT `id`, 0, 0, 0, 0, 0, 0 FROM `user`
ON DUPLICATE KEY UPDATE `update_time` = NOW();


-- 1. 插入管理员用户
INSERT INTO `user` (`username`, `password`, `nickname`, `email`, `role`, `status`, `create_time`, `update_time`)
VALUES ('艾斯Felix', '$2a$10$IKN.GLtnNq4PDyF1iDXxW.3J2a1o36UQiQHgrNoC/3ypWz.lzVzkK', '管理员', '13985465782@163.com', 'admin', 'active', NOW(), NOW());

-- 2. 然后插入分类
INSERT INTO `category` (`category_name`, `category_alias`, `create_user`, `create_time`, `update_time`) VALUES
                                                                                                            ('技术文章', 'tech', 1, NOW(), NOW()),                                                                                                        ('学习笔记', 'note', 1, NOW(), NOW()),
                                                                                                            ('问题讨论', 'discussion', 1, NOW(), NOW()),
                                                                                                            ('项目分享', 'project', 1, NOW(), NOW()),
                                                                                                            ('经验分享', 'experience', 1, NOW(), NOW()),
                                                                                                            ('教程指南', 'tutorial', 1, NOW(), NOW());

-- 添加热点计算字段
ALTER TABLE article ADD COLUMN hot_score DOUBLE DEFAULT 0 COMMENT '预计算热分值';
ALTER TABLE article ADD INDEX idx_hot_score (hot_score DESC);
