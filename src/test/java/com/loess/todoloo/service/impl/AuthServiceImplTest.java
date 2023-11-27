package com.loess.todoloo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loess.todoloo.exceptions.CustomException;
import com.loess.todoloo.model.auth.AuthenticationForm;
import com.loess.todoloo.model.auth.TokenDto;
import com.loess.todoloo.model.db.entity.User;
import com.loess.todoloo.model.dto.request.UserInfoRequest;
import com.loess.todoloo.model.dto.response.UserInfoResponse;
import com.loess.todoloo.model.enums.Role;
import com.loess.todoloo.service.UserService;
import com.loess.todoloo.utils.AuthUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import com.loess.todoloo.utils.EncodingUtil;

import static com.loess.todoloo.utils.AuthUtils.getTokensJson;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuthServiceImplTest {

    @Mock
    private UserService userService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private AuthUtils authUtils;

    @Mock
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @InjectMocks
    private AuthServiceImpl authService;

    @Spy
    private ObjectMapper mapper;

    @Test
    public void login() {

        AuthenticationForm form = new AuthenticationForm();
        form.setUsername("username@mail.com");
        form.setPassword("password");

        User user = new User();
        user.setId(1L);
        user.setRole(Role.KID);
        user.setEmail("username@mail.com");
        user.setPassword(new BCryptPasswordEncoder().encode("password"));

        when(userService.getUserByEmail(anyString())).thenReturn(user);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);

        TokenDto result = authService.login(form);

        assertNotNull(result);
    }

    @Test(expected = CustomException.class)
    public void login_wrongPass() {

        AuthenticationForm form = new AuthenticationForm();
        form.setUsername("username@mail.com");
        form.setPassword("password_wrong");

        User user = new User();
        user.setId(1L);
        user.setRole(Role.KID);
        user.setEmail("username@mail.com");
        user.setPassword(new BCryptPasswordEncoder().encode("password"));

        when(userService.getUserByEmail(anyString())).thenReturn(user);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);

        TokenDto result = authService.login(form);

        assertNotNull(result);
    }

    @Test
    public void refreshToken() {
        String refreshToken = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJlQG1haWwuY29tIiwiaXNzIjoiYXV0aCBzZXJ2ZXIiLCJleHAiOjE3MDExMTM2NzJ9.V26LrDL8AO1ZKNTmwoIWkHJQM6m_Ga-tYsE5aofFeSU";
        User user = new User();
        user.setId(1L);
        user.setRole(Role.KID);
        user.setEmail("username@mail.com");
        user.setPassword(new BCryptPasswordEncoder().encode("password"));

        when(userService.getUserByEmail(anyString())).thenReturn(user);

        TokenDto result = authService.refreshToken(refreshToken);

        assertNotNull(result);
    }

    @Test(expected = CustomException.class)
    public void refreshToken_missingToken() {
        authService.refreshToken(null);
    }

    @Test
    public void register() {
        UserInfoRequest userInfoRequest = new UserInfoRequest();
        userInfoRequest.setEmail("username@mail.com");
        userInfoRequest.setRole(Role.KID);
        userInfoRequest.setName("vasya");
        userInfoRequest.setPassword("password");

        User user = new User();
        user.setId(1L);
        user.setRole(Role.KID);
        user.setEmail("username@mail.com");
        user.setPassword(new BCryptPasswordEncoder().encode("password"));

        when(userService.createUser(any(UserInfoRequest.class))).thenReturn(new UserInfoResponse());
        when(userService.getUserByEmail(any(String.class))).thenReturn(user);

        TokenDto result = authService.register(userInfoRequest);

        assertNotNull(result);
    }
}