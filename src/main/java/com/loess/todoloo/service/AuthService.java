package com.loess.todoloo.service;

import com.loess.todoloo.model.auth.AuthenticationForm;
import com.loess.todoloo.model.auth.TokenDto;
import com.loess.todoloo.model.dto.request.UserInfoRequest;

public interface AuthService {
    TokenDto login(AuthenticationForm form) ;

    TokenDto refreshToken(String refreshToken);

    //TokenDto register(AuthenticationForm form);

    TokenDto register(UserInfoRequest req);
}
