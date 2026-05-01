package com.itniuma.bitinn.scheduler;

import com.itniuma.bitinn.mapper.article.ArticleMapper;
import com.itniuma.bitinn.service.cache.FeedCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 热分值定时刷新调度器
 * 每10分钟重算一次 hot_score，避免每次查询实时计算
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HotScoreScheduler {

    private final ArticleMapper articleMapper;
    private final FeedCacheService feedCacheService;

    /**
     * 每10分钟刷新一次热分值
     */
    @Scheduled(fixedRate = 600_000)
    public void refreshHotScores() {
        try {
            long start = System.currentTimeMillis();
            articleMapper.recalculateHotScores();
            long elapsed = System.currentTimeMillis() - start;
            log.info("热分值刷新完成，耗时: {}ms", elapsed);

            // 版本化失效 Feed 缓存
            feedCacheService.incrementVersion();
        } catch (Exception e) {
            log.error("热分值刷新失败", e);
        }
    }
}
