package com.loess.todoloo.model.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true) //for mapper tests get working at empty fields
public class RewardInfoRequest {

    Long assigneeId;
    Integer rewardPrice;
    String summary;
    Boolean finished;

}
