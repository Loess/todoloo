package com.loess.todoloo.model.dto.response;

import com.fasterxml.jackson.annotation.*;
import com.loess.todoloo.model.db.entity.User;
import com.loess.todoloo.model.dto.request.TaskInfoRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class TaskInfoResponse extends TaskInfoRequest {

    Long id;
    LocalDateTime creationDate;
    LocalDateTime statusDate;
    UserInfoResponse author;
    UserInfoResponse assignee;

}
