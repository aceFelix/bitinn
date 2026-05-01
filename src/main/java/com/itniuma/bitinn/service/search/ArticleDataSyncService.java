package com.itniuma.bitinn.service.search;

import com.itniuma.bitinn.mapper.article.ArticleMapper;
import com.itniuma.bitinn.mapper.article.TagMapper;
import com.itniuma.bitinn.pojo.Article;
import com.itniuma.bitinn.pojo.Tag;
import com.itniuma.bitinn.pojo.document.ArticleDocument;
import com.itniuma.bitinn.repository.ArticleSearchRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ArticleDataSyncService implements ApplicationRunner {

    private final ArticleMapper articleMapper;
    private final TagMapper tagMapper;
    private final ArticleSearchRepository searchRepository;
    private final ElasticsearchOperations esOps;

    private static final int SYNC_BATCH_SIZE = 500;

    public ArticleDataSyncService(ArticleMapper articleMapper, TagMapper tagMapper,
                                  ArticleSearchRepository searchRepository, ElasticsearchOperations esOps) {
        this.articleMapper = articleMapper;
        this.tagMapper = tagMapper;
        this.searchRepository = searchRepository;
        this.esOps = esOps;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("[ES同步] 开始全量同步文章数据到Elasticsearch...");
        try {
            fullSync();
            log.info("[ES同步] 全量同步完成");
        } catch (Exception e) {
            log.error("[ES同步] 全量同步失败，将在后续增量同步中修复", e);
        }
    }

    /**
     * 全量同步：使用滚动查询替代硬编码 LIMIT
     */
    public void fullSync() {
        int offset = 0;
        int totalSynced = 0;
        List<Article> batch;

        do {
            batch = articleMapper.listWithAuthor(null, "已发布", null, offset, SYNC_BATCH_SIZE);
            if (batch.isEmpty()) break;

            // 为每篇文章加载标签
            for (Article article : batch) {
                List<Tag> tags = tagMapper.findByArticleId(article.getId());
                article.setTags(tags);
            }

            List<ArticleDocument> docs = batch.stream()
                    .map(this::toDocument)
                    .collect(Collectors.toList());

            searchRepository.saveAll(docs);
            totalSynced += docs.size();
            offset += SYNC_BATCH_SIZE;

            log.debug("[ES同步] 批次同步完成，offset={}, 本批={}", offset - SYNC_BATCH_SIZE, docs.size());
        } while (batch.size() == SYNC_BATCH_SIZE);

        log.info("[ES同步] 同步了 {} 篇文章", totalSynced);
    }

    public void syncArticle(Article article) {
        if (article == null) return;
        try {
            if ("已发布".equals(article.getState())) {
                List<Tag> tags = tagMapper.findByArticleId(article.getId());
                article.setTags(tags);
                ArticleDocument doc = toDocument(article);
                searchRepository.save(doc);
                log.debug("[ES同步] 增量同步文章: id={}, title={}", article.getId(), article.getTitle());
            } else {
                searchRepository.deleteById(article.getId());
                log.debug("[ES同步] 移除非发布文章: id={}", article.getId());
            }
        } catch (Exception e) {
            log.error("[ES同步] 增量同步文章失败: id={}", article.getId(), e);
        }
    }

    public void deleteArticle(Integer articleId) {
        if (articleId == null) return;
        try {
            searchRepository.deleteById(articleId);
            log.debug("[ES同步] 删除文章: id={}", articleId);
        } catch (Exception e) {
            log.error("[ES同步] 删除文章失败: id={}", articleId, e);
        }
    }

    public void updateArticleCounts(Integer articleId, Integer likeCount, Integer commentCount, Integer favoriteCount, Integer viewCount) {
        if (articleId == null) return;
        try {
            Map<String, Object> updates = new java.util.HashMap<>();
            if (likeCount != null) updates.put("likeCount", likeCount);
            if (commentCount != null) updates.put("commentCount", commentCount);
            if (favoriteCount != null) updates.put("favoriteCount", favoriteCount);
            if (viewCount != null) updates.put("viewCount", viewCount);

            if (updates.isEmpty()) return;

            UpdateQuery updateQuery = UpdateQuery.builder(articleId.toString())
                    .withDocument(org.springframework.data.elasticsearch.core.document.Document.from(updates))
                    .withDocAsUpsert(false)
                    .build();

            esOps.update(updateQuery, IndexCoordinates.of("bitinn-article"));
            log.debug("[ES同步] 更新文章计数: id={}, updates={}", articleId, updates);
        } catch (Exception e) {
            log.error("[ES同步] 更新文章计数失败: id={}", articleId, e);
        }
    }

    /**
     * 更新 ES 中某用户相关的冗余字段（昵称/头像变更时调用）
     */
    public void updateUserFields(Integer userId, String nickname, String avatarUrl) {
        if (userId == null) return;
        try {
            // 查询该用户的所有已发布文章
            List<Article> articles = articleMapper.list(null, null, userId);
            for (Article article : articles) {
                if (!"已发布".equals(article.getState())) continue;

                Map<String, Object> updates = new java.util.HashMap<>();
                if (nickname != null) updates.put("authorName", nickname);
                if (avatarUrl != null) updates.put("authorAvatar", avatarUrl);

                if (updates.isEmpty()) continue;

                UpdateQuery updateQuery = UpdateQuery.builder(article.getId().toString())
                        .withDocument(org.springframework.data.elasticsearch.core.document.Document.from(updates))
                        .withDocAsUpsert(false)
                        .build();

                esOps.update(updateQuery, IndexCoordinates.of("bitinn-article"));
            }
            log.info("[ES同步] 用户 {} 的冗余字段已更新到 ES", userId);
        } catch (Exception e) {
            log.error("[ES同步] 更新用户冗余字段失败: userId={}", userId, e);
        }
    }

    /**
     * 更新 ES 中某分类名称的冗余字段（分类名称变更时调用）
     */
    public void updateCategoryName(Integer categoryId, String categoryName) {
        if (categoryId == null) return;
        try {
            List<Article> articles = articleMapper.list(categoryId, "已发布", null);
            for (Article article : articles) {
                Map<String, Object> updates = Map.of("categoryName", categoryName);

                UpdateQuery updateQuery = UpdateQuery.builder(article.getId().toString())
                        .withDocument(org.springframework.data.elasticsearch.core.document.Document.from(updates))
                        .withDocAsUpsert(false)
                        .build();

                esOps.update(updateQuery, IndexCoordinates.of("bitinn-article"));
            }
            log.info("[ES同步] 分类 {} 的名称已更新到 ES", categoryId);
        } catch (Exception e) {
            log.error("[ES同步] 更新分类冗余字段失败: categoryId={}", categoryId, e);
        }
    }

    private ArticleDocument toDocument(Article article) {
        ArticleDocument doc = new ArticleDocument();
        doc.setId(article.getId());
        doc.setTitle(article.getTitle());
        doc.setContent(article.getContent());
        doc.setExcerpt(article.getExcerpt());
        doc.setCoverImg(article.getCoverImg());
        doc.setCategoryName(article.getCategoryName());
        doc.setAuthorName(article.getAuthorName());
        doc.setAuthorAvatar(article.getAuthorAvatar());
        doc.setLikeCount(article.getLikeCount());
        doc.setCommentCount(article.getCommentCount());
        doc.setFavoriteCount(article.getFavoriteCount());
        doc.setViewCount(article.getViewCount());
        doc.setHotScore(article.getHotScore());
        doc.setCreateTime(article.getCreateTime());
        doc.setUpdateTime(article.getUpdateTime());

        if (article.getTags() != null) {
            List<String> tagNames = new ArrayList<>();
            for (Tag tag : article.getTags()) {
                String name = tag.getTagName();
                if (name != null) tagNames.add(name);
            }
            doc.setTags(tagNames);
        } else {
            doc.setTags(List.of());
        }

        return doc;
    }
}
