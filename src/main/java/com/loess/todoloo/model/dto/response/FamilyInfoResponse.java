package com.loess.todoloo.model.dto.response;

import com.loess.todoloo.model.db.entity.User;
import com.loess.todoloo.model.dto.request.FamilyInfoRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FamilyInfoResponse extends FamilyInfoRequest {

    Long id;
    UserInfoResponse owner;
    List<UserInfoResponse> members;

}
