package com.loess.todoloo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loess.todoloo.exceptions.CustomException;
import com.loess.todoloo.model.db.entity.Family;
import com.loess.todoloo.model.db.entity.Reward;
import com.loess.todoloo.model.db.entity.User;
import com.loess.todoloo.model.db.repository.RewardRepo;
import com.loess.todoloo.model.dto.request.RewardInfoRequest;
import com.loess.todoloo.model.dto.response.RewardInfoResponse;
import com.loess.todoloo.model.enums.Role;
import com.loess.todoloo.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RewardServiceImplTest {

    @InjectMocks
    private RewardServiceImpl rewardService;

    @Mock
    private RewardRepo rewardRepo;
    @Mock
    private UserService userService;

    @Spy
    private ObjectMapper mapper;

    @Test
    public void getUserActiveRewards() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        Reward reward = new Reward();
        reward.setAssignee(user);

        List<Reward> activeRewards = new ArrayList<>(List.of(reward));

        when(userService.getUserById(userId)).thenReturn(user);
        when(rewardRepo.findByAssigneeAndFinished(user, false)).thenReturn(activeRewards);

        List<RewardInfoResponse> result = rewardService.getUserActiveRewards(userId);

        assertNotNull(result);
        assertEquals(userId, result.get(0).getAssigneeId());
        verify(userService, times(1)).getUserById(userId);
        verify(rewardRepo).findByAssigneeAndFinished(user, false);
    }

    @Test
    public void getAllUserRewards() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        Reward reward = new Reward();
        reward.setAssignee(user);

        List<Reward> activeRewards = new ArrayList<>(List.of(reward));

        when(userService.getUserById(userId)).thenReturn(user);
        when(rewardRepo.findByAssignee(user)).thenReturn(activeRewards);

        List<RewardInfoResponse> result = rewardService.getAllUserRewards(userId);

        assertNotNull(result);
        assertEquals(userId, result.get(0).getAssigneeId());
        verify(userService, times(1)).getUserById(userId);
        verify(rewardRepo).findByAssignee(user);
    }

    @Test
    public void getRewardDTOById() {
        Long userId = 1L;
        Long rewardId = 10L;
        User user = new User();
        user.setId(userId);

        Reward reward = new Reward();
        reward.setId(rewardId);
        reward.setAssignee(user);

        when(userService.getUserById(userId)).thenReturn(user);
        when(rewardRepo.findById(rewardId)).thenReturn(Optional.of(reward));

        RewardInfoResponse result = rewardService.getRewardDTOById(userId, rewardId);

        assertEquals(rewardId, result.getId());
        assertEquals(userId, result.getAssigneeId());

        verify(rewardRepo).findById(rewardId);
    }

    @Test(expected = CustomException.class)
    public void getRewardDTOById_validateUserRightsForReward_wrongRights() {
        Long userId = 1L;
        Long userId2 = 111L;
        Long rewardId = 10L;
        User user1 = new User();
        user1.setId(userId);
        user1.setRole(Role.KID);
        User user2 = new User();
        user2.setId(userId2);
        user2.setRole(Role.KID);
        Family family = new Family();
        user1.setFamily(family);
        user2.setFamily(family);

        Reward reward = new Reward();
        reward.setId(rewardId);
        reward.setAssignee(user2);

        when(userService.getUserById(userId)).thenReturn(user1);
        when(rewardRepo.findById(rewardId)).thenReturn(Optional.of(reward));

        rewardService.getRewardDTOById(userId, rewardId);
    }

    @Test
    public void addReward() {
        Long userId = 1L;
        Long assigneeId = 2L;
        Family family = new Family();
        User user1 = new User();
        user1.setId(userId);
        user1.setFamily(family);
        user1.setRole(Role.PARENT);
        User user2 = new User();
        user2.setId(assigneeId);
        user2.setFamily(family);
        user2.setRole(Role.KID);

        RewardInfoRequest request = new RewardInfoRequest();
        request.setAssigneeId(assigneeId);
        request.setSummary("Test Reward");
        request.setRewardPrice(50);
        request.setFinished(false);

        when(userService.getUserById(userId)).thenReturn(user1);
        when(userService.getUserById(assigneeId)).thenReturn(user2);
        when(rewardRepo.save(any(Reward.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Мок сохранения и возврат переданного объекта

        RewardInfoResponse result = rewardService.addReward(userId, request);

        verify(userService).getUserById(userId);
        verify(userService).getUserById(assigneeId);
        verify(rewardRepo).save(any(Reward.class));

        // Assert something about the result if necessary
        assertNotNull(result);
        // Add assertions about the result according to your requirements

    }

    @Test
    public void editRewardById() {
        Long userId = 1L;
        Long rewardId = 2L;
        Long newAssigneeId = 3L;
        User user = new User();
        user.setId(userId);
        user.setRole(Role.PARENT);

        Reward reward = new Reward();
        reward.setId(rewardId);
        reward.setAssignee(user);

        RewardInfoRequest request = new RewardInfoRequest();
        request.setAssigneeId(newAssigneeId);
        request.setSummary("Updated Reward");
        request.setRewardPrice(101);
        request.setFinished(true);

        User assignee = new User();
        assignee.setId(newAssigneeId);

        Family family = new Family();
        family.setId(1L);

        user.setFamily(family);
        assignee.setFamily(family);

        when(userService.getUserById(userId)).thenReturn(user);
        when(userService.getUserById(newAssigneeId)).thenReturn(assignee);
        when(rewardRepo.findById(rewardId)).thenReturn(Optional.of(reward));
        when(rewardRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0)); // Мок сохранения и возврат переданного объекта

        RewardInfoResponse result = rewardService.editRewardById(userId, rewardId, request);

        verify(userService, times(4)).getUserById(anyLong());
        verify(rewardRepo, times(1)).findById(rewardId);
        verify(rewardRepo, times(1)).save(any(Reward.class));

        assertNotNull(result);
        assertEquals("Updated Reward", result.getSummary());
    }

    @Test
    public void finishReward() {
        Long userId = 1L;
        Long rewardId = 10L;
        User user1 = new User();
        user1.setRewardBalance(101);
        Reward reward = new Reward();
        reward.setId(rewardId);
        reward.setAssignee(user1);
        reward.setRewardPrice(100);
        reward.setFinished(false);

        when(userService.getUserById(userId)).thenReturn(user1);
        when(rewardRepo.findById(rewardId)).thenReturn(Optional.of(reward));
        when(rewardRepo.save(any(Reward.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Мок сохранения и возврат переданного объекта

        RewardInfoResponse result = rewardService.finishReward(userId, rewardId);

        verify(rewardRepo, times(1)).save(reward);
        verify(userService).updateUser(user1);

        assertNotNull(result);
    }

    @Test
    public void deleteReward() {
        Long userId = 1L;
        Long rewardId = 10L;
        User user1 = new User();
        Reward reward = new Reward();
        reward.setId(rewardId);
        reward.setAssignee(user1);

        when(userService.getUserById(userId)).thenReturn(user1);
        when(rewardRepo.findById(rewardId)).thenReturn(Optional.of(reward));

        boolean result = rewardService.deleteReward(userId, rewardId);

        verify(rewardRepo, times(1)).delete(any(Reward.class));
        assertTrue(result);
    }
}