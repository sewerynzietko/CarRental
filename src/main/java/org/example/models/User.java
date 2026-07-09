package org.example.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @EqualsAndHashCode(of = "id") @ToString(exclude = "passwordHash")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String login;

    @Column(nullable = false)
    private String passwordHash;

    @Embedded
    private Address address;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    public User copy(){
        return User.builder()
                .id(id)
                .login(login)
                .passwordHash(passwordHash)
                .address(address != null ? Address.builder()
                        .city(address.getCity())
                        .street(address.getStreet())
                        .postalCode(address.getPostalCode())
                        .build() : null)
                .roles(roles == null
                        ? new HashSet<>()
                        : new HashSet<>(roles))
                .build();
    }
}