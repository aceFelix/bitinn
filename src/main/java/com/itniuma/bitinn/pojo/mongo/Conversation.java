package com.itniuma.bitinn.pojo.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AI 对话会话实体 (MongoDB)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "conversations")
@CompoundIndexes({
    @CompoundIndex(name = "user_updated_idx", def = "{'userId': 1, 'updatedAt': -1}"),
    @CompoundIndex(name = "user_pinned_idx", def = "{'userId': 1, 'isPinned': -1}")
})
public class Conversation {

    @Id
    private String id;

    // 关联的用户 ID (MySQL user.id)
    private Integer userId;

    // 对话标题（AI 生成或用户命名）
    private String title;

    // 使用的 AI 模型
    private String model;

    // 消息总数
    private Integer messageCount;

    // 最后一条消息时间
    private LocalDateTime lastMessageAt;

    // 对话标签，如 ["技术", "代码"]
    private List<String> tags;

    // 是否置顶
    private Boolean isPinned;

    // 是否删除（软删除）
    private Boolean isDeleted;

    // 创建时间
    private LocalDateTime createdAt;

    // 更新时间
    private LocalDateTime updatedAt;
}
