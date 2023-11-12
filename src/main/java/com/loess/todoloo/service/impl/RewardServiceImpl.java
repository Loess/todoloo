package com.loess.todoloo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loess.todoloo.exceptions.CustomException;
import com.loess.todoloo.model.db.entity.Reward;
import com.loess.todoloo.model.db.entity.User;
import com.loess.todoloo.model.db.repository.RewardRepo;
import com.loess.todoloo.model.dto.request.RewardInfoRequest;
import com.loess.todoloo.model.dto.response.RewardInfoResponse;
import com.loess.todoloo.model.enums.Role;
import com.loess.todoloo.service.NotificationService;
import com.loess.todoloo.service.RewardService;
import com.loess.todoloo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RewardServiceImpl implements RewardService {

    private final ObjectMapper mapper;
    private final UserService userService;
    private final NotificationService notificationService;
    private final RewardRepo rewardRepo;

    @Override
    public List<RewardInfoResponse> getUserActiveRewards(Long userId) {
        User user = userService.getUserById(userId);
        List<Reward> activeRewards = rewardRepo.findByAssigneeAndFinished(user, false);
        return activeRewards.stream()
                .map(reward -> {
                    RewardInfoResponse response = mapper.convertValue(reward, RewardInfoResponse.class);
                    response.setAssigneeId(reward.getAssignee().getId());
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<RewardInfoResponse> getAllUserRewards(Long userId) {
        User user = userService.getUserById(userId);
        List<Reward> allRewards = rewardRepo.findByAssignee(user);
        return allRewards.stream()
                .map(reward -> {
                    RewardInfoResponse response = mapper.convertValue(reward, RewardInfoResponse.class);
                    response.setAssigneeId(reward.getAssignee().getId());
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public RewardInfoResponse getRewardDTOById(Long userId, Long rewardId) {
        Reward rew = validateUserRightsForReward(userId, rewardId);
        RewardInfoResponse response = mapper.convertValue(rew, RewardInfoResponse.class);
        response.setAssigneeId(rew.getAssignee().getId());
        return response;
    }

    private Reward getRewardById(Long rewardId) {
        return rewardRepo.findById(rewardId)
                .orElseThrow(() -> new CustomException("Reward not found", HttpStatus.NOT_FOUND));
    }

    @Override
    public RewardInfoResponse addReward(Long userId, RewardInfoRequest request) {
        User user = userService.getUserById(userId);
        User assignee = userService.getUserById(request.getAssigneeId());
        if (!userId.equals(request.getAssigneeId()) && // не для себя
                !(user.getFamily() != null &&
                        user.getFamily().equals(assignee.getFamily()) && // Оба пользователи в одной семье
                        user.getRole() == Role.PARENT && assignee.getRole() == Role.KID) // Создающий - родитель, для ребенка
        ) {
            throw new CustomException("You have no rights to create a reward for this user", HttpStatus.FORBIDDEN);
        }

        Reward reward = new Reward();
        reward.setAssignee(assignee);
        reward.setSummary(request.getSummary());
        if (!request.getRewardPrice().equals(0) && user.getFamily() != null && assignee.getRole() == Role.KID) {
            throw new CustomException("You have no rights to set reward amount", HttpStatus.FORBIDDEN);
        } else {
            reward.setRewardPrice(request.getRewardPrice());
        }
        reward.setFinished(request.getFinished());
        reward.setFinishedDate(request.getFinished() ? LocalDateTime.now() : null);
        Reward savedReward = rewardRepo.save(reward);

        RewardInfoResponse response = new RewardInfoResponse();
        response.setId(savedReward.getId());
        response.setAssigneeId(savedReward.getAssignee().getId());
        response.setRewardPrice(savedReward.getRewardPrice());
        response.setSummary(savedReward.getSummary());
        response.setFinishedDate(savedReward.getFinishedDate());
        response.setFinished(savedReward.getFinished());

        return response;
    }

    @Override
    public RewardInfoResponse editRewardById(Long userId, Long rewardId, RewardInfoRequest request) {
        Reward reward = validateUserRightsForReward(userId, rewardId);
        User newAssignee = userService.getUserById(request.getAssigneeId());
        User user = userService.getUserById(userId);
        if (!request.getRewardPrice().equals(reward.getRewardPrice()) && //если меняет прайс
                user.getFamily() != null && //в семье
                user.getRole() == Role.KID){
            throw new CustomException("You have no rights to set reward amount", HttpStatus.FORBIDDEN);
        } else {
            reward.setRewardPrice(request.getRewardPrice());
        }

        //check allowed assignees
        if (!request.getAssigneeId().equals(reward.getAssignee().getId()) && //если меняет assignee
                (user.getFamily() == null || user.getRole() == Role.KID || //нельзя менять безсемейным и детям
                        !(user.getFamily().equals(newAssignee.getFamily())))) // и разнофамильцам
        {
            throw new CustomException("You cannot assign task to this user", HttpStatus.FORBIDDEN);
        } else {
            reward.setAssignee(userService.getUserById(request.getAssigneeId()));
        }

        reward.setSummary(request.getSummary());
        reward.setFinished(request.getFinished());
        reward.setFinishedDate(Boolean.TRUE.equals(request.getFinished()) ? LocalDateTime.now() : null);
        Reward savedReward = rewardRepo.save(reward);

        RewardInfoResponse response = mapper.convertValue(savedReward, RewardInfoResponse.class);
        response.setAssigneeId(savedReward.getAssignee().getId());

        return response;
    }

    @Override
    public RewardInfoResponse finishReward(Long userId, Long rewardId) {
        Reward rew = validateUserRightsForReward(userId, rewardId);
        if (rew.getFinished())
            throw new CustomException("Reward is already taken", HttpStatus.FORBIDDEN);
        if (userService.getUserById(userId).getRewardBalance() < rew.getRewardPrice()){
            throw new CustomException("You have not enough points to get this reward", HttpStatus.FORBIDDEN);
        }
        User assignee = rew.getAssignee();
        assignee.setRewardBalance(assignee.getRewardBalance() - rew.getRewardPrice());
        rew.setFinished(true);
        rew.setFinishedDate(LocalDateTime.now());

        userService.updateUser(assignee);
        Reward saved = rewardRepo.save(rew);
        RewardInfoResponse response = mapper.convertValue(saved, RewardInfoResponse.class);
        response.setAssigneeId(saved.getAssignee().getId());
        return response;
    }

    @Override
    public boolean deleteReward(Long userId, Long rewardId) {
        Reward rew = validateUserRightsForReward(userId, rewardId);
        rewardRepo.delete(rew);
        return true;
    }

    private Reward validateUserRightsForReward(Long userId, Long rewardId) {
        User user = userService.getUserById(userId);
        Reward reward = getRewardById(rewardId);
        if (!user.equals(reward.getAssignee()) && // не сам
                !(user.getFamily() != null &&
                        user.getFamily().equals(reward.getAssignee().getFamily()) && // Оба пользователи в одной семье
                        user.getRole() == Role.PARENT && reward.getAssignee().getRole() == Role.KID) // редактор - родитель, для ребенка
        ) {
            throw new CustomException("You have no rights to perform this operation", HttpStatus.FORBIDDEN);
        }
        return reward;
    }

}
