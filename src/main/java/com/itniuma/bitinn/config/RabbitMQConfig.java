package com.itniuma.bitinn.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 配置 - 跨存储数据同步
 */
@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchanges.data-sync}")
    private String dataSyncExchange;

    @Value("${rabbitmq.queues.article-count-sync}")
    private String articleCountSyncQueue;

    @Value("${rabbitmq.queues.es-sync}")
    private String esSyncQueue;

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public DirectExchange dataSyncExchange() {
        return new DirectExchange(dataSyncExchange, true, false);
    }

    @Bean
    public Queue articleCountSyncQueue() {
        return new Queue(articleCountSyncQueue, true);
    }

    @Bean
    public Queue esSyncQueue() {
        return new Queue(esSyncQueue, true);
    }

    @Bean
    public Binding articleCountSyncBinding() {
        return BindingBuilder.bind(articleCountSyncQueue())
                .to(dataSyncExchange())
                .with("article.count");
    }

    @Bean
    public Binding esSyncBinding() {
        return BindingBuilder.bind(esSyncQueue())
                .to(dataSyncExchange())
                .with("article.es");
    }
}
