package com.loess.todoloo.service;

import com.loess.todoloo.model.db.entity.Notification;
import com.loess.todoloo.model.db.entity.User;
import com.loess.todoloo.model.dto.response.NotificationResponse;

import java.util.List;

public interface NotificationService {

    Notification createNotification(User to, String summary, String message);

    List<NotificationResponse> getUserUnreadNotifications(Long userId);

    List<NotificationResponse> getAllUserNotifications(Long userId);

    boolean markNotificationRead(Long userId, Long id);

}
