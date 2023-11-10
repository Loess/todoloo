package com.loess.todoloo.service;

import com.loess.todoloo.model.db.entity.Family;
import com.loess.todoloo.model.db.entity.User;
import com.loess.todoloo.model.dto.request.UserInfoRequest;
import com.loess.todoloo.model.dto.response.UserInfoResponse;

import java.util.List;

public interface UserService {

    UserInfoResponse createUser(UserInfoRequest request);

    User getUserById(Long userId);

    User getUserByEmail(String email);

    UserInfoResponse getUserInfoById(Long userId, Long id);

    UserInfoResponse editUser(Long userId, Long id, UserInfoRequest request);

    User updateFamily(User user);

}
