package com.itniuma.bigevent.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itniuma.bigevent.mapper.ArticleMapper;
import com.itniuma.bigevent.pojo.Article;
import com.itniuma.bigevent.pojo.PageBean;
import com.itniuma.bigevent.pojo.Result;
import com.itniuma.bigevent.service.ArticleService;
import com.itniuma.bigevent.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ArticleServiceImpl implements ArticleService {
    @Autowired
    private ArticleMapper articleMapper;
    // 添加文章
    @Override
    public void add(Article article) {
        // 补充属性
        article.setCreateTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());
        // 补充创建用户
        Map<String,Object> map = ThreadLocalUtil.get();
        article.setCreateUser((Integer)map.get("id"));
        articleMapper.add(article);
    }

    // 文章列表
    @Override
    public PageBean<Article> list(Integer pageNum, Integer pageSize, Integer categoryId, String state) {
        // 创建PageBean对象
        PageBean<Article> pageBean = new PageBean<>();
        // 开启分页查询 需要MyBatis分页插件PageHelper
        PageHelper.startPage(pageNum,pageSize);
        // 调用mapper层查询分页数据
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        List<Article> items = articleMapper.list(userId,categoryId,state);
        // 封装PageBean对象: 总记录数和当前页数据
        Page<Article> page = (Page<Article>) items;
        pageBean.setTotal(page.getTotal());
        pageBean.setItems(page.getResult());
        return pageBean;
    }

    // 文章详情
    @Override
    public Article detail(Integer id) {
        Article article = articleMapper.detail(id);
        return article;
    }

    // 修改文章
    @Override
    public void update(Article article) {
        article.setUpdateTime(LocalDateTime.now());
        articleMapper.update(article);
    }

    // 删除文章
    @Override
    public void delete(Integer id) {
        articleMapper.delete(id);
    }
}
