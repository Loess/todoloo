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

import static com.loess.todoloo.utils.AuthUtils.extractUserIdFromToken;

@Tag(name = "Награды")
@RestController
@RequestMapping("/rewards")
@RequiredArgsConstructor //конструктор для инъекции сервиса
@SecurityRequirement(name = "Authorization")
public class RewardController {

    private final RewardService rewardService;

    @GetMapping
    @Operation(summary = "Посмотреть активные награды пользователя")
    public List<RewardInfoResponse> getUserActiveRewards(@RequestHeader("Authorization") String token) {
        return rewardService.getUserActiveRewards(extractUserIdFromToken(token));
    }

    @GetMapping("/all")
    @Operation(summary = "Посмотреть все награды пользователя")
    public List<RewardInfoResponse> getAllUserRewards(@RequestHeader("Authorization") String token) {
        return rewardService.getAllUserRewards(extractUserIdFromToken(token));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Посмотреть описание награды по ID")
    public RewardInfoResponse getRewardDTOById(@RequestHeader("Authorization") String token, @PathVariable("id") Long rewardId) {
        return rewardService.getRewardDTOById(extractUserIdFromToken(token), rewardId);
    }

    @PutMapping("/new")
    @Operation(summary = "Добавить цель")
    public RewardInfoResponse addReward(@RequestHeader("Authorization") String token, @RequestBody RewardInfoRequest request) {
        return rewardService.addReward(extractUserIdFromToken(token), request);
    }

    @PostMapping("/edit/{id}")
    @Operation(summary = "Редактировать награду по ID")
    public RewardInfoResponse editRewardById(@RequestHeader("Authorization") String token,
                                             @PathVariable("id") Long rewardId,
                                             @RequestBody RewardInfoRequest request) {
        return rewardService.editRewardById(extractUserIdFromToken(token), rewardId, request);
    }

    @PostMapping("/finish/{id}")
    @Operation(summary = "Получить награду, отметить завершенной")
    public RewardInfoResponse finishReward(@RequestHeader("Authorization") String token, @PathVariable("id") Long rewardId) {
        return rewardService.finishReward(extractUserIdFromToken(token), rewardId);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить награду")
    public boolean deleteReward(@RequestHeader("Authorization") String token, @PathVariable("id") Long rewardId) {
        return rewardService.deleteReward(extractUserIdFromToken(token), rewardId);
    }

}
