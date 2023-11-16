package com.loess.todoloo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loess.todoloo.model.db.entity.Notification;
import com.loess.todoloo.model.db.entity.User;
import com.loess.todoloo.model.db.repository.NotificationRepo;
import com.loess.todoloo.model.dto.response.NotificationResponse;
import com.loess.todoloo.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NotificationServiceImplTest {

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Mock
    private NotificationRepo notificationRepo;
    @Mock
    private UserService userService;

    @Spy
    private ObjectMapper mapper;

    @Test
    public void testCreateNotification() {
        User user = new User();
        String summary = "Test summary";
        String message = "Test message";
        LocalDateTime mockDateTime = LocalDateTime.of(2023, 11, 15, 12, 0);

        // Моки
        when(notificationRepo.save(any(Notification.class)))
                .thenAnswer(invocation -> {
                    Notification n = invocation.getArgument(0);
                    n.setId(1L); // Ставим ID, который должен быть заполнен после сохранения
                    n.setCreationTime(mockDateTime);
                    return n;
                });

        // Вызов тестируемого метода
        Notification createdNotification = notificationService.createNotification(user, summary, message);

        assertEquals(summary, createdNotification.getSummary());
        assertEquals(message, createdNotification.getMessage());
        assertEquals(user, createdNotification.getUser());
    }

    @Test
    public void getUserUnreadNotifications() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        Notification n = new Notification();
        n.setDeliveredTime(null); // условие недоставленного Notif
        List<Notification> notifications = new ArrayList<>();
        notifications.add(n);

        // Моки
        when(userService.getUserById(userId)).thenReturn(user);
        when(notificationRepo.findUnreadByUser(user)).thenReturn(notifications);

        // Вызов тестируемого метода
        List<NotificationResponse> unreadNotifications = notificationService.getUserUnreadNotifications(userId);

        assertEquals(1, unreadNotifications.size());
    }

    @Test
    public void markNotificationRead() {
        Long userId = 1L;
        Long notificationId = 1444L;
        User user = new User();
        user.setId(userId);

        Notification notification = new Notification();
        notification.setId(notificationId);
        notification.setUser(user);
        notification.setDeliveredTime(null);

        // Моки
        when(userService.getUserById(userId)).thenReturn(user);
        when(notificationRepo.findById(notificationId)).thenReturn(Optional.of(notification));

        // Вызов тестируемого метода
        boolean result = notificationService.markNotificationRead(userId, notificationId);

        assertTrue(result);
        assertNotNull(notification.getDeliveredTime());
        assertEquals(1444L, (long) notification.getId());
        verify(notificationRepo, times(1)).save(notification);
    }

    @Test
    public void getAllUserNotifications() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        Notification n1 = new Notification();
        n1.setId(1L);
        Notification n2 = new Notification();
        n2.setId(2L);

        List<Notification> notifications = new ArrayList<>();
        notifications.add(n1);
        notifications.add(n2);

        // Моки
        when(userService.getUserById(userId)).thenReturn(user);
        when(notificationRepo.findAllByUserOrderByCreationTimeDesc(user)).thenReturn(notifications);

        // Вызов тестируемого метода
        List<NotificationResponse> result = notificationService.getAllUserNotifications(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, (long) result.get(0).getId());
        assertEquals(2L, (long) result.get(1).getId());
        verify(notificationRepo, times(1)).findAllByUserOrderByCreationTimeDesc(user);
    }
}