package com.loess.todoloo.model.dto.response;

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
    LocalDateTime creation_date;
    Long for_user;
    String summary;
    String text;
    Boolean delivered;

}
