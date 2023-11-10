package com.loess.todoloo.model.db.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.loess.todoloo.model.enums.Role;
import com.loess.todoloo.model.enums.TaskStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    @Column(unique = true) //уникальность на уровне объекта (Java Entity)
    String email;
    String password;
    Integer rewardBalance;
    @Enumerated(EnumType.STRING) // для хранения в бд как String
    Role role;

    @ManyToOne
    @JsonBackReference(value = "family_members")
    Family family;

    @OneToMany
    @JsonManagedReference(value = "author_tasks")
    @JsonIgnore // Исключить поле из маппинга
    List<Task> authorOf;

    @OneToMany
    @JsonManagedReference(value = "assigned_tasks")
    @JsonIgnore // Исключить поле из маппинга
    List<Task> assignedTo;

    //mappedBy связывает с полем user в классе Notification.
    //CascadeType.ALL указывает, что операции сохранения, обновления и удаления пользователя также должны применяться к его уведомлениям
    //orphanRemoval = true гарантирует, что уведомления будут удалены из базы данных, если они больше не привязаны к пользователю.
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Notification> notifications;

}
