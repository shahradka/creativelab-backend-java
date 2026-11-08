package top.creativelab.user.ports.outbound.userRole;

import top.creativelab.user.domain.userRole.UserRole;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Outbound port describing the persistence operations required by the user/role assignment
 * use cases.
 */
public interface UserRoleRepository {

    UserRole save(UserRole userRole);

    List<UserRole> findAll();

    Optional<UserRole> findById(UUID id);

    List<UserRole> findByUserId(UUID userId);

    List<UserRole> findByRoleId(UUID roleId);

    Optional<UserRole> findByUserIdAndRoleId(UUID userId, UUID roleId);

    boolean existsByUserIdAndRoleId(UUID userId, UUID roleId);

    long countByUserId(UUID userId);

    void deleteById(UUID id);

    void deleteByUserIdAndRoleId(UUID userId, UUID roleId);

    void deleteByUserId(UUID userId);
}
