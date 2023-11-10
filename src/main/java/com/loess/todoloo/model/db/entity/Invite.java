package com.loess.todoloo.model.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "invites")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Invite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Long familyId;
    Long userId;

}
