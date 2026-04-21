package com.itniuma.bitinn.scheduler;

import com.itniuma.bitinn.mapper.article.ArticleMapper;
import com.itniuma.bitinn.mapper.interaction.UserFollowMapper;
import com.itniuma.bitinn.utils.RedisCacheHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 计数器回写调度器
 * 定时将 Redis Hash 中的交互计数回写到 MySQL，保证数据最终一致性
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CounterSyncScheduler {

    private final RedisCacheHelper redisCache;
    private final ArticleMapper articleMapper;
    private final UserFollowMapper userFollowMapper;

    private static final String ARTICLE_COUNT_PREFIX = "count:article:";
    private static final String USER_COUNT_PREFIX = "count:user:";

    /**
     * 每5分钟将文章计数回写到 MySQL
     */
    @Scheduled(fixedRate = 300_000)
    public void syncArticleCountsToMySQL() {
        try {
            var cursor = redisCache.scanKeys(ARTICLE_COUNT_PREFIX + "*");
            int count = 0;
            for (String key : cursor) {
                try {
                    String idStr = key.substring(ARTICLE_COUNT_PREFIX.length());
                    Integer articleId = Integer.parseInt(idStr);
                    Map<Object, Object> counts = redisCache.hGetAll(key);
                    if (counts.isEmpty()) continue;

                    int likeCount = parseInt(counts.get("like"));
                    int favoriteCount = parseInt(counts.get("favorite"));
                    int commentCount = parseInt(counts.get("comment"));
                    int shareCount = parseInt(counts.get("share"));

                    articleMapper.updateCounts(articleId, likeCount, favoriteCount, commentCount, shareCount);
                    count++;
                } catch (NumberFormatException e) {
                    log.warn("跳过无效的计数器key: {}", key);
                }
            }
            if (count > 0) {
                log.info("[计数器回写] 文章计数回写完成，共处理 {} 篇", count);
            }
        } catch (Exception e) {
            log.error("[计数器回写] 文章计数回写失败", e);
        }
    }

    /**
     * 每10分钟将用户关注计数回写到 MySQL
     */
    @Scheduled(fixedRate = 600_000)
    public void syncUserCountsToMySQL() {
        try {
            var cursor = redisCache.scanKeys(USER_COUNT_PREFIX + "*");
            int count = 0;
            for (String key : cursor) {
                try {
                    String idStr = key.substring(USER_COUNT_PREFIX.length());
                    Integer userId = Integer.parseInt(idStr);
                    Map<Object, Object> counts = redisCache.hGetAll(key);
                    if (counts.isEmpty()) continue;

                    int followerCount = parseInt(counts.get("follower"));
                    // 用户关注计数目前没有单独的 MySQL 字段，仅做日志记录
                    count++;
                } catch (NumberFormatException e) {
                    log.warn("跳过无效的计数器key: {}", key);
                }
            }
            if (count > 0) {
                log.debug("[计数器回写] 用户关注计数检查完成，共处理 {} 个用户", count);
            }
        } catch (Exception e) {
            log.error("[计数器回写] 用户关注计数回写失败", e);
        }
    }

    private int parseInt(Object val) {
        if (val == null) return 0;
        try {
            return Integer.parseInt(val.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
