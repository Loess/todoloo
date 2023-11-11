package com.loess.todoloo.model.dto.response;

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

    public UserInfoResponse(Long id) {
        this.id = id;
    }

    Long id;
    Integer rewardBalance;
    String email;
    String name;
    FamilyInfoResponse family;
    Role role;

}
