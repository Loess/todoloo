package com.loess.todoloo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loess.todoloo.exceptions.CustomException;
import com.loess.todoloo.model.db.entity.Notification;
import com.loess.todoloo.model.db.entity.User;
import com.loess.todoloo.model.db.repository.NotificationRepo;
import com.loess.todoloo.model.dto.response.NotificationResponse;
import com.loess.todoloo.service.NotificationService;
import com.loess.todoloo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final ObjectMapper mapper;
    private final UserService userService;
    private final NotificationRepo notificationRepo;

    @Override
    public Notification createNotification(User to, String summary, String message) {
        Notification n = new Notification();
        n.setUser(to);
        n.setSummary(summary);
        n.setMessage(message);
        n.setCreationTime(LocalDateTime.now());
        Notification saved = notificationRepo.save(n);
        return saved;
    }

    @Override
    public List<NotificationResponse> getUserUnreadNotifications(Long userId) {
        User user = userService.getUserById(userId);
        return notificationRepo.findUnreadByUser(user).stream()
                .map(c -> mapper.convertValue(c, NotificationResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public boolean markNotificationRead(Long userId, Long id) {
        User user = userService.getUserById(userId);
        Notification n = notificationRepo.findById(id)
                .orElseThrow(() -> new CustomException("Notification not found", HttpStatus.NOT_FOUND));
        if (n.getUser() != user)
            throw new CustomException("No rights to change notification", HttpStatus.FORBIDDEN);
        if (n.getDeliveredTime() != null)
            return false;
        n.setDeliveredTime(LocalDateTime.now());
        notificationRepo.save(n);
        return true;
    }

    @Override
    public List<NotificationResponse> getAllUserNotifications(Long userId) {
        User user = userService.getUserById(userId);
        return notificationRepo.findAllByUserOrderByCreationTimeDesc(user).stream()
                .map(c -> mapper.convertValue(c, NotificationResponse.class))
                .collect(Collectors.toList());
    }

}
