package com.itniuma.bitinn.service.interaction;

import com.itniuma.bitinn.utils.RedisCacheHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class InteractionCounterService {

    private final RedisCacheHelper redisCache;

    private static final String ARTICLE_LIKE_COUNT = "count:article:like:";
    private static final String ARTICLE_FAVORITE_COUNT = "count:article:favorite:";
    private static final String ARTICLE_COMMENT_COUNT = "count:article:comment:";
    private static final String USER_FOLLOWER_COUNT = "count:user:follower:";
    private static final String USER_FOLLOWING_COUNT = "count:user:following:";

    public InteractionCounterService(RedisCacheHelper redisCache) {
        this.redisCache = redisCache;
    }

    public Long incrementLikeCount(Integer articleId) {
        return redisCache.increment(ARTICLE_LIKE_COUNT + articleId);
    }

    public Long decrementLikeCount(Integer articleId) {
        Long val = redisCache.decrement(ARTICLE_LIKE_COUNT + articleId);
        if (val != null && val < 0) {
            redisCache.set(ARTICLE_LIKE_COUNT + articleId, "0", 24, java.util.concurrent.TimeUnit.HOURS);
            return 0L;
        }
        return val != null ? val : 0L;
    }

    public Long incrementFavoriteCount(Integer articleId) {
        return redisCache.increment(ARTICLE_FAVORITE_COUNT + articleId);
    }

    public Long decrementFavoriteCount(Integer articleId) {
        Long val = redisCache.decrement(ARTICLE_FAVORITE_COUNT + articleId);
        if (val != null && val < 0) {
            redisCache.set(ARTICLE_FAVORITE_COUNT + articleId, "0", 24, java.util.concurrent.TimeUnit.HOURS);
            return 0L;
        }
        return val != null ? val : 0L;
    }

    public Long incrementCommentCount(Integer articleId) {
        return redisCache.increment(ARTICLE_COMMENT_COUNT + articleId);
    }

    public Long decrementCommentCount(Integer articleId) {
        Long val = redisCache.decrement(ARTICLE_COMMENT_COUNT + articleId);
        if (val != null && val < 0) {
            redisCache.set(ARTICLE_COMMENT_COUNT + articleId, "0", 24, java.util.concurrent.TimeUnit.HOURS);
            return 0L;
        }
        return val != null ? val : 0L;
    }

    public Long incrementFollowerCount(Integer userId) {
        return redisCache.increment(USER_FOLLOWER_COUNT + userId);
    }

    public Long decrementFollowerCount(Integer userId) {
        Long val = redisCache.decrement(USER_FOLLOWER_COUNT + userId);
        if (val != null && val < 0) {
            redisCache.set(USER_FOLLOWER_COUNT + userId, "0", 24, java.util.concurrent.TimeUnit.HOURS);
            return 0L;
        }
        return val != null ? val : 0L;
    }

    public Long incrementFollowingCount(Integer userId) {
        return redisCache.increment(USER_FOLLOWING_COUNT + userId);
    }

    public Long decrementFollowingCount(Integer userId) {
        Long val = redisCache.decrement(USER_FOLLOWING_COUNT + userId);
        if (val != null && val < 0) {
            redisCache.set(USER_FOLLOWING_COUNT + userId, "0", 24, java.util.concurrent.TimeUnit.HOURS);
            return 0L;
        }
        return val != null ? val : 0L;
    }

    public Long getLikeCount(Integer articleId) {
        String val = redisCache.get(ARTICLE_LIKE_COUNT + articleId);
        return val != null ? Long.parseLong(val) : 0L;
    }

    public Long getFavoriteCount(Integer articleId) {
        String val = redisCache.get(ARTICLE_FAVORITE_COUNT + articleId);
        return val != null ? Long.parseLong(val) : 0L;
    }

    public Long getCommentCount(Integer articleId) {
        String val = redisCache.get(ARTICLE_COMMENT_COUNT + articleId);
        return val != null ? Long.parseLong(val) : 0L;
    }

    public Long getFollowerCount(Integer userId) {
        String val = redisCache.get(USER_FOLLOWER_COUNT + userId);
        return val != null ? Long.parseLong(val) : 0L;
    }

    public Long getFollowingCount(Integer userId) {
        String val = redisCache.get(USER_FOLLOWING_COUNT + userId);
        return val != null ? Long.parseLong(val) : 0L;
    }
}
