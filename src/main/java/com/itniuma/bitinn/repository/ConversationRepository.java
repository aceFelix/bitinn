package com.itniuma.bitinn.repository;

import com.itniuma.bitinn.pojo.mongo.Conversation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * AI 对话会话 Repository
 */
@Repository
public interface ConversationRepository extends MongoRepository<Conversation, String> {

    /**
     * 获取用户的会话列表（按更新时间倒序）
     */
    @Query("{ 'userId': ?0, 'isDeleted': false }")
    List<Conversation> findByUserIdOrderByUpdatedAtDesc(Integer userId, Pageable pageable);

    /**
     * 获取用户未删除的单个会话
     */
    @Query(value = "{ '_id': ?0, 'userId': ?1, 'isDeleted': false }", fields = "{ }")
    Optional<Conversation> findByIdAndUserId(String id, Integer userId);

    /**
     * 统计用户未删除的会话数量
     */
    @Query(value = "{ 'userId': ?0, 'isDeleted': false }", count = true)
    long countByUserIdAndNotDeleted(Integer userId);
}
