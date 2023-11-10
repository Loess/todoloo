package com.loess.todoloo.model.db.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "invites")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String message;
    LocalDateTime creationTime;
    LocalDateTime deliveredTime;

    @ManyToOne
    @JoinColumn(name = "user_id") //использщовать этот столбец для связи с User
    User user;

}
