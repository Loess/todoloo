package com.loess.todoloo.model.dto.response;

import com.loess.todoloo.model.db.entity.User;
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
public class NotificationResponse {

    Long id;
    LocalDateTime creationTime;
    LocalDateTime deliveredTime;
    User user;
    String summary;
    String message;


}
