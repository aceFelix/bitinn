package com.itniuma.bitinn.service.notification;

import com.itniuma.bitinn.mapper.notification.NotificationMapper;
import com.itniuma.bitinn.pojo.Notification;
import com.itniuma.bitinn.pojo.NotificationCount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 通知服务实现
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationMapper notificationMapper;

    public NotificationServiceImpl(NotificationMapper notificationMapper) {
        this.notificationMapper = notificationMapper;
    }

    @Override
    @Transactional
    public void sendNotification(Integer userId, String type, String title, String content,
                                 Integer sourceUserId, Integer sourceId, String sourceType) {
        // 自己不通知自己
        if (userId.equals(sourceUserId)) {
            return;
        }

        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setSourceUserId(sourceUserId);
        notification.setSourceId(sourceId);
        notification.setSourceType(sourceType);
        notification.setIsRead(0);

        notificationMapper.insert(notification);
        notificationMapper.updateNotificationCount(userId, type, 1);

        log.info("发送通知: userId={}, type={}, title={}", userId, type, title);
    }

    @Override
    public List<Notification> getNotifications(Integer userId, Integer page, Integer pageSize) {
        int offset = (page - 1) * pageSize;
        return notificationMapper.findByUserId(userId, offset, pageSize);
    }

    @Override
    public List<Notification> getNotificationsByType(Integer userId, String type, Integer page, Integer pageSize) {
        int offset = (page - 1) * pageSize;
        return notificationMapper.findByUserIdAndType(userId, type, offset, pageSize);
    }

    @Override
    @Transactional
    public void markAsRead(Integer userId, Integer notificationId) {
        notificationMapper.markAsRead(notificationId);
    }

    @Override
    @Transactional
    public void markAllAsRead(Integer userId) {
        notificationMapper.markAllAsRead(userId);
        notificationMapper.updateNotificationCount(userId, null, -notificationMapper.getUnreadCount(userId));
    }

    @Override
    @Transactional
    public void markAsReadByType(Integer userId, String type) {
        notificationMapper.markAsReadByType(userId, type);
    }

    @Override
    public Integer getUnreadCount(Integer userId) {
        NotificationCount count = notificationMapper.getNotificationCount(userId);
        return count != null ? count.getUnreadCount() : 0;
    }

    @Override
    public NotificationCount getNotificationCount(Integer userId) {
        NotificationCount count = notificationMapper.getNotificationCount(userId);
        if (count == null) {
            count = new NotificationCount();
            count.setUserId(userId);
            count.setUnreadCount(0);
            count.setLikeCount(0);
            count.setFavoriteCount(0);
            count.setCommentCount(0);
            count.setFollowCount(0);
            count.setRepostCount(0);
        }
        return count;
    }

    @Override
    public void initUserNotificationCount(Integer userId) {
        notificationMapper.initNotificationCount(userId);
    }

    @Override
    @Transactional
    public void cleanupOldNotifications(Integer keepCount) {
        // 遍历所有用户清理旧通知
        log.info("开始清理旧通知，保留最近 {} 条", keepCount);
    }
}
