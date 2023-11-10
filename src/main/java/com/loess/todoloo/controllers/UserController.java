package com.loess.todoloo.controllers;

import com.loess.todoloo.model.dto.request.TaskInfoRequest;
import com.loess.todoloo.model.dto.request.UserInfoRequest;
import com.loess.todoloo.model.dto.response.TaskInfoResponse;
import com.loess.todoloo.model.dto.response.UserInfoResponse;
import com.loess.todoloo.service.TaskService;
import com.loess.todoloo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Пользователи")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor //конструктор для инъекции сервиса
public class UserController {

    private final UserService userService;

    @PostMapping("/new")
    @Operation(summary = "Создать нового пользователя")
    public UserInfoResponse createUser(@RequestBody UserInfoRequest request) {
        return userService.createUser(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить инфо о пользователе")
    public UserInfoResponse getUser(@RequestHeader("userid") Long userId, @PathVariable Long id) {
        return userService.getUserInfoById(userId,id);
    }

    @PostMapping("/{id}")
    @Operation(summary = "Редактировать пользователя")
    public UserInfoResponse editUser(@RequestHeader("userid") Long userId, @PathVariable Long id, @RequestBody UserInfoRequest request) {
        return userService.editUser(userId,id,request);
    }


}
