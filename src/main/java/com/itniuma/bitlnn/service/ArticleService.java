package com.itniuma.bitlnn.service;

import com.itniuma.bitlnn.pojo.Article;
import com.itniuma.bitlnn.pojo.PageBean;

/**
 * 文章接口
 *
 * @author aceFelix
 */
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
