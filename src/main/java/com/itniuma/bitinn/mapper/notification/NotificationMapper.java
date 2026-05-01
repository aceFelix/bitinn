package com.itniuma.bitinn.mapper.notification;

import com.itniuma.bitinn.pojo.Notification;
import com.itniuma.bitinn.pojo.NotificationCount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NotificationMapper {

    /**
     * 插入通知
     */
    void insert(Notification notification);

    /**
     * 查询用户的通知列表
     */
    List<Notification> findByUserId(@Param("userId") Integer userId,
                                    @Param("offset") Integer offset,
                                    @Param("limit") Integer limit);

    /**
     * 查询用户的某类型通知
     */
    List<Notification> findByUserIdAndType(@Param("userId") Integer userId,
                                            @Param("type") String type,
                                            @Param("offset") Integer offset,
                                            @Param("limit") Integer limit);

    /**
     * 将通知标记为已读
     */
    void markAsRead(@Param("id") Integer id);

    /**
     * 标记所有通知为已读
     */
    void markAllAsRead(@Param("userId") Integer userId);

    /**
     * 标记某类型通知为已读
     */
    void markAsReadByType(@Param("userId") Integer userId, @Param("type") String type);

    /**
     * 获取未读通知数量
     */
    Integer getUnreadCount(@Param("userId") Integer userId);

    /**
     * 获取各类别未读数
     */
    NotificationCount getNotificationCount(@Param("userId") Integer userId);

    /**
     * 更新通知计数
     */
    void updateNotificationCount(@Param("userId") Integer userId, @Param("type") String type, @Param("delta") Integer delta);

    /**
     * 初始化用户通知计数
     */
    void initNotificationCount(@Param("userId") Integer userId);

    /**
     * 删除旧通知（保留最近N条）
     */
    void deleteOldNotifications(@Param("userId") Integer userId, @Param("keepCount") Integer keepCount);
}
