package com.itniuma.bitinn.repository;

import com.itniuma.bitinn.pojo.mongo.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AI 聊天消息 Repository
 */
@Repository
public interface MessageRepository extends MongoRepository<Message, String> {

    /**
     * 获取对话的消息列表（按时间正序）
     */
    @Query("{ 'conversationId': ?0, 'isDeleted': false }")
    List<Message> findByConversationIdOrderByCreatedAtAsc(String conversationId, Pageable pageable);

    /**
     * 获取对话的消息列表（按时间倒序，取最近N条）
     */
    @Query("{ 'conversationId': ?0, 'isDeleted': false }")
    List<Message> findRecentMessagesByConversationId(String conversationId, Pageable pageable);

    /**
     * 获取用户最近的消息
     */
    @Query("{ 'userId': ?0, 'isDeleted': false }")
    List<Message> findByUserIdOrderByCreatedAtDesc(Integer userId, Pageable pageable);

    /**
     * 统计对话的消息数量
     */
    @Query(value = "{ 'conversationId': ?0, 'isDeleted': false }", count = true)
    long countByConversationIdAndNotDeleted(String conversationId);

    /**
     * 统计用户使用指定模型的次数
     */
    @Query(value = "{ 'userId': ?0, 'role': 'assistant', 'model': ?1, 'isDeleted': false }", count = true)
    long countByUserIdAndModel(Integer userId, String model);
}
