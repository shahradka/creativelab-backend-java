package top.creativelab.role.adapters.outbound.persistence.role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RoleJpaRepository extends JpaRepository<RoleJpaEntity, UUID> {
    List<RoleJpaEntity> findByNameContainingIgnoreCase(String name);
}
