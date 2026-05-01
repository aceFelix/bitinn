package com.itniuma.bitinn.service.interaction;

import com.itniuma.bitinn.utils.RedisCacheHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 交互计数器服务 - 基于 Redis Hash + EXISTS 实现
 * Hash Key: count:article:{articleId}  fields: like/favorite/comment/share
 * Hash Key: count:user:{userId}        fields: follower/following
 * 使用 EXISTS 判断缓存存在性，避免 0 值误判
 */
@Slf4j
@Service
public class InteractionCounterService {

    private final RedisCacheHelper redisCache;

    private static final String ARTICLE_COUNT_PREFIX = "count:article:";
    private static final String USER_COUNT_PREFIX = "count:user:";

    public InteractionCounterService(RedisCacheHelper redisCache) {
        this.redisCache = redisCache;
    }

    // ==================== 文章计数 ====================

    public Long incrementLikeCount(Integer articleId) {
        return hIncrement(ARTICLE_COUNT_PREFIX + articleId, "like", 1);
    }

    public Long decrementLikeCount(Integer articleId) {
        Long val = hIncrement(ARTICLE_COUNT_PREFIX + articleId, "like", -1);
        if (val != null && val < 0) {
            hPut(ARTICLE_COUNT_PREFIX + articleId, "like", "0");
            return 0L;
        }
        return val != null ? val : 0L;
    }

    public Long incrementFavoriteCount(Integer articleId) {
        return hIncrement(ARTICLE_COUNT_PREFIX + articleId, "favorite", 1);
    }

    public Long decrementFavoriteCount(Integer articleId) {
        Long val = hIncrement(ARTICLE_COUNT_PREFIX + articleId, "favorite", -1);
        if (val != null && val < 0) {
            hPut(ARTICLE_COUNT_PREFIX + articleId, "favorite", "0");
            return 0L;
        }
        return val != null ? val : 0L;
    }

    public Long incrementCommentCount(Integer articleId) {
        return hIncrement(ARTICLE_COUNT_PREFIX + articleId, "comment", 1);
    }

    public Long decrementCommentCount(Integer articleId) {
        Long val = hIncrement(ARTICLE_COUNT_PREFIX + articleId, "comment", -1);
        if (val != null && val < 0) {
            hPut(ARTICLE_COUNT_PREFIX + articleId, "comment", "0");
            return 0L;
        }
        return val != null ? val : 0L;
    }

    public Long incrementShareCount(Integer articleId) {
        return hIncrement(ARTICLE_COUNT_PREFIX + articleId, "share", 1);
    }

    // ==================== 用户计数 ====================

    public Long incrementFollowerCount(Integer userId) {
        return hIncrement(USER_COUNT_PREFIX + userId, "follower", 1);
    }

    public Long decrementFollowerCount(Integer userId) {
        Long val = hIncrement(USER_COUNT_PREFIX + userId, "follower", -1);
        if (val != null && val < 0) {
            hPut(USER_COUNT_PREFIX + userId, "follower", "0");
            return 0L;
        }
        return val != null ? val : 0L;
    }

    public Long incrementFollowingCount(Integer userId) {
        return hIncrement(USER_COUNT_PREFIX + userId, "following", 1);
    }

    public Long decrementFollowingCount(Integer userId) {
        Long val = hIncrement(USER_COUNT_PREFIX + userId, "following", -1);
        if (val != null && val < 0) {
            hPut(USER_COUNT_PREFIX + userId, "following", "0");
            return 0L;
        }
        return val != null ? val : 0L;
    }

    // ==================== 批量读取计数（一次 hGetAll 替代 4 次 hExists + 4 次 hGet）====================

