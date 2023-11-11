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
    @JoinColumn(name = "author_id") //использовать этот столбец для связи с User
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    User author;

    @ManyToOne
    @JoinColumn(name = "assignee_id") //использовать этот столбец для связи с User
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    User assignee;

    //@Column(name = "creation_date")
    LocalDateTime creationDate;
    //@Column(name = "status_date")
    LocalDateTime statusDate;

    @Enumerated(EnumType.STRING)
    TaskStatus status;
    Boolean needVerify;
    String summary;
    String description;

    String textReward;
    Integer rewardAmount;
    Integer priority;
    String rightAnswers;

}
