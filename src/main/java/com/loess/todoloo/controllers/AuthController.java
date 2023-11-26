package com.loess.todoloo.controllers;

import com.loess.todoloo.model.auth.AuthenticationForm;
import com.loess.todoloo.model.auth.TokenDto;
import com.loess.todoloo.model.dto.request.UserInfoRequest;
import com.loess.todoloo.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication controller
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/token/refresh")
    @Operation(summary = "Refresh token")
    public TokenDto refreshToken(@RequestHeader("refresh-token") String refreshToken) {
        return authService.refreshToken(refreshToken);
    }

    @PostMapping("/login")
    @Operation(summary = "User authentication")
    public TokenDto login(@RequestBody AuthenticationForm form) {

        return authService.login(form);
    }

    @PostMapping(value = "/register")
    @Operation(summary = "User registration")
    public TokenDto register(@RequestBody UserInfoRequest form) {

        return authService.register(form);
    }

}
