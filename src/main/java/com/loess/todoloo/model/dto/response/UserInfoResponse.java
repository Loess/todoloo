package com.loess.todoloo.model.dto.response;

import com.loess.todoloo.model.db.entity.Family;
import com.loess.todoloo.model.dto.request.UserInfoRequest;
import com.loess.todoloo.model.enums.Role;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserInfoResponse {

    Long id;
    Integer rewardBalance;
    String email;
    String name;
    FamilyInfoResponse family;
    Role role;

}
