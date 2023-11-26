package com.loess.todoloo.controllers;

import com.loess.todoloo.model.dto.request.RewardInfoRequest;
import com.loess.todoloo.model.dto.response.RewardInfoResponse;
import com.loess.todoloo.service.RewardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Награды")
@RestController
@RequestMapping("/rewards")
@RequiredArgsConstructor //конструктор для инъекции сервиса
@SecurityRequirement(name = "Authorization")
public class RewardController {

    private final RewardService rewardService;

    @GetMapping
    @Operation(summary = "Посмотреть активные награды пользователя")
    public List<RewardInfoResponse> getUserActiveRewards(@RequestHeader("userid") Long userId) {
        return rewardService.getUserActiveRewards(userId);
    }

    @GetMapping("/all")
    @Operation(summary = "Посмотреть все награды пользователя")
    public List<RewardInfoResponse> getAllUserRewards(@RequestHeader("userid") Long userId) {
        return rewardService.getAllUserRewards(userId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Посмотреть описание награды по ID")
    public RewardInfoResponse getRewardDTOById(@RequestHeader("userid") Long userId, @PathVariable("id") Long rewardId) {
        return rewardService.getRewardDTOById(userId, rewardId);
    }

    @PutMapping("/new")
    @Operation(summary = "Добавить цель")
    public RewardInfoResponse addReward(@RequestHeader("userid") Long userId, @RequestBody RewardInfoRequest request) {
        return rewardService.addReward(userId, request);
    }

    @PostMapping("/edit/{id}")
    @Operation(summary = "Редактировать награду по ID")
    public RewardInfoResponse editRewardById(@RequestHeader("userid") Long userId,
                                             @PathVariable("id") Long rewardId,
                                             @RequestBody RewardInfoRequest request) {
        return rewardService.editRewardById(userId, rewardId, request);
    }

    @PostMapping("/finish/{id}")
    @Operation(summary = "Получить награду, отметить завершенной")
    public RewardInfoResponse finishReward(@RequestHeader("userid") Long userId, @PathVariable("id") Long rewardId) {
        return rewardService.finishReward(userId, rewardId);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить награду")
    public boolean deleteReward(@RequestHeader("userid") Long userId, @PathVariable("id") Long rewardId) {
        return rewardService.deleteReward(userId, rewardId);
    }

}
