package com.loess.todoloo.controllers;

import com.loess.todoloo.model.dto.response.NotificationResponse;
import com.loess.todoloo.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.loess.todoloo.utils.AuthUtils.extractUserIdFromToken;

@Tag(name = "Уведомления")
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor //конструктор для инъекции сервиса
@SecurityRequirement(name = "Authorization")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "Получить непрочитанные уведомления пользователя")
    public List<NotificationResponse> getUserUnreadNotifications(@RequestHeader("Authorization") String token) {
        return notificationService.getUserUnreadNotifications(extractUserIdFromToken(token));
    }

    @GetMapping("/all")
    @Operation(summary = "Получить все уведомления пользователя")
    public List<NotificationResponse> getAllUserNotifications(@RequestHeader("Authorization") String token) {
        return notificationService.getAllUserNotifications(extractUserIdFromToken(token));
    }

    @PatchMapping("delivered/{id}")
    @Operation(summary = "Отметить уведомление прочитанным")
    public boolean markNotificationRead(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        return notificationService.markNotificationRead(extractUserIdFromToken(token), id);
    }

}
