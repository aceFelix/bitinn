package com.itniuma.bitinn.service;

import com.itniuma.bitinn.pojo.Article;
import com.itniuma.bitinn.pojo.PageBean;
import com.itniuma.bitinn.pojo.Result;

public interface ArticleService {

    Result add(Article article);

    Result update(Article article);

    Result delete(Integer id);

    Result<Article> detail(Integer id);

    Result<PageBean<Article>> list(Integer categoryId, String state, Integer pageNum, Integer pageSize);

    Result<PageBean<Article>> myArticles(String state, Integer pageNum, Integer pageSize);
}
