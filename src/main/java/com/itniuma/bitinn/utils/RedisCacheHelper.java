package com.itniuma.bitinn.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RedisCacheHelper {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisCacheHelper(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public <T> T get(String key, Class<T> clazz) {
        try {
            String json = redisTemplate.opsForValue().get(key);
            if (json == null) return null;
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            log.warn("Redis读取失败: key={}", key);
            return null;
        }
    }

    public String get(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.warn("Redis读取失败: key={}", key);
            return null;
        }
    }

    public <T> void set(String key, T value, long timeout, TimeUnit unit) {
        try {
            String json = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, json, timeout, unit);
        } catch (Exception e) {
            log.warn("Redis写入失败: key={}", key);
        }
    }

    public void set(String key, String value, long timeout, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, unit);
        } catch (Exception e) {
            log.warn("Redis写入失败: key={}", key);
        }
    }

    public Boolean setIfAbsent(String key, String value, long timeout, TimeUnit unit) {
        try {
            return redisTemplate.opsForValue().setIfAbsent(key, value, timeout, unit);
        } catch (Exception e) {
            log.warn("Redis setIfAbsent失败: key={}", key);
            return false;
        }
    }

    public void delete(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.warn("Redis删除失败: key={}", key);
        }
    }

    /**
     * 使用 SCAN 命令替代 KEYS 命令进行批量删除
     * KEYS 会阻塞 Redis，SCAN 是非阻塞的游标迭代
     */
    public void deleteByPrefix(String prefix) {
        try {
            Set<String> keys = new HashSet<>();
            org.springframework.data.redis.core.Cursor<String> cursor = redisTemplate.scan(
                    org.springframework.data.redis.core.ScanOptions.scanOptions()
                            .match(prefix + "*")
                            .count(100)
                            .build()
            );
            cursor.forEachRemaining(keys::add);
            cursor.close();
            if (!keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
        } catch (Exception e) {
            log.warn("Redis批量删除失败: prefix={}", prefix);
        }
    }

    public Long increment(String key) {
        try {
            return redisTemplate.opsForValue().increment(key);
        } catch (Exception e) {
            log.warn("Redis increment失败: key={}", key);
            return null;
        }
    }

    public Long decrement(String key) {
        try {
            return redisTemplate.opsForValue().decrement(key);
        } catch (Exception e) {
            log.warn("Redis decrement失败: key={}", key);
            return null;
        }
    }

    public Double zIncrementScore(String key, String member, double delta) {
        try {
            return redisTemplate.opsForZSet().incrementScore(key, member, delta);
        } catch (Exception e) {
            log.warn("Redis ZINCRBY失败: key={}", key);
            return null;
        }
    }

    public Set<String> zReverseRange(String key, long start, long end) {
        try {
            return redisTemplate.opsForZSet().reverseRange(key, start, end);
        } catch (Exception e) {
            log.warn("Redis ZREVRANGE失败: key={}", key);
            return new HashSet<>();
        }
    }

    public Set<org.springframework.data.redis.core.ZSetOperations.TypedTuple<String>> zReverseRangeWithScore(String key, long start, long end) {
        try {
            return redisTemplate.opsForZSet().reverseRangeWithScores(key, start, end);
        } catch (Exception e) {
            log.warn("Redis ZREVRANGEWITHSCORES失败: key={}", key);
            return new HashSet<>();
        }
    }

    public Long zCard(String key) {
        try {
            return redisTemplate.opsForZSet().zCard(key);
        } catch (Exception e) {
            log.warn("Redis ZCARD失败: key={}", key);
            return 0L;
        }
    }

    public Boolean expire(String key, long timeout, TimeUnit unit) {
        try {
            return redisTemplate.expire(key, timeout, unit);
        } catch (Exception e) {
            log.warn("Redis EXPIRE失败: key={}", key);
            return false;
        }
    }

    // ==================== Hash 操作 ====================

    /**
     * 判断 key 是否存在
     */
    public Boolean exists(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.warn("Redis EXISTS失败: key={}", key);
            return false;
        }
    }

    /**
     * Hash: 获取字段值
     */
    public String hGet(String key, String field) {
        try {
            return redisTemplate.opsForHash().get(key, field) != null
                    ? redisTemplate.opsForHash().get(key, field).toString() : null;
        } catch (Exception e) {
            log.warn("Redis HGET失败: key={}, field={}", key, field);
            return null;
        }
    }

    /**
     * Hash: 设置字段值
     */
    public void hPut(String key, String field, String value) {
        try {
            redisTemplate.opsForHash().put(key, field, value);
        } catch (Exception e) {
            log.warn("Redis HPUT失败: key={}, field={}", key, field);
        }
    }

    /**
     * Hash: 自增字段值
     */
    public Long hIncrement(String key, String field, long delta) {
        try {
            return redisTemplate.opsForHash().increment(key, field, delta);
        } catch (Exception e) {
            log.warn("Redis HINCRBY失败: key={}, field={}", key, field);
            return null;
        }
    }

    /**
     * Hash: 判断字段是否存在
     */
    public Boolean hExists(String key, String field) {
        try {
            return redisTemplate.opsForHash().hasKey(key, field);
        } catch (Exception e) {
            log.warn("Redis HEXISTS失败: key={}, field={}", key, field);
            return false;
        }
    }

    /**
     * Hash: 获取所有字段和值
     */
    public Map<Object, Object> hGetAll(String key) {
        try {
            return redisTemplate.opsForHash().entries(key);
        } catch (Exception e) {
            log.warn("Redis HGETALL失败: key={}", key);
            return Map.of();
        }
    }

    /**
     * Hash: 批量设置字段
     */
    public void hPutAll(String key, Map<String, String> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
        } catch (Exception e) {
            log.warn("Redis HMSET失败: key={}", key);
        }
    }

    /**
     * Hash: 删除字段
     */
    public Long hDelete(String key, String... fields) {
        try {
            return redisTemplate.opsForHash().delete(key, (Object[]) fields);
        } catch (Exception e) {
            log.warn("Redis HDEL失败: key={}", key);
            return 0L;
        }
    }

    /**
     * 使用 SCAN 命令扫描匹配的 key（替代 KEYS）
     */
    public java.util.List<String> scanKeys(String pattern) {
        java.util.List<String> keys = new java.util.ArrayList<>();
        try {
            org.springframework.data.redis.core.Cursor<String> cursor = redisTemplate.scan(
                    org.springframework.data.redis.core.ScanOptions.scanOptions()
                            .match(pattern)
                            .count(100)
                            .build()
            );
            cursor.forEachRemaining(keys::add);
            cursor.close();
        } catch (Exception e) {
            log.warn("Redis SCAN失败: pattern={}", pattern);
        }
        return keys;
    }
}
