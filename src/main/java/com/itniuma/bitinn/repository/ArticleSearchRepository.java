package com.itniuma.bitinn.repository;

import com.itniuma.bitinn.pojo.document.ArticleDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ArticleSearchRepository extends ElasticsearchRepository<ArticleDocument, Integer> {
}