    /**
     * 批量获取文章所有计数，一次 Redis 调用完成
     * @return null 表示缓存不存在，调用方应从 MySQL 加载
     */
    public ArticleCounts getAllArticleCounts(Integer articleId) {
        String key = ARTICLE_COUNT_PREFIX + articleId;
        Map<Object, Object> all = redisCache.hGetAll(key);
        if (all == null || all.isEmpty()) {
            return null;
        }
        return new ArticleCounts(
                parseLong((String) all.get("like")),
                parseLong((String) all.get("favorite")),
                parseLong((String) all.get("comment")),
                parseLong((String) all.get("share"))
        );
    }

    // ==================== 读取单字段计数（保留兼容旧调用）====================

    public Long getLikeCount(Integer articleId) {
        return getCountField(articleId, "like");
    }

    public Long getFavoriteCount(Integer articleId) {
        return getCountField(articleId, "favorite");
    }

    public Long getCommentCount(Integer articleId) {
        return getCountField(articleId, "comment");
    }

    public Long getShareCount(Integer articleId) {
        return getCountField(articleId, "share");
    }

    private Long getCountField(Integer articleId, String field) {
        String key = ARTICLE_COUNT_PREFIX + articleId;
        if (Boolean.TRUE.equals(redisCache.hExists(key, field))) {
            return parseLong(redisCache.hGet(key, field));
        }
        return null;
    }

    public Long getFollowerCount(Integer userId) {
        String key = USER_COUNT_PREFIX + userId;
        if (Boolean.TRUE.equals(redisCache.hExists(key, "follower"))) {
            return parseLong(redisCache.hGet(key, "follower"));
        }
        return null;
    }

    public Long getFollowingCount(Integer userId) {
        String key = USER_COUNT_PREFIX + userId;
        if (Boolean.TRUE.equals(redisCache.hExists(key, "following"))) {
            return parseLong(redisCache.hGet(key, "following"));
        }
        return null;
    }

    // ==================== 初始化计数缓存（从 MySQL 加载后调用） ====================

    public void initArticleCounts(Integer articleId, long likeCount, long favoriteCount, long commentCount, long shareCount) {
        String key = ARTICLE_COUNT_PREFIX + articleId;
        redisCache.hPutAll(key, Map.of(
                "like", String.valueOf(likeCount),
                "favorite", String.valueOf(favoriteCount),
                "comment", String.valueOf(commentCount),
                "share", String.valueOf(shareCount)
        ));
        redisCache.expire(key, 24, java.util.concurrent.TimeUnit.HOURS);
    }

    public void initUserCounts(Integer userId, long followerCount, long followingCount) {
        String key = USER_COUNT_PREFIX + userId;
        redisCache.hPutAll(key, Map.of(
                "follower", String.valueOf(followerCount),
                "following", String.valueOf(followingCount)
        ));
        redisCache.expire(key, 24, java.util.concurrent.TimeUnit.HOURS);
    }

    // ==================== 私有方法 ====================

    private Long hIncrement(String key, String field, long delta) {
        Long val = redisCache.hIncrement(key, field, delta);
        if (val != null && delta > 0 && val.equals(delta)) {
            // 首次写入，设置过期时间
            redisCache.expire(key, 24, java.util.concurrent.TimeUnit.HOURS);
        }
        return val;
    }

    private void hPut(String key, String field, String value) {
        redisCache.hPut(key, field, value);
    }

    private Long parseLong(String val) {
        if (val == null) return 0L;
        try {
            return Long.parseLong(val);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    // ==================== 内部类 ====================

    /**
     * 文章计数快照，一次查询获取所有计数字段
     */
    public static class ArticleCounts {
        public final long likeCount;
        public final long favoriteCount;
        public final long commentCount;
        public final long shareCount;

        public ArticleCounts(long likeCount, long favoriteCount, long commentCount, long shareCount) {
            this.likeCount = likeCount;
            this.favoriteCount = favoriteCount;
            this.commentCount = commentCount;
            this.shareCount = shareCount;
        }

        public boolean allZero() {
            return likeCount == 0 && favoriteCount == 0 && commentCount == 0 && shareCount == 0;
        }
    }
}
