package com.loess.todoloo.controllers;

import com.loess.todoloo.model.dto.request.FamilyInfoRequest;
import com.loess.todoloo.model.dto.request.TaskInfoRequest;
import com.loess.todoloo.model.dto.request.UserInfoRequest;
import com.loess.todoloo.model.dto.response.FamilyInfoResponse;
import com.loess.todoloo.model.dto.response.TaskInfoResponse;
import com.loess.todoloo.service.FamilyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Семьи")
@RestController
@RequestMapping("/family")
@RequiredArgsConstructor //конструктор для инъекции сервиса
public class FamilyController {

    private final FamilyService familyService;

    @PostMapping("/new")
    @Operation(summary = "Создать новую семью")
    public FamilyInfoResponse createFamily(@RequestHeader("userid") Long userId, @RequestBody FamilyInfoRequest request) {
        return familyService.createFamily(userId,request);
    }

    @Operation(summary = "Получить инфо по ID")
    @GetMapping("/{familyId}")
    public FamilyInfoResponse getFamilyInfo(@RequestHeader("userid") Long userId, @PathVariable Long familyId) {
        return familyService.getFamilyInfo(userId, familyId);
    }

    @Operation(summary = "Изменить название или владельца семьи")
    @PatchMapping("/change")
    public FamilyInfoResponse getFamilyInfo(@RequestHeader("userid") Long userId,
                                            @RequestBody FamilyInfoRequest request) {
        return familyService.changeFamily(userId, request);
    }

    @Operation(summary = "Пригласить пользователя по email")
    @PostMapping("/invite")
    public Boolean inviteByEmail(@RequestHeader("userid") Long userId, @RequestBody String email) {
        return familyService.inviteByEmail(userId, email);
    }

    @Operation(summary = "Присоединиться по приглашению")
    @PostMapping("/join/{familyId}")
    public Boolean joinByInvite(@RequestHeader("userid") Long userId, @PathVariable Long familyId) {
        return familyService.joinByInvite(userId, familyId);
    }

    @Operation(summary = "Выйти из семьи")
    @PostMapping("/leave")
    public Boolean leaveFamily(@RequestHeader("userid") Long userId) {
        return familyService.leaveFamily(userId);
    }

}
