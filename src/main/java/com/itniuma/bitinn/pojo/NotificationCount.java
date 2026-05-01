package com.itniuma.bitinn.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通知计数实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationCount {
    private Integer userId;
    // 未读通知总数
    private Integer unreadCount;
    // 点赞未读数
    private Integer likeCount;
    // 收藏未读数
    private Integer favoriteCount;
    // 评论未读数
    private Integer commentCount;
    // 关注未读数
    private Integer followCount;
    // 转发未读数
    private Integer repostCount;
}
