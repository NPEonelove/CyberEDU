package org.npeonelove.backend.model.user;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CurrentTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class User {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username")
    private String username;

    @Column(name = "age")
    private Integer age;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode")
    private UserMode mode;

    @Column(name = "experience")
    private Integer experience;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRole role;

    @CurrentTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
