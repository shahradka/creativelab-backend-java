package top.creativelab.user.adapters.outbound.persistence.userRole;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRoleJpaRepository extends JpaRepository<UserRoleJpaEntity, UUID> {

    List<UserRoleJpaEntity> findByUserId(UUID userId);

    List<UserRoleJpaEntity> findByRoleId(UUID roleId);

    Optional<UserRoleJpaEntity> findByUserIdAndRoleId(UUID userId, UUID roleId);

    boolean existsByUserIdAndRoleId(UUID userId, UUID roleId);

    long countByUserId(UUID userId);

    void deleteByUserIdAndRoleId(UUID userId, UUID roleId);

    void deleteByUserId(UUID userId);
}
