package com.itniuma.bitinn.service.notification;

import com.itniuma.bitinn.pojo.Notification;
import com.itniuma.bitinn.pojo.NotificationCount;

import java.util.List;

/**
 * 通知服务接口
 */
public interface NotificationService {

    /**
     * 发送通知
     * @param userId 接收通知的用户ID
     * @param type 通知类型: like/favorite/comment/follow/repost
     * @param title 通知标题
     * @param content 通知内容摘要
     * @param sourceUserId 触发通知的用户ID
     * @param sourceId 关联资源ID
     * @param sourceType 资源类型: article/comment
     */
    void sendNotification(Integer userId, String type, String title, String content,
                          Integer sourceUserId, Integer sourceId, String sourceType);

    /**
     * 获取通知列表
     */
    List<Notification> getNotifications(Integer userId, Integer page, Integer pageSize);

    /**
     * 获取某类型通知列表
     */
    List<Notification> getNotificationsByType(Integer userId, String type, Integer page, Integer pageSize);

    /**
     * 标记通知为已读
     */
    void markAsRead(Integer userId, Integer notificationId);

    /**
     * 标记所有通知为已读
     */
    void markAllAsRead(Integer userId);

    /**
     * 标记某类型通知为已读
     */
    void markAsReadByType(Integer userId, String type);

    /**
     * 获取未读通知数量
     */
    Integer getUnreadCount(Integer userId);

    /**
     * 获取各类别未读数
     */
    NotificationCount getNotificationCount(Integer userId);

    /**
     * 初始化用户通知计数
     */
    void initUserNotificationCount(Integer userId);

    /**
     * 清理旧通知（定时任务）
     */
    void cleanupOldNotifications(Integer keepCount);
}
