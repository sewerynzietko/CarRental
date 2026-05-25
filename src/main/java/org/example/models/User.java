package org.example.models;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = "passwordHash")

@Entity
@Table(name = "users")
public class User {
    @Id
    private String id;
    private String login;
    @Column(name = "password_hash")
    private String passwordHash;
    @Enumerated(EnumType.STRING)
    private Role role;

    public User copy(){
        return User.builder()
                .id(id)
                .login(login)
                .passwordHash(passwordHash)
                .role(role)
                .build();
    }
}