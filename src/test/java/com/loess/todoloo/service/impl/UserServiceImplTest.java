package com.loess.todoloo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loess.todoloo.exceptions.CustomException;
import com.loess.todoloo.model.db.entity.Family;
import com.loess.todoloo.model.db.entity.User;
import com.loess.todoloo.model.db.repository.UserRepo;
import com.loess.todoloo.model.dto.request.UserInfoRequest;
import com.loess.todoloo.model.dto.response.UserInfoResponse;
import com.loess.todoloo.model.enums.Role;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepo userRepo;

    @Spy
    private ObjectMapper mapper;

    @Test
    public void createUser() {
        UserInfoRequest request = new UserInfoRequest();
        request.setEmail("valid@example.com");

        when(userRepo.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepo.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L); //save()
            return savedUser;
        });

        // Вызов тестируемого метода
        UserInfoResponse response = userService.createUser(request);

        assertNotNull(response);
        assertEquals("valid@example.com", response.getEmail());
        verify(userRepo, times(1)).findByEmail(anyString());
        verify(userRepo, times(1)).save(any());
    }

    @Test
    public void createUser_invalidEmail() { //junit5
        UserInfoRequest request = new UserInfoRequest();
        request.setEmail("@nonvalid.com");

        // Вызов тестируемого метода
        Assertions.assertThrows(
                CustomException.class,
                () -> userService.createUser(request),
                "Invalid email"
        );
        verifyNoInteractions(userRepo);
    }

    @Test(expected = CustomException.class)
    public void createUser_alreadyExists() { //junit4
        UserInfoRequest request = new UserInfoRequest();
        request.setEmail("abc@def.com");

        User user = new User();
        user.setEmail("abc@def.com");

        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(user));

        // Вызов тестируемого метода
        userService.createUser(request);

        verifyNoInteractions(userRepo);
    }

    @Test
    public void getUserByIdNullable() {
        Long id = -7L;
        User user = new User();
        user.setId(id);

        //when(userRepo.findById(id)).thenReturn(Optional.empty());

        User response = userService.getUserByIdNullable(id);
        assertNull(response);
    }

    @Test(expected = CustomException.class)
    public void getUserByIdNullable_notExists() {
        Long id = 1L;
        User user = new User();
        user.setId(id);

        when(userRepo.findById(id)).thenReturn(Optional.empty());

        userService.getUserByIdNullable(id);
    }

    @Test
    public void getUserByEmail() {
        String validEmail = "example@example.com";
        User user = new User();
        user.setEmail(validEmail);

        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(user));

        User find = userService.getUserByEmail(validEmail);

        assertNotNull(find);
        assertEquals(find.getEmail(), validEmail);
    }

    @Test(expected = CustomException.class)
    public void getUserByEmail_invalidEmail() {
        User find = userService.getUserByEmail("lol.com");
    }

    @Test
    public void getUserInfoById() {
        Long user1Id = 1L;
        Long user2Id = 2L;

        User user1 = new User();
        user1.setId(user1Id);
        User user2 = new User();
        user2.setId(user2Id);

        Family family1 = new Family();
        user1.setFamily(family1);
        user2.setFamily(family1);

        when(userRepo.findById(user1Id)).thenReturn(Optional.of(user1));
        when(userRepo.findById(user2Id)).thenReturn(Optional.of(user2));

        UserInfoResponse userInfoResponse = userService.getUserInfoById(user1Id, user2Id);
        assertNotNull(userInfoResponse);
    }

    @Test(expected = CustomException.class)
    public void getUserInfoById_OtherFamily() {
        Long user1Id = 1L;
        Long user2Id = 2L;

        User user1 = new User();
        user1.setId(user1Id);
        User user2 = new User();
        user2.setId(user2Id);

        Family family1 = new Family();
        user1.setFamily(family1);
        Family family2 = new Family();
        user2.setFamily(family2);

        when(userRepo.findById(user1Id)).thenReturn(Optional.of(user1));
        when(userRepo.findById(user2Id)).thenReturn(Optional.of(user2));

        userService.getUserInfoById(user1Id, user2Id);

    }

    @Test
    public void getUserInfoByIdNullable() {
        Long userId1 = 1L;
        Long UserId2 = 2L;

        User user1 = new User();
        user1.setId(userId1);
        User user2 = new User();
        user2.setId(UserId2);

        Family family = new Family();
        user1.setFamily(family);
        user2.setFamily(family);

        when(userRepo.findById(userId1)).thenReturn(Optional.of(user1));
        when(userRepo.findById(UserId2)).thenReturn(Optional.of(user2));

        UserInfoResponse userInfoResponse = userService.getUserInfoByIdNullable(userId1, UserId2);
        assertNotNull(userInfoResponse);
    }

    @Test
    public void getUserInfoByIdNullable_notInFamily() {
        Long userId1 = 1L;
        Long UserId2 = 2L;

        User user1 = new User();
        user1.setId(userId1);
        User user2 = new User();
        user2.setId(UserId2);

        Family family = new Family();
        user1.setFamily(family);
        Family family2 = new Family();
        user2.setFamily(family2);

        when(userRepo.findById(userId1)).thenReturn(Optional.of(user1));
        when(userRepo.findById(UserId2)).thenReturn(Optional.of(user2));

        UserInfoResponse userInfoResponse = userService.getUserInfoByIdNullable(userId1, UserId2);
        assertNull(userInfoResponse);
    }

    @Test
    public void updateUser() {
        User user = new User();

        when(userRepo.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L); //save()
            return savedUser;
        });

        User response = userService.updateUser(user);
        assertEquals(user, response);
    }

    @Test(expected = CustomException.class)
    public void editUser_wrongRole() {
        User user1 = new User();
        User user2 = new User();
        Family family = new Family();
        user1.setFamily(family);
        user1.setRole(Role.PARENT);
        user2.setFamily(family);
        user2.setRole(Role.PARENT);

        UserInfoRequest request = new UserInfoRequest();

        when(userRepo.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepo.findById(2L)).thenReturn(Optional.of(user2));

        UserInfoResponse response = userService.editUser(1L, 2L, request);
    }

    @Test(expected = CustomException.class)
    public void editUser_duplicatingEmail() {
        String email = "e@mail.com";
        User user1 = new User();
        User user2 = new User();
        user2.setEmail(email);

        UserInfoRequest request = new UserInfoRequest();
        request.setEmail(email);

        when(userRepo.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user2));

        UserInfoResponse response = userService.editUser(1L, 1L, request);
    }

    @Test(expected = CustomException.class)
    public void editUser_kidChangesRole() {
        User user1 = new User();
        User user2 = new User();
        Family family = new Family();
        user1.setFamily(family);
        user1.setRole(Role.KID);
        user2.setFamily(family);
        user2.setRole(Role.PARENT);

        UserInfoRequest request = new UserInfoRequest();
        request.setRole(Role.PARENT);

        when(userRepo.findById(1L)).thenReturn(Optional.of(user1));
       // when(userRepo.findById(2L)).thenReturn(Optional.of(user2));

        UserInfoResponse response = userService.editUser(1L, 1L, request);
    }

    @Test
    public void editUser() {
        User user1 = new User();
        User user2 = new User();
        Family family = new Family();
        user1.setFamily(family);
        user1.setRole(Role.PARENT);
        user2.setFamily(family);
        user2.setRole(Role.KID);

        UserInfoRequest request = new UserInfoRequest();
        request.setRole(Role.PARENT);
        request.setEmail("valid@example.com");

        when(userRepo.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepo.findById(2L)).thenReturn(Optional.of(user2));
        when(userRepo.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            return savedUser;
        });

        UserInfoResponse response = userService.editUser(1L, 2L, request);

        assertNotNull(response);
        assertEquals("valid@example.com", response.getEmail());
        assertEquals(Role.PARENT, response.getRole());
        verify(userRepo, times(1)).save(any(User.class));
    }


    @Test
    public void loadUserByUsername() {
        String email = "test@example.com";
        User mockedUser = new User();
        mockedUser.setEmail(email);
        mockedUser.setPassword("hashedPassword");
        mockedUser.setRole(Role.KID);

        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(mockedUser));

        UserDetails userDetails = userService.loadUserByUsername(email);

        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
        assertEquals("hashedPassword", userDetails.getPassword());
        assertEquals(1, userDetails.getAuthorities().size());
    }
}