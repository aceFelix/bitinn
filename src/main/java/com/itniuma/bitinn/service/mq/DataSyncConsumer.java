package com.itniuma.bitinn.service.mq;

import com.itniuma.bitinn.mapper.article.ArticleMapper;
import com.itniuma.bitinn.pojo.Article;
import com.itniuma.bitinn.service.interaction.InteractionCounterService;
import com.itniuma.bitinn.service.search.ArticleDataSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 跨存储数据同步消息消费者
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataSyncConsumer {

    private final ArticleMapper articleMapper;
    private final InteractionCounterService counterService;

    @Autowired(required = false)
    private ArticleDataSyncService dataSyncService;

    @Value("${rabbitmq.queues.article-count-sync}")
    private String articleCountQueue;

    @Value("${rabbitmq.queues.es-sync}")
    private String esSyncQueue;

    /**
     * 消费文章计数同步消息
     */
    @RabbitListener(queues = "${rabbitmq.queues.article-count-sync}")
    public void handleArticleCountSync(Map<String, Object> message) {
        try {
            Integer articleId = (Integer) message.get("articleId");
            String action = (String) message.get("action");
            log.debug("[MQ] 收到文章计数同步消息: articleId={}, action={}", articleId, action);

            // 从 MySQL 获取最新计数
            Article article = articleMapper.findById(articleId);
            if (article == null) return;

            // 同步到 Redis Hash（初始化/修正）
            counterService.initArticleCounts(articleId,
                    article.getLikeCount() != null ? article.getLikeCount() : 0,
                    article.getFavoriteCount() != null ? article.getFavoriteCount() : 0,
                    article.getCommentCount() != null ? article.getCommentCount() : 0,
                    article.getShareCount() != null ? article.getShareCount() : 0
            );

            // 同步到 ES
            if (dataSyncService != null) {
                dataSyncService.updateArticleCounts(articleId,
                        article.getLikeCount(), article.getCommentCount(),
                        article.getFavoriteCount(), article.getViewCount());
            }
        } catch (Exception e) {
            log.error("[MQ] 处理文章计数同步消息失败", e);
        }
    }

    /**
     * 消费 ES 同步消息
     */
    @RabbitListener(queues = "${rabbitmq.queues.es-sync}")
    public void handleEsSync(Map<String, Object> message) {
        try {
            Integer articleId = (Integer) message.get("articleId");
            String action = (String) message.get("action");
            log.debug("[MQ] 收到ES同步消息: articleId={}, action={}", articleId, action);

            if (dataSyncService == null) return;

            switch (action) {
                case "sync" -> {
                    Article article = articleMapper.findById(articleId);
                    if (article != null) {
                        dataSyncService.syncArticle(article);
                    }
                }
                case "delete" -> dataSyncService.deleteArticle(articleId);
                default -> log.warn("[MQ] 未知ES同步操作: {}", action);
            }
        } catch (Exception e) {
            log.error("[MQ] 处理ES同步消息失败", e);
        }
    }
}
