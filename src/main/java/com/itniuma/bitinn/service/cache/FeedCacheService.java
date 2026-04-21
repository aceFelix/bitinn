package com.itniuma.bitinn.service.cache;

import com.itniuma.bitinn.utils.RedisCacheHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Feed 缓存版本化服务
 * 通过版本号控制缓存失效，避免 SCAN+DELETE 全量清除的暴力操作
 * 缓存 Key 格式: feed:v{version}:{sortType}:{pageNum}:{pageSize}
 * 失效策略: 递增版本号，旧缓存自然过期
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FeedCacheService {

    private final RedisCacheHelper redisCache;

    private static final String FEED_VERSION_KEY = "feed:version";
    private static final String FEED_LOCK_PREFIX = "feed:lock:";
    private static final int FEED_CACHE_BASE_MINUTES = 3;
    private static final int FEED_CACHE_RANDOM_MINUTES = 2;

    /**
     * 获取当前 Feed 版本号
     */
    public long getCurrentVersion() {
        String version = redisCache.get(FEED_VERSION_KEY);
        if (version != null) {
            return Long.parseLong(version);
        }
        // 初始化版本号
        redisCache.set(FEED_VERSION_KEY, "1", 7, TimeUnit.DAYS);
        return 1;
    }

    /**
     * 递增版本号（使旧缓存全部失效）
     */
    public void incrementVersion() {
        Long newVersion = redisCache.increment(FEED_VERSION_KEY);
        if (newVersion != null) {
            redisCache.expire(FEED_VERSION_KEY, 7, TimeUnit.DAYS);
            log.info("Feed缓存版本号更新: v{}", newVersion);
        }
    }

    /**
     * 构建版本化缓存 Key
     */
    public String buildCacheKey(String sortType, int pageNum, int pageSize) {
        return "feed:v" + getCurrentVersion() + ":" + sortType + ":" + pageNum + ":" + pageSize;
    }

    /**
     * 构建缓存锁 Key
     */
    public String buildLockKey(String sortType, int pageNum) {
        return FEED_LOCK_PREFIX + sortType + ":" + pageNum;
    }

    /**
     * 获取缓存过期时间（随机防雪崩）
     */
    public int getCacheTTLMinutes() {
        int randomMinutes = (int) (Math.random() * FEED_CACHE_RANDOM_MINUTES);
        return FEED_CACHE_BASE_MINUTES + randomMinutes;
    }
}
