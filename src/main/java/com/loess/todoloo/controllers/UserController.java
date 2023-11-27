package com.loess.todoloo.controllers;

import com.loess.todoloo.model.dto.request.TaskInfoRequest;
import com.loess.todoloo.model.dto.request.UserInfoRequest;
import com.loess.todoloo.model.dto.response.TaskInfoResponse;
import com.loess.todoloo.model.dto.response.UserInfoResponse;
import com.loess.todoloo.service.TaskService;
import com.loess.todoloo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.loess.todoloo.utils.AuthUtils.extractUserIdFromToken;

@Tag(name = "Пользователи")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor //конструктор для инъекции сервиса
@SecurityScheme(type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT", name = "Authorization")
public class UserController {

    private final UserService userService;

//    @PutMapping("/new")
//    @Operation(summary = "Создать нового пользователя")
//    public UserInfoResponse createUser(@RequestBody UserInfoRequest request) {
//        return userService.createUser(request);
//    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить инфо о пользователе", security = @SecurityRequirement(name = "Authorization"))
    public UserInfoResponse getUser(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        return userService.getUserInfoById(extractUserIdFromToken(token), id);
    }

    @PostMapping("/{id}")
    @Operation(summary = "Редактировать пользователя", security = @SecurityRequirement(name = "Authorization"))
    public UserInfoResponse editUser(@RequestHeader("Authorization") String token, @PathVariable Long id, @RequestBody UserInfoRequest request) {
        return userService.editUser(extractUserIdFromToken(token), id,request);
    }


}
