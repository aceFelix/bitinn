package com.itniuma.bitinn.config;

import com.itniuma.bitinn.utils.RedisCacheHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WarmupConfig implements ApplicationListener<ApplicationReadyEvent> {

    private final JdbcTemplate jdbcTemplate;
    private final RedisCacheHelper redisCache;

    public WarmupConfig(JdbcTemplate jdbcTemplate, RedisCacheHelper redisCache) {
        this.jdbcTemplate = jdbcTemplate;
        this.redisCache = redisCache;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("开始预热数据库和Redis连接...");

        warmupDatabase();
        warmupRedis();
        warmupTables();

        log.info("所有连接池预热完成，服务已就绪！");
    }

    private void warmupDatabase() {
        for (int i = 0; i < 5; i++) {
            try {
                jdbcTemplate.queryForObject("SELECT 1", Integer.class);
                Thread.sleep(100);
            } catch (Exception e) {
                log.warn("数据库连接预热第{}次失败: {}", i + 1, e.getMessage());
            }
        }
        log.info("数据库连接预热完成（5次）");
    }

    private void warmupRedis() {
        for (int i = 0; i < 3; i++) {
            try {
                redisCache.get("warmup:test");
                Thread.sleep(50);
            } catch (Exception e) {
                log.warn("Redis连接预热第{}次失败: {}", i + 1, e.getMessage());
            }
        }
        log.info("Redis连接预热完成（3次）");
    }

    private void warmupTables() {
        try {
            jdbcTemplate.queryForObject("SELECT COUNT(*) FROM article", Long.class);
            log.info("文章表查询预热完成");
        } catch (Exception e) {
            log.warn("文章表查询预热失败: {}", e.getMessage());
        }

        try {
            jdbcTemplate.queryForObject("SELECT COUNT(*) FROM user", Long.class);
            log.info("用户表查询预热完成");
        } catch (Exception e) {
            log.warn("用户表查询预热失败: {}", e.getMessage());
        }
    }
}
