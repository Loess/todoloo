package com.loess.todoloo.service;

import com.loess.todoloo.model.dto.request.FamilyInfoRequest;
import com.loess.todoloo.model.dto.response.FamilyInfoResponse;

public interface FamilyService {
    FamilyInfoResponse createFamily(Long userId, FamilyInfoRequest request);

    FamilyInfoResponse getFamilyInfo(Long userId, Long familyId);

    FamilyInfoResponse changeFamily(Long userId, FamilyInfoRequest request);

    Boolean inviteByEmail(Long userId, String email);

    Boolean leaveFamily(Long userId);

    Boolean joinByInvite(Long userId, Long familyId);
}
