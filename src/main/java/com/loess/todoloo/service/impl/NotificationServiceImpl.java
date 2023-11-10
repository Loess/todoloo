package com.loess.todoloo.service.impl;

import com.loess.todoloo.model.dto.response.NotificationResponse;
import com.loess.todoloo.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    @Override
    public List<NotificationResponse> getUserUnreadNotifications(Long userId) {
        NotificationResponse a = new NotificationResponse();
        a.setId(1L);
        a.setCreation_date(LocalDateTime.now());
        a.setDelivered(false);
        a.setText("test text");
        a.setSummary("Summary");
        a.setFor_user(1L);
        NotificationResponse b = a;
        b.setText("lolkek");
        List<NotificationResponse> nlist = List.of(a, b);;
        return nlist;
    }

    @Override
    public boolean markNotificationRead(Long userId, Long id) {
        return true;
    }

    @Override
    public List<NotificationResponse> getAllUserNotifications(Long userId) {
        return null;
    }

}
