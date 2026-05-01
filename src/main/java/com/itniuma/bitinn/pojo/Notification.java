package com.itniuma.bitinn.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 通知实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    private Integer id;
    // 接收通知的用户ID
    private Integer userId;
    // 通知类型: like/favorite/comment/follow/repost
    private String type;
    // 通知标题
    private String title;
    // 通知内容摘要
    private String content;
    // 触发通知的用户ID（谁干的）
    private Integer sourceUserId;
    // 关联资源ID（文章ID/评论ID等）
    private Integer sourceId;
    // 资源类型: article/comment
    private String sourceType;
    // 是否已读: 0-未读 1-已读
    private Integer isRead;
    // 创建时间
    private LocalDateTime createTime;

    // 扩展：触发者的用户名和头像（用于展示）
    private String sourceUsername;
    private String sourceUserAvatar;
}
