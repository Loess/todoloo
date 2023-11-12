package com.loess.todoloo.service;

import com.loess.todoloo.model.dto.request.RewardInfoRequest;
import com.loess.todoloo.model.dto.response.RewardInfoResponse;

import java.util.List;

public interface RewardService {

    List<RewardInfoResponse> getUserActiveRewards(Long userId);

    List<RewardInfoResponse> getAllUserRewards(Long userId);

    RewardInfoResponse getRewardDTOById(Long userId, Long rewardId);

    RewardInfoResponse addReward(Long userId, RewardInfoRequest request);

    RewardInfoResponse editRewardById(Long userId, Long rewardId, RewardInfoRequest request);

    RewardInfoResponse finishReward(Long userId, Long rewardId);

    boolean deleteReward(Long userId, Long rewardId);
}
