package com.loess.todoloo.model.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "rewards")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Reward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String summary;
    Integer rewardPrice;
    Boolean finished;
    LocalDateTime finishedDate;

    @ManyToOne
    @JsonIgnore //stop recursion
    @JoinColumn(name = "assignee_id") //использщовать этот столбец для связи с User
    User assignee;

}
