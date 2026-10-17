package top.creativelab.role.adapters.outbound.persistence.permission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PermissionJpaRepository extends JpaRepository<PermissionJpaEntity, UUID> {
    List<PermissionJpaEntity> findByNameContainingIgnoreCase(String name);
}
