package com.itniuma.bitinn.config;

import com.itniuma.bitinn.pojo.document.ArticleDocument;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import jakarta.annotation.PostConstruct;

@Slf4j
@Configuration
@EnableElasticsearchRepositories(basePackages = "com.itniuma.bitinn.repository")
public class ElasticsearchConfig {

    private final ElasticsearchOperations esOps;

    public ElasticsearchConfig(ElasticsearchOperations esOps) {
        this.esOps = esOps;
    }

    @PostConstruct
    public void initIndex() {
        try {
            IndexOperations indexOps = esOps.indexOps(ArticleDocument.class);
            if (!indexOps.exists()) {
                indexOps.createWithMapping();
                log.info("[ES] 索引 bitinn-article 创建成功，已应用 IK 分词器映射");
            } else {
                indexOps.putMapping();
                log.info("[ES] 索引 bitinn-article 已存在，映射已更新");
            }
        } catch (Exception e) {
            log.error("[ES] 索引初始化失败", e);
        }
    }
}
