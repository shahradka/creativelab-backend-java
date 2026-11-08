package top.creativelab.user.application.userRole;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import top.creativelab.user.application.exception.UserNotFoundException;
import top.creativelab.user.application.exception.UserRoleNotFoundException;
import top.creativelab.user.domain.user.vo.UserId;
import top.creativelab.user.domain.userRole.UserRole;
import top.creativelab.user.domain.userRole.vo.RoleId;
import top.creativelab.user.domain.userRole.vo.UserRoleId;
import top.creativelab.user.ports.outbound.user.UserRepository;
import top.creativelab.user.ports.outbound.userRole.UserRoleRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRoleServiceTest {

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private UserRepository userRepository;

    private UserRoleService service;

    private UUID userId;
    private UUID roleId;

    @BeforeEach
    void setUp() {
        service = new UserRoleService(userRoleRepository, userRepository);
        userId = UUID.randomUUID();
        roleId = UUID.randomUUID();
    }

    private UserRole assignment(UUID role) {
        return UserRole.rehydrate(new UserRoleId(UUID.randomUUID()), new UserId(userId), new RoleId(role));
    }

    // =====================
    // assignRole
    // =====================

    @Test
    void assignRolePersistsAssignment() {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRoleRepository.existsByUserIdAndRoleId(userId, roleId)).thenReturn(false);
        when(userRoleRepository.save(any(UserRole.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserRole result = service.assignRole(userId, roleId);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getUserId().getValue()).isEqualTo(userId);
        assertThat(result.getRoleId().getValue()).isEqualTo(roleId);
        verify(userRoleRepository).save(result);
    }

    @Test
    void assignRoleRejectsDuplicateAssignment() {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRoleRepository.existsByUserIdAndRoleId(userId, roleId)).thenReturn(true);

        assertThatThrownBy(() -> service.assignRole(userId, roleId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already assigned");
        verify(userRoleRepository, never()).save(any());
    }

    @Test
    void assignRoleRejectsUnknownUser() {
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThatThrownBy(() -> service.assignRole(userId, roleId))
                .isInstanceOf(UserNotFoundException.class);
        verify(userRoleRepository, never()).save(any());
    }

    @Test
    void assignRoleRejectsNullRoleId() {
        assertThatThrownBy(() -> service.assignRole(userId, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("RoleId");
        verify(userRepository, never()).existsById(any());
    }

    // =====================
    // replaceRoles
    // =====================

    @Test
    void replaceRolesAddsMissingAndRemovesUnexpected() {
        UUID keptRole = UUID.randomUUID();
        UUID droppedRole = UUID.randomUUID();
        UUID addedRole = UUID.randomUUID();

        UserRole dropped = assignment(droppedRole);
        UserRole kept = assignment(keptRole);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRoleRepository.findByUserId(userId)).thenReturn(List.of(dropped, kept), List.of(kept));
        when(userRoleRepository.save(any(UserRole.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<UserRole> result = service.replaceRoles(userId, Set.of(keptRole, addedRole));

        verify(userRoleRepository).deleteById(dropped.getId().getValue());
        assertThat(result)
                .extracting(userRole -> userRole.getRoleId().getValue())
                .containsExactlyInAnyOrder(keptRole, addedRole);
    }

    @Test
    void replaceRolesWithNullClearsAssignments() {
        UserRole existing = assignment(roleId);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRoleRepository.findByUserId(userId)).thenReturn(List.of(existing), List.of());

        List<UserRole> result = service.replaceRoles(userId, null);

        verify(userRoleRepository).deleteById(existing.getId().getValue());
        assertThat(result).isEmpty();
        verify(userRoleRepository, never()).save(any());
    }

    // =====================
    // unassignRole
    // =====================

    @Test
    void unassignRoleDeletesAssignment() {
        UserRole existing = assignment(roleId);
        when(userRoleRepository.findByUserIdAndRoleId(userId, roleId)).thenReturn(Optional.of(existing));

        service.unassignRole(userId, roleId);

        verify(userRoleRepository).deleteById(existing.getId().getValue());
    }

    @Test
    void unassignRoleRejectsMissingAssignment() {
        when(userRoleRepository.findByUserIdAndRoleId(userId, roleId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.unassignRole(userId, roleId))
                .isInstanceOf(UserRoleNotFoundException.class);
        verify(userRoleRepository, never()).deleteById(any());
    }

    // =====================
    // queries
    // =====================

    @Test
    void findByUserIdDelegates() {
        UserRole existing = assignment(roleId);
        when(userRoleRepository.findByUserId(userId)).thenReturn(List.of(existing));

        assertThat(service.findByUserId(userId)).containsExactly(existing);
    }

    @Test
    void findByRoleIdDelegates() {
        UserRole existing = assignment(roleId);
        when(userRoleRepository.findByRoleId(roleId)).thenReturn(List.of(existing));

        assertThat(service.findByRoleId(roleId)).containsExactly(existing);
    }

    @Test
    void hasRoleDelegates() {
        when(userRoleRepository.existsByUserIdAndRoleId(userId, roleId)).thenReturn(true);

        assertThat(service.hasRole(userId, roleId)).isTrue();
    }

    @Test
    void countRolesDelegates() {
        when(userRoleRepository.countByUserId(userId)).thenReturn(3L);

        assertThat(service.countRoles(userId)).isEqualTo(3L);
    }
}
