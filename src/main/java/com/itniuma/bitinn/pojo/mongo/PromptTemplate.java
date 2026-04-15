package com.itniuma.bitinn.pojo.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AI 提示词模板实体 (MongoDB)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "prompt_templates")
public class PromptTemplate {

    @Id
    private String id;

    // 模板名称
    private String name;

    // 模板描述
    private String description;

    // 系统提示词
    private String systemPrompt;

    // 变量列表
    private List<String> variables;

    // 分类: "writing" | "code" | "analysis"
    private String category;

    // 是否公开
    private Boolean isPublic;

    // 使用次数
    private Long useCount;

    // 创建者用户 ID
    private Integer createdBy;

    // 创建时间
    private LocalDateTime createdAt;

    // 更新时间
    private LocalDateTime updatedAt;
}
