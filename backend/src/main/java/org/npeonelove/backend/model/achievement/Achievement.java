package org.npeonelove.backend.model.achievement;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "achievements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class Achievement {

    @Id
    @Column(name = "achievement_id")
    private UUID achievementId;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "icon")
    private String icon;

    @Column(name = "required_exp")
    private Integer requiredExp;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}