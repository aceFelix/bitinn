package com.itniuma.bitinn.service.mq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 跨存储数据同步消息生产者
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataSyncProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchanges.data-sync}")
    private String exchange;

    /**
     * 发送文章计数同步消息（MySQL <-> Redis <-> ES）
     */
    public void sendArticleCountSync(Integer articleId, String action) {
        try {
            Map<String, Object> message = Map.of(
                    "articleId", articleId,
                    "action", action,
                    "timestamp", System.currentTimeMillis()
            );
            rabbitTemplate.convertAndSend(exchange, "article.count", message);
            log.debug("[MQ] 发送文章计数同步消息: articleId={}, action={}", articleId, action);
        } catch (Exception e) {
            log.warn("[MQ] 发送文章计数同步消息失败: articleId={}", articleId, e);
        }
    }

    /**
     * 发送 ES 文档同步消息
     */
    public void sendEsSync(Integer articleId, String action) {
        try {
            Map<String, Object> message = Map.of(
                    "articleId", articleId,
                    "action", action,
                    "timestamp", System.currentTimeMillis()
            );
            rabbitTemplate.convertAndSend(exchange, "article.es", message);
            log.debug("[MQ] 发送ES同步消息: articleId={}, action={}", articleId, action);
        } catch (Exception e) {
            log.warn("[MQ] 发送ES同步消息失败: articleId={}", articleId, e);
        }
    }
}
