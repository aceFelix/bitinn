package com.itniuma.bitinn.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WarmupConfig implements ApplicationListener<ApplicationReadyEvent> {

    private final JdbcTemplate jdbcTemplate;
    private final StringRedisTemplate redisTemplate;

    public WarmupConfig(JdbcTemplate jdbcTemplate, StringRedisTemplate redisTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("开始预热数据库和Redis连接...");

        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            log.info("数据库连接预热完成");
        } catch (Exception e) {
            log.error("数据库连接预热失败: {}", e.getMessage());
        }

        try {
            redisTemplate.opsForValue().get("warmup:test");
            log.info("Redis连接预热完成");
        } catch (Exception e) {
            log.error("Redis连接预热失败: {}", e.getMessage());
        }

        log.info("连接池预热完成，服务已就绪！");
    }
}
