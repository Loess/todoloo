package com.loess.todoloo.model.auth;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TokenDto {

    String accessToken;
    Instant accessTokenTTL;
    String refreshToken;
    Instant refreshTokenTTL;

}
