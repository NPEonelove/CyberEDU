package org.npeonelove.backend.repository;

import org.npeonelove.backend.model.scenario.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TypeRepository extends JpaRepository<Type, UUID> {
    Optional<Type> findTypeByTypeId(UUID typeId);
}
