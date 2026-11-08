package top.creativelab.user.adapters.outbound.persistence.userRole;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import persistance.BasePersistenceAdapter;
import top.creativelab.user.domain.user.vo.UserId;
import top.creativelab.user.domain.userRole.UserRole;
import top.creativelab.user.domain.userRole.vo.RoleId;
import top.creativelab.user.domain.userRole.vo.UserRoleId;
import top.creativelab.user.ports.outbound.userRole.UserRoleRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRolePersistenceAdapter
        extends BasePersistenceAdapter<UserRole, UserRoleJpaEntity, UUID>
        implements UserRoleRepository {

    public UserRolePersistenceAdapter(UserRoleJpaRepository userRoleJpaRepository) {
        super(userRoleJpaRepository);
    }

    private UserRoleJpaRepository jpaRepository() {
        return (UserRoleJpaRepository) repository;
    }

    // =====================
    // Mapping
    // =====================

    @Override
    protected UserRoleJpaEntity mapToJpaEntity(UserRole userRole) {
        return new UserRoleJpaEntity(
                userRole.getId().getValue(),
                userRole.getUserId().getValue(),
                userRole.getRoleId().getValue()
        );
    }

    @Override
    protected UserRole mapToDomain(UserRoleJpaEntity entity) {
        return UserRole.rehydrate(
                new UserRoleId(entity.getId()),
                new UserId(entity.getUserId()),
                new RoleId(entity.getRoleId())
        );
    }

    // =====================
    // Persistence
    // =====================

    @Override
    public UserRole save(UserRole userRole) {
        UUID id = userRole.getId().getValue();
        UUID userId = userRole.getUserId().getValue();
        UUID roleId = userRole.getRoleId().getValue();

        // Load the managed entity when it already exists so that JPA auditing keeps
        // created_at/updated_at consistent (mirrors the Role persistence adapter).
        UserRoleJpaEntity entity = jpaRepository().findById(id)
                .orElseGet(() -> new UserRoleJpaEntity(id, userId, roleId));

        entity.updateFrom(userId, roleId);

        return mapToDomain(jpaRepository().save(entity));
    }

    @Override
    public List<UserRole> findAll() {
        return super.findAll();
    }

    @Override
    public Optional<UserRole> findById(UUID id) {
        return super.findById(id);
    }

    // =====================
    // Queries
    // =====================

    @Override
    public List<UserRole> findByUserId(UUID userId) {
        return jpaRepository().findByUserId(userId).stream().map(this::mapToDomain).toList();
    }

    @Override
    public List<UserRole> findByRoleId(UUID roleId) {
        return jpaRepository().findByRoleId(roleId).stream().map(this::mapToDomain).toList();
    }

    @Override
    public Optional<UserRole> findByUserIdAndRoleId(UUID userId, UUID roleId) {
        return jpaRepository().findByUserIdAndRoleId(userId, roleId).map(this::mapToDomain);
    }

    @Override
    public boolean existsByUserIdAndRoleId(UUID userId, UUID roleId) {
        return jpaRepository().existsByUserIdAndRoleId(userId, roleId);
    }

    @Override
    public long countByUserId(UUID userId) {
        return jpaRepository().countByUserId(userId);
    }

    // =====================
    // Deletion
    // =====================

    @Override
    @Transactional
    public void deleteById(UUID id) {
        super.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByUserIdAndRoleId(UUID userId, UUID roleId) {
        jpaRepository().deleteByUserIdAndRoleId(userId, roleId);
    }

    @Override
    @Transactional
    public void deleteByUserId(UUID userId) {
        jpaRepository().deleteByUserId(userId);
    }
}
