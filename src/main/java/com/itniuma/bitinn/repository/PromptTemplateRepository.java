package com.itniuma.bitinn.repository;

import com.itniuma.bitinn.pojo.mongo.PromptTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AI 提示词模板 Repository
 */
@Repository
public interface PromptTemplateRepository extends MongoRepository<PromptTemplate, String> {

    /**
     * 获取用户创建的模板
     */
    List<PromptTemplate> findByCreatedBy(Integer createdBy);

    /**
     * 获取公开的模板（按使用次数排序）
     */
    @Query("{ 'isPublic': true }")
    List<PromptTemplate> findPublicTemplatesOrderByUseCountDesc();

    /**
     * 按分类获取公开模板
     */
    @Query("{ 'isPublic': true, 'category': ?0 }")
    List<PromptTemplate> findByCategory(String category);

    /**
     * 搜索模板（按名称模糊匹配）
     */
    List<PromptTemplate> findByNameContainingIgnoreCase(String name);
}
