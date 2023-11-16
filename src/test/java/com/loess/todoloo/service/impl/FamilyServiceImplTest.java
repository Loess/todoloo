package com.loess.todoloo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loess.todoloo.exceptions.CustomException;
import com.loess.todoloo.model.db.entity.Family;
import com.loess.todoloo.model.db.entity.Invite;
import com.loess.todoloo.model.db.entity.Notification;
import com.loess.todoloo.model.db.entity.User;
import com.loess.todoloo.model.db.repository.FamilyRepo;
import com.loess.todoloo.model.db.repository.InviteRepo;
import com.loess.todoloo.model.dto.request.FamilyInfoRequest;
import com.loess.todoloo.model.dto.response.FamilyInfoResponse;
import com.loess.todoloo.model.enums.Role;
import com.loess.todoloo.service.NotificationService;
import com.loess.todoloo.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FamilyServiceImplTest {

    @InjectMocks
    private FamilyServiceImpl familyService;

    @Mock
    private FamilyRepo familyRepo;
    @Mock
    private InviteRepo inviteRepo;
    @Mock
    private UserService userService;
    @Mock
    private NotificationService notificationService;

    @Spy
    private ObjectMapper mapper;

    @Test
    public void createFamily() {

        Long userId = 1L;
        FamilyInfoRequest request = new FamilyInfoRequest();
        request.setName("FamilyName");
        request.setOwnerId(userId);

        User user = new User();
        user.setId(userId);
        user.setRole(Role.PARENT);

        when(userService.getUserById(userId)).thenReturn(user);

        Family savedFamily = new Family();
        savedFamily.setId(1L);
        savedFamily.setName("FamilyName");

        // сохранение семьи
        when(familyRepo.save(any(Family.class))).thenReturn(savedFamily);

        // вызываем тестируемый метод
        FamilyInfoResponse response = familyService.createFamily(userId, request);

        // Проверки
        assertNotNull(response);
        assertEquals(userId, response.getOwnerId());
        assertEquals(request.getName(), response.getName());

        verify(userService, times(1)).updateUser(any(User.class));
        verify(familyRepo, times(1)).save(any(Family.class));
    }

    @Test(expected = CustomException.class)
    public void createFamily_emptyFamilyName() {

        Long userId = 1L;
        FamilyInfoRequest request = new FamilyInfoRequest();
        request.setName("");
        request.setOwnerId(userId);

        User user = new User();
        user.setId(userId);
        user.setRole(Role.PARENT);

        when(userService.getUserById(userId)).thenReturn(user);

        // вызываем тестируемый метод
        familyService.createFamily(userId, request);
    }

    @Test
    public void getFamilyInfo_userHasInvite() {
        Long userId = 1L;
        Long familyId = 2L;

        Family family = new Family();
        family.setId(familyId);

        User user = new User();
        user.setId(userId);
        user.setFamily(family); // Устанавливаем семью для пользователя

        Invite invite = new Invite();
        invite.setId(10L);
        invite.setUser(user);
        invite.setFamilyId(familyId);

        when(familyRepo.findById(familyId)).thenReturn(Optional.of(family));
        when(userService.getUserById(userId)).thenReturn(user);
        when(inviteRepo.findFirstByUserAndFamilyId(any(User.class), anyLong())).thenReturn(Optional.of(invite));

        // Вызываем тестируемый метод
        FamilyInfoResponse response = familyService.getFamilyInfo(userId, familyId);

        // Проверяем, что метод вернул FamilyInfoResponse
        assertNotNull(response);
        assertEquals(familyId, response.getId());

        // Проверяем, что методы репо были вызваны нужное количество раз с нужными параметрами
        verify(familyRepo, times(1)).findById(familyId);
        verify(userService, times(1)).getUserById(userId);
        verify(inviteRepo, times(1)).findFirstByUserAndFamilyId(user, familyId);
    }

    @Test(expected = CustomException.class)
    public void getFamilyInfo_userNotMemberNotInvited() {
        Long userId = 1L;
        Long familyId = 2L;

        Family family = new Family();
        family.setId(familyId);

        User user = new User();
        user.setId(userId);

        when(familyRepo.findById(familyId)).thenReturn(Optional.of(family));
        when(userService.getUserById(userId)).thenReturn(user);
        when(inviteRepo.findFirstByUserAndFamilyId(any(User.class), eq(familyId))).thenReturn(Optional.empty());

        // Вызываем тестируемый метод
        familyService.getFamilyInfo(userId, familyId);

    }

    @Test
    public void changeFamily() {
        Long userId = 1L;
        Long newOwnerId = 2L;

        User user = new User();
        user.setId(userId);

        User newOwner = new User();
        newOwner.setId(newOwnerId);

        Family family = new Family();
        family.setId(3L);
        family.setOwner(user); // для мока
        user.setFamily(family);

        Family family2 = new Family();
        family2.setId(3L);
        family2.setOwner(newOwner); // для мока
        family2.setName("New Family Name");
        newOwner.setFamily(family2);

        FamilyInfoRequest request = new FamilyInfoRequest();
        request.setOwnerId(newOwnerId);
        request.setName("New Family Name");

        when(userService.getUserById(userId)).thenReturn(user);
        when(userService.getUserById(newOwnerId)).thenReturn(newOwner);
        //when(familyRepo.save(any(Family.class))).thenReturn(family2);
        when(familyRepo.save(any(Family.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Мок сохранения и возврат переданного объекта


        FamilyInfoResponse response = familyService.changeFamily(userId, request);

        assertNotNull(response);
        assertEquals(request.getName(), response.getName());
        //т.к. в FamilyInfoResponse Owner не маппится - не проверить его Ид
    }

    @Test
    public void inviteByEmail() {
        Long userId = 1L;
        String email = "example@example.com";

        User userToInvite = new User();
        userToInvite.setId(2L);

        User inviter = new User();
        inviter.setId(userId);

        Family family = new Family();
        family.setId(3L);
        family.setOwner(inviter); // инвайтер - владелец семьи
        inviter.setFamily(family);

        when(userService.getUserByEmail(email)).thenReturn(userToInvite);
        when(userService.getUserById(userId)).thenReturn(inviter);
        when(inviteRepo.findFirstByUserAndFamilyId(any(User.class), anyLong())).thenReturn(Optional.empty());
        when(inviteRepo.save(any(Invite.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Мок сохранения и возврат переданного объекта
        when(notificationService.createNotification(any(User.class), anyString(), anyString())).thenReturn(new Notification());

        Boolean result = familyService.inviteByEmail(userId, email);

        assertTrue(result);
        verify(inviteRepo, times(1)).save(any(Invite.class));
    }

    @Test
    public void joinByInvite() {
        Long userId = 1L;
        Long familyId = 123L;

        User user = new User();
        user.setId(userId);

        Family family = new Family();
        family.setId(familyId);
        family.setMembers(new ArrayList<>(Arrays.asList(user)));

        Invite invite = new Invite();
        invite.setUser(user);
        invite.setFamilyId(familyId);

        when(userService.getUserById(userId)).thenReturn(user);
        when(inviteRepo.findFirstByUserAndFamilyId(user, familyId)).thenReturn(Optional.of(invite));
        when(familyRepo.findById(familyId)).thenReturn(Optional.of(family));

        // Вызов тестируемого метода
        Boolean result = familyService.joinByInvite(userId, familyId);

        assertTrue(result);
        assertEquals(family, user.getFamily());
        assertTrue(family.getMembers().contains(user));

        verify(userService, times(1)).updateUser(user);
        verify(familyRepo, times(1)).save(family);
        verify(inviteRepo, times(1)).delete(invite);
    }

    @Test
    public void leaveFamily_userIsOwnerAndOnlyMember_familyDeleted() {

        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        Family family = new Family();
        family.setId(123L);
        family.setOwner(user);
        family.setMembers(new ArrayList<>());
        family.getMembers().add(user);
        user.setFamily(family);

        // Моки
        when(userService.getUserById(userId)).thenReturn(user);

        // Вызов тестируемого метода
        Boolean result = familyService.leaveFamily(userId);

        assertTrue(result);
        assertNull(user.getFamily());

        verify(userService, times(1)).updateUser(user);
        verify(familyRepo, times(1)).delete(family);

    }

    @Test
    public void leaveFamily_userIsOwnerAndNotTheOnlyMember() {

        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        User user2 = new User();
        user2.setId(2L);

        Family family = new Family();
        family.setId(123L);
        family.setOwner(user);
        family.setMembers(new ArrayList<>());
        family.getMembers().add(user);
        family.getMembers().add(user2);
        user.setFamily(family);

        // Моки
        when(userService.getUserById(userId)).thenReturn(user);

        // Вызов тестируемого метода
        Boolean result = familyService.leaveFamily(userId);

        assertTrue(result);
        assertNull(user.getFamily());
        assertFalse(family.getMembers().contains(user));
        assertTrue(family.getMembers().contains(user2));

        verify(userService, times(1)).updateUser(user);
        verify(familyRepo, times(1)).save(family);

    }
}