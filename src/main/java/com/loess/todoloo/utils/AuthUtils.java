package com.loess.todoloo.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.loess.todoloo.model.auth.TokenDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loess.todoloo.service.UserService;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthUtils {

    /**
     * Method that send the current error appeared while authentication in process
     *
     * @param response the {@link HttpServletResponse} response
     * @param e        the Exception value
     *
     * @throws IOException if an input or output exception occurred
     */
    public static void sendAuthError(HttpServletResponse response, Exception e) throws
            IOException {
        response.setHeader("error", e.getMessage());
        response.setStatus(FORBIDDEN.value());
        Map<String, String> error = new HashMap<>();
        error.put("error_message", e.getMessage());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), error);
    }

    public static TokenDto getTokensJson(UserDetails user, Long userid) {
        Algorithm algorithm = EncodingUtil.getAlgorithm("secret");

        Date accessExpiresAt = new Date(System.currentTimeMillis() + 120 * 60 * 1000);
        String accessToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(accessExpiresAt)
                .withIssuer("auth server")
                .withClaim("roles", user.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .withClaim("userid", userid)
                .sign(algorithm);

        Date refreshExpiresAt = new Date(System.currentTimeMillis() + 180 * 60 * 1000);
        String refreshToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(refreshExpiresAt)
                .withIssuer("auth server")
                .sign(algorithm);

        return TokenDto.builder()
                .accessToken(accessToken)
                .accessTokenTTL(accessExpiresAt.toInstant())
                .refreshToken(refreshToken)
                .refreshTokenTTL(refreshExpiresAt.toInstant())
                .build();
    }

}
