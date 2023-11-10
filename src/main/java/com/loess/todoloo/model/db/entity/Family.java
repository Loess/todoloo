package com.loess.todoloo.model.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.loess.todoloo.model.enums.Role;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "families")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Family {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;

    @JsonIgnore
    @OneToOne
    User owner;

    @OneToMany
    @JsonManagedReference(value = "family_members")
    List<User> members;

}
