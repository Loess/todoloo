package com.loess.todoloo.controllers;

import com.loess.todoloo.model.dto.request.FamilyInfoRequest;
import com.loess.todoloo.model.dto.request.TaskInfoRequest;
import com.loess.todoloo.model.dto.request.UserInfoRequest;
import com.loess.todoloo.model.dto.response.FamilyInfoResponse;
import com.loess.todoloo.model.dto.response.TaskInfoResponse;
import com.loess.todoloo.service.FamilyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.loess.todoloo.utils.AuthUtils.extractUserIdFromToken;

@Tag(name = "Семьи")
@RestController
@RequestMapping("/family")
@RequiredArgsConstructor //конструктор для инъекции сервиса
@SecurityRequirement(name = "Authorization")
public class FamilyController {

    private final FamilyService familyService;

    @PutMapping("/new")
    @Operation(summary = "Создать новую семью")
    public FamilyInfoResponse createFamily(@RequestHeader("Authorization") String token, @RequestBody FamilyInfoRequest request) {
        return familyService.createFamily(extractUserIdFromToken(token), request);
    }

    @Operation(summary = "Получить инфо по ID")
    @GetMapping("/{familyId}")
    public FamilyInfoResponse getFamilyInfo(@RequestHeader("Authorization") String token, @PathVariable Long familyId) {
        return familyService.getFamilyInfo(extractUserIdFromToken(token), familyId);
    }

    @Operation(summary = "Изменить название или владельца семьи")
    @PatchMapping("/change")
    public FamilyInfoResponse getFamilyInfo(@RequestHeader("Authorization") String token,
                                            @RequestBody FamilyInfoRequest request) {
        return familyService.changeFamily(extractUserIdFromToken(token), request);
    }

    @Operation(summary = "Пригласить пользователя по email")
    @PostMapping("/invite")
    public Boolean inviteByEmail(@RequestHeader("Authorization") String token, @RequestBody String email) {
        return familyService.inviteByEmail(extractUserIdFromToken(token), email);
    }

    @Operation(summary = "Присоединиться по приглашению")
    @PostMapping("/join/{familyId}")
    public Boolean joinByInvite(@RequestHeader("Authorization") String token, @PathVariable Long familyId) {
        return familyService.joinByInvite(extractUserIdFromToken(token), familyId);
    }

    @Operation(summary = "Выйти из семьи")
    @PostMapping("/leave")
    public Boolean leaveFamily(@RequestHeader("Authorization") String token) {
        return familyService.leaveFamily(extractUserIdFromToken(token));
    }

}
