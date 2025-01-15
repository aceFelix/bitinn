package com.itniuma.bigevent.service;

import com.itniuma.bigevent.pojo.Article;
import com.itniuma.bigevent.pojo.PageBean;

public interface ArticleService {
    // 添加文章
    void add(Article article);
    // 文章列表
    PageBean<Article> list(Integer pageNum, Integer pageSize, Integer categoryId, String state);
    // 文章详情
    Article detail(Integer id);
    // 修改文章
    void update(Article article);
    // 删除文章
    void delete(Integer id);
}
