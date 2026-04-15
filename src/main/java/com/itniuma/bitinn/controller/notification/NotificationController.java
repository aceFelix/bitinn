package com.itniuma.bitinn.controller.notification;

import com.itniuma.bitinn.pojo.Notification;
import com.itniuma.bitinn.pojo.NotificationCount;
import com.itniuma.bitinn.pojo.Result;
import com.itniuma.bitinn.service.notification.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 通知控制器
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * 获取通知列表
     */
    @GetMapping
    public Result<List<Notification>> getNotifications(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        Integer userId = extractUserId(token);
        List<Notification> notifications = notificationService.getNotifications(userId, page, pageSize);
        return Result.success(notifications);
    }

    /**
     * 获取某类型通知
     */
    @GetMapping("/type/{type}")
    public Result<List<Notification>> getNotificationsByType(
            @RequestHeader("Authorization") String token,
            @PathVariable String type,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        Integer userId = extractUserId(token);
        List<Notification> notifications = notificationService.getNotificationsByType(userId, type, page, pageSize);
        return Result.success(notifications);
    }

    /**
     * 获取未读数量统计
     */
    @GetMapping("/count")
    public Result<NotificationCount> getNotificationCount(@RequestHeader("Authorization") String token) {
        Integer userId = extractUserId(token);
        NotificationCount count = notificationService.getNotificationCount(userId);
        return Result.success(count);
    }

    /**
     * 标记单条通知为已读
     */
    @PutMapping("/{id}/read")
    public Result<Void> markAsRead(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer id) {
        Integer userId = extractUserId(token);
        notificationService.markAsRead(userId, id);
        return Result.success();
    }

    /**
     * 标记所有通知为已读
     */
    @PutMapping("/read-all")
    public Result<Void> markAllAsRead(@RequestHeader("Authorization") String token) {
        Integer userId = extractUserId(token);
        notificationService.markAllAsRead(userId);
        return Result.success();
    }

    /**
     * 标记某类型通知为已读
     */
    @PutMapping("/type/{type}/read")
    public Result<Void> markAsReadByType(
            @RequestHeader("Authorization") String token,
            @PathVariable String type) {
        Integer userId = extractUserId(token);
        notificationService.markAsReadByType(userId, type);
        return Result.success();
    }

    /**
     * 从token中提取用户ID（简化实现）
     */
    private Integer extractUserId(String token) {
        // 实际应从JWT或其他认证机制获取
        // 这里需要在调用处正确注入userId
        return null;
    }
}
