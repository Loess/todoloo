package com.loess.todoloo.service;

//import com.example.demo.model.db.entity.User;
import com.loess.todoloo.model.dto.response.NotificationResponse;

import java.util.List;

public interface NotificationService {

    List<NotificationResponse> getUserUnreadNotifications(Long userId);

    List<NotificationResponse> getAllUserNotifications(Long userId);

    boolean markNotificationRead(Long userId, Long id);

}
