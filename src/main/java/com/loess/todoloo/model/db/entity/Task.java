package com.loess.todoloo.model.db.entity;

import com.fasterxml.jackson.annotation.*;
import com.loess.todoloo.model.enums.TaskStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "tasks")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JsonBackReference(value = "author_tasks")
    @JsonIgnore // Исключить поле из маппинга, иначе Cannot construct instance of `com.loess.todoloo.model.db.entity.User` (although at least one Creator exists): no long/Long-argument constructor/factory method to deserialize from Number value (0)
    User author;

    //@Column(name = "creation_date")
    LocalDateTime creationDate;
    //@Column(name = "status_date")
    LocalDateTime statusDate;

    @Enumerated(EnumType.STRING)
    TaskStatus status;
    Boolean needVerify;
    String summary;
    String description;

    @ManyToOne
    @JsonBackReference(value = "assigned_tasks")
    @JsonIgnore // Исключить поле из маппинга
    User assignee;
    String textReward;
    Integer rewardAmount;
    Integer priority;
    String rightAnswers;

}
