package top.creativelab.role.adapters.outbound.persistence.resource;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import top.creativelab.role.domain.Resource;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResourceJpaRepository extends JpaRepository<ResourceJpaEntity, UUID> {
    Optional<ResourceJpaEntity> findByName(String name);
}
