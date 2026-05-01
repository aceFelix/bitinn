package com.itniuma.bitinn.service.article.impl;

import com.itniuma.bitinn.mapper.article.ArticleMapper;
import com.itniuma.bitinn.mapper.article.TagMapper;
import com.itniuma.bitinn.mapper.interaction.ArticleFavoriteMapper;
import com.itniuma.bitinn.mapper.interaction.ArticleLikeMapper;
import com.itniuma.bitinn.mapper.interaction.CommentMapper;
import com.itniuma.bitinn.pojo.Article;
import com.itniuma.bitinn.pojo.PageBean;
import com.itniuma.bitinn.pojo.Result;
import com.itniuma.bitinn.pojo.Tag;
import com.itniuma.bitinn.service.article.ArticleService;
import com.itniuma.bitinn.service.cache.FeedCacheService;
import com.itniuma.bitinn.service.interaction.InteractionCounterService;
import com.itniuma.bitinn.service.mq.DataSyncProducer;
import com.itniuma.bitinn.service.search.ArticleDataSyncService;
import com.itniuma.bitinn.utils.RedisCacheHelper;
import com.itniuma.bitinn.utils.ThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ArticleServiceImpl implements ArticleService {

    private final ArticleMapper articleMapper;
    private final TagMapper tagMapper;
    private final RedisCacheHelper redisCache;
    private final InteractionCounterService counterService;
    private final CommentMapper commentMapper;
    private final ArticleLikeMapper likeMapper;
    private final ArticleFavoriteMapper favoriteMapper;
    private final DataSyncProducer dataSyncProducer;
    private final FeedCacheService feedCacheService;
    @Autowired(required = false)
    private ArticleDataSyncService dataSyncService;

    public ArticleServiceImpl(ArticleMapper articleMapper,
                              TagMapper tagMapper,
                              RedisCacheHelper redisCache,
                              InteractionCounterService counterService,
                              CommentMapper commentMapper,
                              ArticleLikeMapper likeMapper,
                              ArticleFavoriteMapper favoriteMapper,
                              DataSyncProducer dataSyncProducer,
                              FeedCacheService feedCacheService) {
        this.articleMapper = articleMapper;
        this.tagMapper = tagMapper;
        this.redisCache = redisCache;
        this.counterService = counterService;
        this.commentMapper = commentMapper;
        this.likeMapper = likeMapper;
        this.favoriteMapper = favoriteMapper;
        this.dataSyncProducer = dataSyncProducer;
        this.feedCacheService = feedCacheService;
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

        String coverImg = article.getCoverImg();
        log.info("[发布文章] 原始coverImg: '{}', 长度: {}", coverImg, coverImg != null ? coverImg.length() : 0);
        if (coverImg != null && !coverImg.isEmpty()) {
            if (!isValidImageUrl(coverImg)) {
                log.warn("[发布文章] coverImg格式无效，已清空: '{}'", coverImg);
                article.setCoverImg("");
            }
        } else {
            article.setCoverImg("");
        }
        article.setCreateTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());

        articleMapper.insert(article);

        if (article.getTagIds() != null && !article.getTagIds().isEmpty()) {
            for (Integer tagId : article.getTagIds()) {
                articleMapper.insertArticleTag(article.getId(), tagId);
            }
        }

        clearFeedCache();

        Article saved = articleMapper.findById(article.getId());
        if (saved != null) {
            dataSyncProducer.sendEsSync(article.getId(), "sync");
        }

        return Result.success(article.getId(), article.getState() != null && article.getState().equals("草稿") ? "草稿已保存" : "发布成功");
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

        String coverImg = article.getCoverImg();
        if (coverImg != null && !coverImg.isEmpty()) {
            if (!isValidImageUrl(coverImg)) {
                article.setCoverImg("");
            }
        } else {
            article.setCoverImg("");
        }

        articleMapper.update(article);

        articleMapper.deleteArticleTagsByArticleId(article.getId());
        if (article.getTagIds() != null && !article.getTagIds().isEmpty()) {
            for (Integer tagId : article.getTagIds()) {
                articleMapper.insertArticleTag(article.getId(), tagId);
            }
        }

        clearFeedCache();

        Article updated = articleMapper.findById(article.getId());
        if (updated != null) {
            dataSyncProducer.sendEsSync(article.getId(), "sync");
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

        clearFeedCache();
        dataSyncProducer.sendEsSync(id, "delete");

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

        // 异步更新浏览量，避免阻塞主流程
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

    @Override
    public Result<PageBean<Article>> feed(String sortType, Integer pageNum, Integer pageSize) {
        if (sortType == null || sortType.isEmpty()) sortType = "recommend";
        if (!List.of("recommend", "latest", "hot").contains(sortType)) sortType = "recommend";
        if (pageNum == null || pageNum < 1) pageNum = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;

        String cacheKey = feedCacheService.buildCacheKey(sortType, pageNum, pageSize);

        // 1. 尝试读取缓存
        PageBean<Article> cached = redisCache.get(cacheKey, PageBean.class);
        if (cached != null) {
            log.debug("Feed缓存命中: {}", cacheKey);
            // 缓存命中仍需刷新实时计数（点赞/收藏等变化频繁）
            if (cached.getItems() != null) {
                for (Article a : cached.getItems()) {
                    enrichArticleCounts(a);
                }
            }
            return Result.success(cached);
        }

        // 2. 防缓存击穿：用 Redis 互斥锁，只有一个请求去查库
        String lockKey = feedCacheService.buildLockKey(sortType, pageNum);
        Boolean lockAcquired = redisCache.setIfAbsent(lockKey, "1", 10, TimeUnit.SECONDS);

        if (lockAcquired != null && lockAcquired) {
            try {
                // 双重检查：获取锁后再次检查缓存
                cached = redisCache.get(cacheKey, PageBean.class);
                if (cached != null) {
                    if (cached.getItems() != null) {
                        for (Article a : cached.getItems()) {
                            enrichArticleCounts(a);
                        }
                    }
                    return Result.success(cached);
                }

                int offset = (pageNum - 1) * pageSize;
                String state = "已发布";

                List<Article> articles = articleMapper.listFeed(sortType, state, offset, pageSize);
                Long total = articleMapper.countFeed(state);

                // 从 Redis 获取实时计数（null 表示缓存不存在，使用 MySQL 值）
                for (Article article : articles) {
                    enrichArticleCounts(article);
                }

                PageBean<Article> pageBean = new PageBean<>(total, articles);

                // 随机过期时间防止缓存雪崩
                int ttlMinutes = feedCacheService.getCacheTTLMinutes();
                redisCache.set(cacheKey, pageBean, ttlMinutes, TimeUnit.MINUTES);
                log.debug("Feed缓存写入: {} (过期: {}分钟)", cacheKey, ttlMinutes);

                return Result.success(pageBean);
            } finally {
                redisCache.delete(lockKey);
            }
        } else {
            // 未获取到锁，短暂等待后重试读缓存
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            cached = redisCache.get(cacheKey, PageBean.class);
            if (cached != null) {
                if (cached.getItems() != null) {
                    for (Article a : cached.getItems()) {
                        enrichArticleCounts(a);
                    }
                }
                return Result.success(cached);
            }

            // 等待后仍无缓存，直接查库（降级策略，保证可用性）
            int offset = (pageNum - 1) * pageSize;
            String state = "已发布";
            List<Article> articles = articleMapper.listFeed(sortType, state, offset, pageSize);
            Long total = articleMapper.countFeed(state);

            // 从 Redis 获取实时计数（降级时也要保证数据准确性）
            for (Article article : articles) {
                enrichArticleCounts(article);
            }
            
            return Result.success(new PageBean<>(total, articles));
        }
    }

    private void enrichArticleCounts(Article article) {
        InteractionCounterService.ArticleCounts counts = counterService.getAllArticleCounts(article.getId());
        if (counts == null) {
            fallbackArticleCounts(article);
        } else {
            article.setLikeCount((int) counts.likeCount);
            article.setFavoriteCount((int) counts.favoriteCount);
            article.setCommentCount((int) counts.commentCount);
            article.setShareCount((int) counts.shareCount);
        }
    }

    private void fallbackArticleCounts(Article article) {
        int articleId = article.getId();

        Long dbLike = likeMapper.countByArticleId(articleId);
        Long dbFav = favoriteMapper.countByArticleId(articleId);
        Long dbCom = commentMapper.countByArticleId(articleId);

        article.setLikeCount(dbLike != null ? dbLike.intValue() : 0);
        article.setFavoriteCount(dbFav != null ? dbFav.intValue() : 0);
        article.setCommentCount(dbCom != null ? dbCom.intValue() : 0);

        counterService.initArticleCounts(articleId,
                article.getLikeCount(),
                article.getFavoriteCount(),
                article.getCommentCount(),
                article.getShareCount() != null ? article.getShareCount() : 0);
    }

    private void clearFeedCache() {
        // 版本化失效：只需递增版本号，旧缓存自然过期
        feedCacheService.incrementVersion();
        log.info("Feed缓存版本号已递增");
    }

    private boolean isValidImageUrl(String url) {
        if (url == null || url.trim().isEmpty()) return false;
        url = url.trim();
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url.contains(".") && url.matches(".*\\.(jpg|jpeg|png|gif|webp|svg|bmp)(\\?.*)?$")
                    || url.contains("aliyuncs.com") || url.contains("oss-");
        }
        if (url.startsWith("/")) {
            return url.matches(".+\\.(jpg|jpeg|png|gif|webp|svg|bmp)(\\?.*)?$");
        }
        return false;
    }
}
