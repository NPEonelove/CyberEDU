package org.npeonelove.backend.model.scenario;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class Type {

    @Id
    @Column(name = "type_id")
    private UUID typeId;

    @Column(name = "title")
    private String title;

    @OneToMany(mappedBy = "type")
    @ToString.Exclude
    private List<Scenario> scenarios;

}
