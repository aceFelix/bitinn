package com.itniuma.bitinn.service.impl;

import com.itniuma.bitinn.mapper.ArticleMapper;
import com.itniuma.bitinn.mapper.TagMapper;
import com.itniuma.bitinn.pojo.Article;
import com.itniuma.bitinn.pojo.PageBean;
import com.itniuma.bitinn.pojo.Result;
import com.itniuma.bitinn.pojo.Tag;
import com.itniuma.bitinn.service.ArticleService;
import com.itniuma.bitinn.utils.ThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ArticleServiceImpl implements ArticleService {

    private final ArticleMapper articleMapper;
    private final TagMapper tagMapper;

    public ArticleServiceImpl(ArticleMapper articleMapper, TagMapper tagMapper) {
        this.articleMapper = articleMapper;
        this.tagMapper = tagMapper;
    }

    @Override
    @Transactional
    public Result add(Article article) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer userId = (Integer) claims.get("id");
        article.setCreateUser(userId);

        if (article.getState() == null || article.getState().isEmpty()) {
            article.setState("已发布");
        }

        articleMapper.insert(article);

        if (article.getTagIds() != null && !article.getTagIds().isEmpty()) {
            for (Integer tagId : article.getTagIds()) {
                articleMapper.insertArticleTag(article.getId(), tagId);
            }
        }

        return Result.success(null, "发布成功");
    }

    @Override
    @Transactional
    public Result update(Article article) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer userId = (Integer) claims.get("id");

        Article existing = articleMapper.findById(article.getId());
        if (existing == null) {
            return Result.error("文章不存在");
        }
        if (!existing.getCreateUser().equals(userId)) {
            return Result.error("无权修改此文章");
        }

        articleMapper.update(article);

        articleMapper.deleteArticleTagsByArticleId(article.getId());
        if (article.getTagIds() != null && !article.getTagIds().isEmpty()) {
            for (Integer tagId : article.getTagIds()) {
                articleMapper.insertArticleTag(article.getId(), tagId);
            }
        }

        return Result.success(null, "更新成功");
    }

    @Override
    @Transactional
    public Result delete(Integer id) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer userId = (Integer) claims.get("id");

        Article existing = articleMapper.findById(id);
        if (existing == null) {
            return Result.error("文章不存在");
        }
        if (!existing.getCreateUser().equals(userId)) {
            return Result.error("无权删除此文章");
        }

        articleMapper.deleteArticleTagsByArticleId(id);
        articleMapper.deleteById(id);

        return Result.success(null, "删除成功");
    }

    @Override
    public Result<Article> detail(Integer id) {
        Article article = articleMapper.findById(id);
        if (article == null) {
            return Result.error("文章不存在");
        }

        List<Tag> tags = tagMapper.findByArticleId(id);
        article.setTags(tags);

        articleMapper.incrementViewCount(id);

        return Result.success(article);
    }

    @Override
    public Result<PageBean<Article>> list(Integer categoryId, String state, Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) pageNum = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;

        int offset = (pageNum - 1) * pageSize;

        if (state == null || state.isEmpty()) {
            state = "已发布";
        }

        List<Article> articles = articleMapper.listWithAuthor(categoryId, state, null, offset, pageSize);
        Long total = articleMapper.countList(categoryId, state, null);

        PageBean<Article> pageBean = new PageBean<>(total, articles);
        return Result.success(pageBean);
    }

    @Override
    public Result<PageBean<Article>> myArticles(String state, Integer pageNum, Integer pageSize) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer userId = (Integer) claims.get("id");

        if (pageNum == null || pageNum < 1) pageNum = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;

        int offset = (pageNum - 1) * pageSize;

        List<Article> articles = articleMapper.listWithAuthor(null, state, userId, offset, pageSize);
        Long total = articleMapper.countList(null, state, userId);

        PageBean<Article> pageBean = new PageBean<>(total, articles);
        return Result.success(pageBean);
    }
}
