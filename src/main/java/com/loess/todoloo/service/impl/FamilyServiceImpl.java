package com.loess.todoloo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loess.todoloo.exceptions.CustomException;
import com.loess.todoloo.model.db.entity.Family;
import com.loess.todoloo.model.db.entity.Invite;
import com.loess.todoloo.model.db.entity.User;
import com.loess.todoloo.model.db.repository.FamilyRepo;
import com.loess.todoloo.model.db.repository.InviteRepo;
import com.loess.todoloo.model.dto.request.FamilyInfoRequest;
import com.loess.todoloo.model.dto.response.FamilyInfoResponse;
import com.loess.todoloo.model.enums.Role;
import com.loess.todoloo.service.FamilyService;
import com.loess.todoloo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class FamilyServiceImpl implements FamilyService {

    private final FamilyRepo familyRepo;
    private final InviteRepo inviteRepo;
    private final ObjectMapper mapper;
    private final UserService userService;

    @Override
    public FamilyInfoResponse createFamily(Long userId, FamilyInfoRequest request) {
        User user = userService.getUserById(userId);

        if (StringUtils.isBlank(request.getName()))
            throw new CustomException("Empty Family name is not accepted", HttpStatus.BAD_REQUEST);
        else if (user.getFamily() != null)
            throw new CustomException("Only one family allowed at a time, exit your current family first!", HttpStatus.BAD_REQUEST);
        else if (user.getRole() != Role.PARENT)
            throw new CustomException("Family creation available only to PARENTs", HttpStatus.BAD_REQUEST);

        Family family = mapper.convertValue(request, Family.class);
        family.setOwner(userService.getUserById(userId));
        family.setName(request.getName());
        family.setMembers(List.of(userService.getUserById(userId)));

        Family saved = familyRepo.save(family);

        user.setFamily(saved);
        userService.updateFamily(user);

        FamilyInfoResponse response = mapper.convertValue(saved, FamilyInfoResponse.class);
        response.setOwnerId(userId);
        return response;
    }

    @Override
    public FamilyInfoResponse getFamilyInfo(Long userId, Long familyId) {

        Family family = familyRepo.findById(familyId)
                .orElseThrow(() -> new CustomException("Family not found", HttpStatus.NOT_FOUND));

        User user = userService.getUserById(userId);

        //нет инвайта и не член семьи
        if (inviteRepo.findMatch(userId, familyId).isEmpty() && !Objects.equals(user.getFamily(), family))
            throw new CustomException("You have no rights to view this family info", HttpStatus.FORBIDDEN);

        return mapper.convertValue(family, FamilyInfoResponse.class);
    }

    @Override
    public FamilyInfoResponse changeFamily(Long userId, FamilyInfoRequest request) {
        User user = userService.getUserById(userId);
        User newOwner = userService.getUserById(request.getOwnerId());
        Family family = user.getFamily();
        if (family == null)
            throw new CustomException("You cannot change Family you don't have!", HttpStatus.BAD_REQUEST);
        if (family.getOwner() != user)
            throw new CustomException("You are not owner of your family", HttpStatus.BAD_REQUEST);

        family.setName(StringUtils.isBlank(request.getName()) ? family.getName() : request.getName());
        family.setOwner(newOwner);
        Family saved = familyRepo.save(family);
        return mapper.convertValue(saved, FamilyInfoResponse.class);
    }

    @Override
    public Boolean inviteByEmail(Long userId, String email) {
        User user = userService.getUserByEmail(email);
        User inviter = userService.getUserById(userId);
        Family family = inviter.getFamily();
        if (family == null)
            throw new CustomException("You cannot invite to Family you don't have!", HttpStatus.BAD_REQUEST);
        if (family.getOwner() != inviter)
            throw new CustomException("You are not owner of your family", HttpStatus.BAD_REQUEST);
        Invite invite = new Invite();
        invite.setFamilyId(family.getId());
        invite.setUserId(user.getId());
        inviteRepo.save(invite);
        return true;
    }

    @Override
    public Boolean joinByInvite(Long userId, Long familyId) {
        Invite invite = inviteRepo.findMatch(userId, familyId)
                .orElseThrow(() -> new CustomException("You have no invitation", HttpStatus.FORBIDDEN));

        User user = userService.getUserById(userId);
        if (user.getFamily() != null)
            throw new CustomException("Leave your current family first!", HttpStatus.BAD_REQUEST);

        Family family = familyRepo.findById(familyId)
                .orElseThrow(() -> new CustomException("Family not found", HttpStatus.NOT_FOUND));

        family.getMembers().add(user);
        user.setFamily(family);
        userService.updateFamily(user);
        familyRepo.save(family);
        inviteRepo.delete(invite);

        return true;
    }

    @Override
    public Boolean leaveFamily(Long userId) {
        User user = userService.getUserById(userId);

        if (user.getFamily() == null)
            throw new CustomException("You cannot leave Family you don't have!", HttpStatus.BAD_REQUEST);

        Family family = user.getFamily();
        if (family.getOwner() == user && family.getMembers().size() == 1) {
            user.setFamily(null);
            userService.updateFamily(user);
            familyRepo.delete(family);
            log.info("family" + family.getId() + " deleted");
        } else {
            user.setFamily(null);
            userService.updateFamily(user);
            if (!family.getMembers().remove(user))
                throw new CustomException("Unknown error! You are not in family", HttpStatus.BAD_REQUEST);
            familyRepo.save(family);
            log.info("Family: " + family.getId() + " left " + family.getMembers().size() + " members");
            if (family.getMembers().size() == 0) { // del zero-members family
                familyRepo.delete(family);
            }
        }
        return true;
    }

}
