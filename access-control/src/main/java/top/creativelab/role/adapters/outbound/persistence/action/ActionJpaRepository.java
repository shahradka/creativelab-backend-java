package top.creativelab.role.adapters.outbound.persistence.action;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import top.creativelab.role.domain.Action;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ActionJpaRepository extends JpaRepository<ActionJpaEntity, UUID> {
    Optional<ActionJpaEntity> findByName(String name);
}
