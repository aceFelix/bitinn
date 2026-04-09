-- 在 big_event 数据库中执行
USE big_event;

-- 创建用户表
CREATE TABLE IF NOT EXISTS user (
                                    id INT UNSIGNED AUTO_INCREMENT COMMENT 'ID' PRIMARY KEY,
                                    username VARCHAR(20) NOT NULL COMMENT '用户名',
                                    password VARCHAR(255) NULL COMMENT '密码',
                                    nickname VARCHAR(10) DEFAULT '' NULL COMMENT '昵称',
                                    email VARCHAR(128) DEFAULT '' NULL COMMENT '邮箱',
                                    user_pic VARCHAR(128) DEFAULT '' NULL COMMENT '头像',
                                    create_time DATETIME NOT NULL COMMENT '创建时间',
                                    update_time DATETIME NOT NULL COMMENT '修改时间',
                                    UNIQUE KEY username (username)
) COMMENT '用户表';

-- 创建分类表
CREATE TABLE IF NOT EXISTS category (
                                        id INT UNSIGNED AUTO_INCREMENT COMMENT 'ID' PRIMARY KEY,
                                        category_name VARCHAR(32) NOT NULL COMMENT '分类名称',
                                        category_alias VARCHAR(32) NOT NULL COMMENT '分类别名',
                                        create_user INT UNSIGNED NOT NULL COMMENT '创建人ID',
                                        create_time DATETIME NOT NULL COMMENT '创建时间',
                                        update_time DATETIME NOT NULL COMMENT '修改时间',
                                        CONSTRAINT fk_category_user FOREIGN KEY (create_user) REFERENCES user(id)
);

-- 创建文章表
CREATE TABLE IF NOT EXISTS article (
                                       id INT UNSIGNED AUTO_INCREMENT COMMENT 'ID' PRIMARY KEY,
                                       title VARCHAR(30) NOT NULL COMMENT '文章标题',
                                       content VARCHAR(10000) NOT NULL COMMENT '文章内容',
                                       cover_img VARCHAR(128) NOT NULL COMMENT '文章封面',
                                       state VARCHAR(3) DEFAULT '草稿' NULL COMMENT '文章状态',
                                       category_id INT UNSIGNED NULL COMMENT '文章分类ID',
                                       create_user INT UNSIGNED NOT NULL COMMENT '创建人ID',
                                       create_time DATETIME NOT NULL COMMENT '创建时间',
                                       update_time DATETIME NOT NULL COMMENT '修改时间',
                                       CONSTRAINT fk_article_category FOREIGN KEY (category_id) REFERENCES category(id),
                                       CONSTRAINT fk_article_user FOREIGN KEY (create_user) REFERENCES user(id)
);

-- 创建标签表
CREATE TABLE IF NOT EXISTS tag (
                                   id INT UNSIGNED AUTO_INCREMENT COMMENT 'ID' PRIMARY KEY,
                                   tag_name VARCHAR(32) NOT NULL COMMENT '标签名称',
                                   tag_color VARCHAR(16) DEFAULT '#409EFF' NULL COMMENT '标签颜色',
                                   create_user INT UNSIGNED NOT NULL COMMENT '创建人ID',
                                   create_time DATETIME NOT NULL COMMENT '创建时间',
                                   update_time DATETIME NOT NULL COMMENT '修改时间',
                                   UNIQUE KEY uk_tag_name (tag_name)
) COMMENT '标签表';

-- 创建文章标签关联表
CREATE TABLE IF NOT EXISTS article_tag (
                                           id INT UNSIGNED AUTO_INCREMENT COMMENT 'ID' PRIMARY KEY,
                                           article_id INT UNSIGNED NOT NULL COMMENT '文章ID',
                                           tag_id INT UNSIGNED NOT NULL COMMENT '标签ID',
                                           create_time DATETIME NOT NULL COMMENT '创建时间',
                                           CONSTRAINT fk_article_tag_article FOREIGN KEY (article_id) REFERENCES article(id) ON DELETE CASCADE,
                                           CONSTRAINT fk_article_tag_tag FOREIGN KEY (tag_id) REFERENCES tag(id) ON DELETE CASCADE,
                                           UNIQUE KEY uk_article_tag (article_id, tag_id)
) COMMENT '文章标签关联表';


-- 手动执行或在 MySQL 中运行
ALTER TABLE user ADD UNIQUE INDEX uk_username (username);
ALTER TABLE user ADD UNIQUE INDEX uk_email (email);