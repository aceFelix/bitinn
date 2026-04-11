-- 插入默认文章分类（create_user=1 为管理员用户）
INSERT INTO category (category_name, category_alias, create_user, create_time, update_time) VALUES
('技术文章', 'tech', 1, NOW(), NOW()),
('学习笔记', 'note', 1, NOW(), NOW()),
('问题讨论', 'discussion', 1, NOW(), NOW()),
('项目分享', 'project', 1, NOW(), NOW()),
('经验分享', 'experience', 1, NOW(), NOW()),
('教程指南', 'tutorial', 1, NOW(), NOW());

-- 插入默认标签
INSERT INTO tag (tag_name, tag_color, create_user, create_time, update_time) VALUES
('Java', '#F97316', 1, NOW(), NOW()),
('Spring Boot', '#6DB33F', 1, NOW(), NOW()),
('Vue', '#42B883', 1, NOW(), NOW()),
('React', '#61DAFB', 1, NOW(), NOW()),
('Python', '#3776AB', 1, NOW(), NOW()),
('Docker', '#2496ED', 1, NOW(), NOW()),
('MySQL', '#4479A1', 1, NOW(), NOW()),
('Redis', '#DC382D', 1, NOW(), NOW()),
('微服务', '#9B59B6', 1, NOW(), NOW()),
('算法', '#E74C3C', 1, NOW(), NOW()),
('TypeScript', '#3178C6', 1, NOW(), NOW()),
('Go', '#00ADD8', 1, NOW(), NOW());
