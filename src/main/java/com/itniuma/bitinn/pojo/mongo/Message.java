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
import java.util.Map;

/**
 * AI 聊天消息实体 (MongoDB)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "messages")
@CompoundIndexes({
    @CompoundIndex(name = "conv_time_idx", def = "{'conversationId': 1, 'createdAt': 1}"),
    @CompoundIndex(name = "user_time_idx", def = "{'userId': 1, 'createdAt': -1}")
})
public class Message {

    @Id
    private String id;

    // 所属对话 ID
    private String conversationId;

    // 所属用户 ID
    private Integer userId;

    // 角色: "user" | "assistant" | "system"
    private String role;

    // 消息内容（Markdown）
    private String content;

    // 原始内容（AI 返回的完整内容）
    private String rawContent;

    // 使用的模型
    private String model;

    // token 消耗
    private Integer tokens;

    // 结束原因: "stop" | "length" | "content_filter"
    private String finishReason;

    // 附件列表
    private List<Attachment> attachments;

    // 扩展元数据
    private Map<String, Object> metadata;

    // 用户评分: 1-5, null=未评分
    private Integer rating;

    // 用户反馈内容
    private String feedback;

    // 软删除
    private Boolean isDeleted;

    // 创建时间
    private LocalDateTime createdAt;

    // 更新时间
    private LocalDateTime updatedAt;

    /**
     * 附件类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Attachment {
        // 类型: "image" | "file"
        private String type;
        // URL
        private String url;
        // 文件名
        private String name;
        // 文件大小
        private Long size;
    }
}
