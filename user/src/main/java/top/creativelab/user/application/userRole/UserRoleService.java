package top.creativelab.user.application.userRole;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.creativelab.user.application.exception.UserNotFoundException;
import top.creativelab.user.application.exception.UserRoleNotFoundException;
import top.creativelab.user.domain.user.vo.UserId;
import top.creativelab.user.domain.userRole.UserRole;
import top.creativelab.user.domain.userRole.vo.RoleId;
import top.creativelab.user.ports.inbound.userRole.UserRoleUseCase;
import top.creativelab.user.ports.outbound.user.UserRepository;
import top.creativelab.user.ports.outbound.userRole.UserRoleRepository;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Orchestrates the assignment of roles to users.
 *
 * <p>Role definitions live in the {@code access-control} service, so this service only validates
 * that the user exists and stores the role identifier; it never checks that the role itself
 * exists.</p>
 */
@Service
@Transactional
@RequiredArgsConstructor
public class UserRoleService implements UserRoleUseCase {

    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;

    @Override
    public UserRole assignRole(UUID userId, UUID roleId) {
        UserId user = requireUserId(userId);
        RoleId role = requireRoleId(roleId);
        ensureUserExists(user);

        if (userRoleRepository.existsByUserIdAndRoleId(userId, roleId)) {
            throw new IllegalStateException(
                    "Role %s is already assigned to user %s".formatted(roleId, userId));
        }

        return userRoleRepository.save(UserRole.assign(user, role));
    }

    @Override
    public List<UserRole> replaceRoles(UUID userId, Set<UUID> roleIds) {
        UserId user = requireUserId(userId);
        ensureUserExists(user);

        Set<UUID> desiredRoleIds = new LinkedHashSet<>();
        if (roleIds != null) {
            roleIds.stream().filter(Objects::nonNull).forEach(desiredRoleIds::add);
        }

        userRoleRepository.findByUserId(userId).stream()
                .filter(userRole -> !desiredRoleIds.contains(userRole.getRoleId().getValue()))
                .forEach(userRole -> userRoleRepository.deleteById(userRole.getId().getValue()));

        List<UserRole> remaining = userRoleRepository.findByUserId(userId);
        Set<UUID> remainingRoleIds = remaining.stream()
                .map(userRole -> userRole.getRoleId().getValue())
                .collect(Collectors.toCollection(LinkedHashSet::new));

        List<UserRole> created = desiredRoleIds.stream()
                .filter(roleId -> !remainingRoleIds.contains(roleId))
                .map(roleId -> userRoleRepository.save(UserRole.assign(user, new RoleId(roleId))))
                .toList();

        return Stream.concat(remaining.stream(), created.stream())
                .sorted(Comparator.comparing(userRole -> userRole.getRoleId().getValue()))
                .toList();
    }

    @Override
    public void unassignRole(UUID userId, UUID roleId) {
        requireUserId(userId);
        requireRoleId(roleId);

        UserRole userRole = userRoleRepository.findByUserIdAndRoleId(userId, roleId)
                .orElseThrow(() -> new UserRoleNotFoundException(userId, roleId));

        userRoleRepository.deleteById(userRole.getId().getValue());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserRole> findByUserId(UUID userId) {
        return userRoleRepository.findByUserId(requireUserId(userId).getValue());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserRole> findByRoleId(UUID roleId) {
        return userRoleRepository.findByRoleId(requireRoleId(roleId).getValue());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasRole(UUID userId, UUID roleId) {
        return userRoleRepository.existsByUserIdAndRoleId(
                requireUserId(userId).getValue(),
                requireRoleId(roleId).getValue());
    }

    @Override
    @Transactional(readOnly = true)
    public long countRoles(UUID userId) {
        return userRoleRepository.countByUserId(requireUserId(userId).getValue());
    }

    private void ensureUserExists(UserId userId) {
        if (!userRepository.existsById(userId.getValue())) {
            throw new UserNotFoundException(userId.getValue().toString());
        }
    }

    private static UserId requireUserId(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("UserId can't be null");
        }
        return new UserId(userId);
    }

    private static RoleId requireRoleId(UUID roleId) {
        if (roleId == null) {
            throw new IllegalArgumentException("RoleId can't be null");
        }
        return new RoleId(roleId);
    }
}
