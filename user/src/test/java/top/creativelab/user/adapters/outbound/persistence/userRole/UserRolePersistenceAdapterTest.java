package top.creativelab.user.adapters.outbound.persistence.userRole;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import top.creativelab.user.domain.user.vo.UserId;
import top.creativelab.user.domain.userRole.UserRole;
import top.creativelab.user.domain.userRole.vo.RoleId;
import top.creativelab.user.domain.userRole.vo.UserRoleId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRolePersistenceAdapterTest {

    @Mock
    private UserRoleJpaRepository jpaRepository;

    private UserRolePersistenceAdapter adapter;

    private UUID userId;
    private UUID roleId;

    @BeforeEach
    void setUp() {
        adapter = new UserRolePersistenceAdapter(jpaRepository);
        userId = UUID.randomUUID();
        roleId = UUID.randomUUID();
    }

    private UserRole domainAssignment() {
        return UserRole.rehydrate(new UserRoleId(UUID.randomUUID()), new UserId(userId), new RoleId(roleId));
    }

    private UserRoleJpaEntity entityFor(UserRole userRole) {
        return new UserRoleJpaEntity(
                userRole.getId().getValue(),
                userRole.getUserId().getValue(),
                userRole.getRoleId().getValue()
        );
    }

    @Test
    void findByIdMapsAllFields() {
        UserRole userRole = domainAssignment();
        when(jpaRepository.findById(userRole.getId().getValue()))
                .thenReturn(Optional.of(entityFor(userRole)));

        Optional<UserRole> found = adapter.findById(userRole.getId().getValue());

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(userRole.getId());
        assertThat(found.get().getUserId().getValue()).isEqualTo(userId);
        assertThat(found.get().getRoleId().getValue()).isEqualTo(roleId);
    }

    @Test
    void saveCreatesNewEntityWhenAbsent() {
        UserRole userRole = domainAssignment();
        when(jpaRepository.findById(userRole.getId().getValue())).thenReturn(Optional.empty());
        when(jpaRepository.save(any(UserRoleJpaEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserRole saved = adapter.save(userRole);

        assertThat(saved.getId()).isEqualTo(userRole.getId());
        assertThat(saved.getUserId().getValue()).isEqualTo(userId);
        assertThat(saved.getRoleId().getValue()).isEqualTo(roleId);
        verify(jpaRepository).save(any(UserRoleJpaEntity.class));
    }

    @Test
    void saveMutatesExistingEntity() {
        UserRole userRole = domainAssignment();
        UserRoleJpaEntity existing = entityFor(userRole);
        when(jpaRepository.findById(userRole.getId().getValue())).thenReturn(Optional.of(existing));
        when(jpaRepository.save(existing)).thenReturn(existing);

        adapter.save(userRole);

        assertThat(existing.getUserId()).isEqualTo(userId);
        assertThat(existing.getRoleId()).isEqualTo(roleId);
        verify(jpaRepository).save(existing);
    }

    @Test
    void findByUserIdMapsEntities() {
        UserRole userRole = domainAssignment();
        when(jpaRepository.findByUserId(userId)).thenReturn(List.of(entityFor(userRole)));

        assertThat(adapter.findByUserId(userId)).hasSize(1);
    }

    @Test
    void findByRoleIdMapsEntities() {
        UserRole userRole = domainAssignment();
        when(jpaRepository.findByRoleId(roleId)).thenReturn(List.of(entityFor(userRole)));

        assertThat(adapter.findByRoleId(roleId)).hasSize(1);
    }

    @Test
    void findByUserIdAndRoleIdMaps() {
        UserRole userRole = domainAssignment();
        when(jpaRepository.findByUserIdAndRoleId(userId, roleId))
                .thenReturn(Optional.of(entityFor(userRole)));

        assertThat(adapter.findByUserIdAndRoleId(userId, roleId)).isPresent();
    }

    @Test
    void existsByUserIdAndRoleIdDelegates() {
        when(jpaRepository.existsByUserIdAndRoleId(userId, roleId)).thenReturn(true);

        assertThat(adapter.existsByUserIdAndRoleId(userId, roleId)).isTrue();
    }

    @Test
    void countByUserIdDelegates() {
        when(jpaRepository.countByUserId(userId)).thenReturn(4L);

        assertThat(adapter.countByUserId(userId)).isEqualTo(4L);
    }

    @Test
    void deleteByUserIdAndRoleIdDelegates() {
        adapter.deleteByUserIdAndRoleId(userId, roleId);

        verify(jpaRepository).deleteByUserIdAndRoleId(userId, roleId);
    }

    @Test
    void deleteByUserIdDelegates() {
        adapter.deleteByUserId(userId);

        verify(jpaRepository).deleteByUserId(userId);
    }
}
