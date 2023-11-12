package com.loess.todoloo.model.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.loess.todoloo.model.dto.request.RewardInfoRequest;
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
@JsonIgnoreProperties(ignoreUnknown = true) //for mapper tests get working at empty fields
public class RewardInfoResponse extends RewardInfoRequest {

    Long id;
    LocalDateTime finishedDate;

}
