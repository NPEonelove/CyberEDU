package org.npeonelove.backend.repository;

import org.npeonelove.backend.model.scenario.Scenario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ScenarioRepository extends JpaRepository<Scenario, UUID> {
    List<Scenario> findScenarioByType_TypeId(UUID typeTypeId);

    Optional<Scenario> findScenarioByScenarioId(UUID scenarioId);
}
