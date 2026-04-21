package com.itniuma.bitinn.scheduler;

import com.itniuma.bitinn.mapper.article.ArticleMapper;
import com.itniuma.bitinn.pojo.Article;
import com.itniuma.bitinn.service.interaction.InteractionCounterService;
import com.itniuma.bitinn.service.mq.DataSyncProducer;
import com.itniuma.bitinn.service.search.ArticleDataSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 数据一致性对账调度器
 * 定时校验 MySQL/Redis/ES 之间的数据一致性，发现不一致时发送 MQ 消息修复
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataConsistencyScheduler {

    private final ArticleMapper articleMapper;
    private final InteractionCounterService counterService;
    private final DataSyncProducer dataSyncProducer;

    @Autowired(required = false)
    private ArticleDataSyncService dataSyncService;

    /**
     * 每30分钟执行一次对账（校验最近100篇文章的计数一致性）
     */
    @Scheduled(fixedRate = 1_800_000)
    public void reconcileArticleCounts() {
        try {
            List<Article> articles = articleMapper.listWithAuthor(null, "已发布", null, 0, 100);
            int inconsistencyCount = 0;

            for (Article article : articles) {
                Integer articleId = article.getId();
                Long redisLike = counterService.getLikeCount(articleId);
                Long redisFav = counterService.getFavoriteCount(articleId);
                Long redisComment = counterService.getCommentCount(articleId);

                boolean inconsistent = false;

                // 检查点赞数一致性
                if (redisLike != null && article.getLikeCount() != null
                        && Math.abs(redisLike - article.getLikeCount()) > 5) {
                    inconsistent = true;
                }

                // 检查收藏数一致性
                if (redisFav != null && article.getFavoriteCount() != null
                        && Math.abs(redisFav - article.getFavoriteCount()) > 5) {
                    inconsistent = true;
                }

                // 检查评论数一致性
                if (redisComment != null && article.getCommentCount() != null
                        && Math.abs(redisComment - article.getCommentCount()) > 5) {
                    inconsistent = true;
                }

                if (inconsistent) {
                    dataSyncProducer.sendArticleCountSync(articleId, "reconcile");
                    inconsistencyCount++;
                }
            }

            if (inconsistencyCount > 0) {
                log.info("[对账] 发现 {} 篇文章计数不一致，已发送修复消息", inconsistencyCount);
            }
        } catch (Exception e) {
            log.error("[对账] 文章计数对账失败", e);
        }
    }

    /**
     * 每天凌晨3点全量 ES 对账
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void reconcileEsData() {
        if (dataSyncService == null) return;
        try {
            log.info("[对账] 开始ES全量对账...");
            dataSyncService.fullSync();
            log.info("[对账] ES全量对账完成");
        } catch (Exception e) {
            log.error("[对账] ES全量对账失败", e);
        }
    }
}
