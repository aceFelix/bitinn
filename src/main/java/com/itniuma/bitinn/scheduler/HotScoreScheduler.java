package com.itniuma.bitinn.scheduler;

import com.itniuma.bitinn.mapper.article.ArticleMapper;
import com.itniuma.bitinn.utils.RedisCacheHelper;
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
    private final RedisCacheHelper redisCache;

    private static final String FEED_CACHE_PREFIX = "feed:";

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

            // 刷新后清除 Feed 缓存，确保下次请求获取最新排序
            redisCache.deleteByPrefix(FEED_CACHE_PREFIX);
        } catch (Exception e) {
            log.error("热分值刷新失败", e);
        }
    }
}
