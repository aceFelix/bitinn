package com.itniuma.bitlnn.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itniuma.bitlnn.mapper.ArticleMapper;
import com.itniuma.bitlnn.pojo.Article;
import com.itniuma.bitlnn.pojo.PageBean;
import com.itniuma.bitlnn.pojo.Tag;
import com.itniuma.bitlnn.service.ArticleService;
import com.itniuma.bitlnn.service.TagService;
import com.itniuma.bitlnn.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 文章服务实现
 *
 * @author aceFelix
 */
@Service
public class ArticleServiceImpl implements ArticleService {
    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private TagService tagService;

    private Integer getCurrentUserId() {
        Map<String, Object> map = ThreadLocalUtil.get();
        return (Integer) map.get("id");
    }

    @Override
    @Transactional
    public void add(Article article) {
        article.setCreateTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());
        article.setCreateUser(getCurrentUserId());
        articleMapper.add(article);
        if (article.getTagIds() != null && !article.getTagIds().isEmpty()) {
            tagService.addArticleTags(article.getId(), article.getTagIds());
        }
    }

    @Override
    public PageBean<Article> list(Integer pageNum, Integer pageSize, Integer categoryId, String state) {
        PageBean<Article> pageBean = new PageBean<>();
        PageHelper.startPage(pageNum, pageSize);

        List<Article> items = articleMapper.list(getCurrentUserId(), categoryId, state);
        Page<Article> page = (Page<Article>) items;
        pageBean.setTotal(page.getTotal());
        pageBean.setItems(page.getResult());
        return pageBean;
    }

    @Override
    public Article detail(Integer id) {
        Article article = articleMapper.detail(id, getCurrentUserId());
        if (article == null) {
            throw new RuntimeException("文章不存在或无权限访问");
        }
        List<Tag> tags = tagService.findByArticleId(id);
        article.setTags(tags);
        return article;
    }

    @Override
    @Transactional
    public void update(Article article) {
        article.setCreateUser(getCurrentUserId());
        article.setUpdateTime(LocalDateTime.now());
        if (articleMapper.update(article) == 0) {
            throw new RuntimeException("文章不存在或无权限修改");
        }
        tagService.updateArticleTags(article.getId(), article.getTagIds());
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        if (articleMapper.delete(id, getCurrentUserId()) == 0) {
            throw new RuntimeException("文章不存在或无权限删除");
        }
        tagService.updateArticleTags(id, null);
    }
}
