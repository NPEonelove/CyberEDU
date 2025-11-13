package org.npeonelove.backend.model.scenario;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "scenarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class Scenario {

    @Id
    @Column(name = "scenario_id")
    private UUID scenarioId;

    @Column(name = "title")
    private String title;

    @Column(name = "text")
    private String text;

    @Column(name = "scam")
    private Boolean scam;

    @Column(name = "response")
    private String response;

    @ManyToOne
    @JoinColumn(name = "type_id")
    private Type type;

}
