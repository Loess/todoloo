package com.loess.todoloo.model.db.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.loess.todoloo.model.enums.Role;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User implements UserDetails {

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

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL) //ссылаемся на поле author entity Task
    List<Task> authorOf;

    @OneToMany(mappedBy = "assignee", cascade = CascadeType.ALL) //ссылаемся на поле assignee entity Task
    List<Task> assignedTo;

    //mappedBy связывает с полем user в классе Notification.
    //CascadeType.ALL указывает, что операции сохранения, обновления и удаления пользователя также должны применяться к его уведомлениям
    //orphanRemoval = true гарантирует, что уведомления будут удалены из базы данных, если они больше не привязаны к пользователю.
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Notification> notifications;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    List<Invite> invites;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return role.name();
            }
        });
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
