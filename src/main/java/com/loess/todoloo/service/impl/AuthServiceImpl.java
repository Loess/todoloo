package com.loess.todoloo.service.impl;

import com.loess.todoloo.exceptions.CustomException;
import com.loess.todoloo.model.auth.AuthenticationForm;
import com.loess.todoloo.model.auth.TokenDto;
import com.loess.todoloo.model.db.entity.User;
import com.loess.todoloo.model.dto.request.UserInfoRequest;
import com.loess.todoloo.service.AuthService;
import com.loess.todoloo.service.UserService;
import com.loess.todoloo.utils.EncodingUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import static com.loess.todoloo.utils.AuthUtils.getTokensJson;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;

    /**
     * Injection of manager provides the processes an {@link Authentication} request.
     */
    private final AuthenticationManager manager;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @Override
    public TokenDto login(AuthenticationForm form) {
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(form.getUsername(),
                        form.getPassword());

        manager.authenticate(token);
        User user = userService.getUserByEmail(form.getUsername());
        if (!passwordEncoder.matches(form.getPassword(), user.getPassword())) {
            throw new CustomException("wrong password!", HttpStatus.UNAUTHORIZED);
        }
        return getTokensJson(user, userService.getUserByEmail(user.getUsername()).getId());

    }

    @Override
    public TokenDto refreshToken(String refreshToken) {
        if (refreshToken != null && refreshToken.startsWith("Bearer ")) {
                User user = userService.getUserByEmail(
                        EncodingUtil.getDecodedUsername("secret", refreshToken));
                return getTokensJson(user, userService.getUserByEmail(user.getUsername()).getId());

        } else {
            throw new CustomException("Refresh token is missing", HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public TokenDto register(UserInfoRequest req) {
        String encoded = passwordEncoder.encode(req.getPassword());
        userService.createUser(UserInfoRequest.builder()
                .email(req.getEmail())
                .password(encoded)
                .role(req.getRole())
                .name(req.getName())
                .build());

        AuthenticationForm form = new AuthenticationForm();
        form.setUsername(req.getEmail());
        form.setPassword(req.getPassword());
        return login(form);

    }

}
