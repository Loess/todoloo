package com.loess.todoloo.model.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.loess.todoloo.model.db.entity.User;
import com.loess.todoloo.model.enums.TaskStatus;
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
public class TaskInfoRequest {

    TaskStatus status;
    Boolean needVerify;
    String summary;
    String description;
    Long assigneeId;
    String textReward;
    Integer rewardAmount;
    Integer priority;
    String rightAnswers;

//	creation_date status_date author id
//	status need_verify summary description assignee
//	instant_reward reward_amount priority right_answers

}
