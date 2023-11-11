package com.loess.todoloo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loess.todoloo.exceptions.CustomException;
import com.loess.todoloo.model.db.entity.User;
import com.loess.todoloo.model.db.repository.UserRepo;
import com.loess.todoloo.model.dto.request.UserInfoRequest;
import com.loess.todoloo.model.dto.response.FamilyInfoResponse;
import com.loess.todoloo.model.dto.response.UserInfoResponse;
import com.loess.todoloo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final ObjectMapper mapper;

    @Override
    public UserInfoResponse createUser(UserInfoRequest request) {
        String email = request.getEmail();
        if (!EmailValidator.getInstance().isValid(email)) {
            throw new CustomException("Invalid email", HttpStatus.BAD_REQUEST);
        }

        userRepo.findByEmail(email).ifPresent(u -> {
            throw new CustomException("User with email " + email + " already exists", HttpStatus.BAD_REQUEST);
        });

        User user = mapper.convertValue(request, User.class);
        user.setRewardBalance(0);

        User save = userRepo.save(user);
        return mapper.convertValue(save, UserInfoResponse.class);
    }

    @Override
    public User getUserById(Long userId) {
        return userRepo.findById(userId).orElseThrow(
                () -> new CustomException("no user with id " + userId + " found", HttpStatus.BAD_REQUEST));
    }

    @Override
    public User getUserByIdNullable(Long userId) {
        if (userId == null || userId <= 0) return null;
        return getUserById(userId);
    }

    @Override
    public User getUserByEmail(String email) {
        String femail = email.replaceAll("^\"|\"$", "");
        if (!EmailValidator.getInstance().isValid(femail))
            throw new CustomException("Invalid email", HttpStatus.BAD_REQUEST);

        return userRepo.findByEmail(femail).orElseThrow(
                () -> new CustomException("User with email " + femail + " not found", HttpStatus.BAD_REQUEST));
    }

    @Override
    public UserInfoResponse getUserInfoById(Long userId, Long id) {
        //check family rights
        if ((!userId.equals(id)) && (getUserById(userId).getFamily() != getUserById(id).getFamily()) ||
                (!userId.equals(id) && getUserById(userId).getFamily() == null)
        ) {
            throw new CustomException("User is not in your family", HttpStatus.FORBIDDEN);
        }
        UserInfoResponse userInfoResponse = mapper.convertValue(getUserById(id), UserInfoResponse.class);
        userInfoResponse.setFamily(mapper.convertValue(getUserById(id).getFamily(), FamilyInfoResponse.class));
        return userInfoResponse;
    }

    @Override
    public UserInfoResponse getUserInfoByIdNullable(Long userId, Long id) {
        //check family rights
        if ((!userId.equals(id)) && (getUserById(userId).getFamily() != getUserById(id).getFamily()) ||
                (!userId.equals(id) && getUserById(userId).getFamily() == null)
        ) {
            log.warn("User is not in your family in getUserInfoById({}, {})", userId, id);
            return null;
            // при стандартном пути заведения таски это не понадобится, т.к. нет возможности указать автора не из семьи
            // но при выходе автора из семьи - задачи вышедших авторов - недоступны, 403 при запросе списка
        }
        UserInfoResponse userInfoResponse = mapper.convertValue(getUserById(id), UserInfoResponse.class);
        userInfoResponse.setFamily(mapper.convertValue(getUserById(id).getFamily(), FamilyInfoResponse.class));
        return userInfoResponse;
    }


    @Override
    public UserInfoResponse editUser(Long userId, Long id, UserInfoRequest request) {
        //todo: check rights - edit only self or family p->k
        User user = getUserById(id);

        String email = request.getEmail();
        if (StringUtils.isBlank(email) || email.equals(user.getEmail())) {
            email = user.getEmail();
        } else if (!EmailValidator.getInstance().isValid(email)) {
            throw new CustomException("Invalid email", HttpStatus.BAD_REQUEST);
        } else if (userRepo.findByEmail(email).isPresent()) {
            throw new CustomException("User with email " + email + " already exists", HttpStatus.BAD_REQUEST);
        }
        user.setEmail(email);

        user.setPassword(StringUtils.isBlank(request.getPassword()) ? user.getPassword() : request.getPassword());
        user.setName(StringUtils.isBlank(request.getName()) ? user.getName() : request.getName());

        //do not edit family. use familyService for join/leave
        //    String family;
        //todo: check rights on role - edit only (self && family=null) or family p->k
        user.setRole(request.getRole() == null ? user.getRole() : request.getRole());
        User saved = userRepo.save(user);

        return mapper.convertValue(user, UserInfoResponse.class);
    }

    @Override
    public User updateFamily(User user) {
        return userRepo.save(user);
    }

}
