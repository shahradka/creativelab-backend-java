package top.creativelab.user.ports.inbound.userRole;

import top.creativelab.user.domain.userRole.UserRole;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Use cases for assigning roles (owned by the {@code access-control} service) to users.
 */
public interface UserRoleUseCase {

    /**
     * Assigns a single role to a user.
     *
     * @param userId the user receiving the role
     * @param roleId the role to assign
     * @return the created assignment
     * @throws IllegalArgumentException  if either identifier is {@code null}
     * @throws IllegalStateException     if the role is already assigned to the user
     * @throws top.creativelab.user.application.exception.UserNotFoundException if the user does not exist
     */
    UserRole assignRole(UUID userId, UUID roleId);

    /**
     * Replaces the full set of roles of a user with the supplied one: missing roles are added and
     * roles that are not part of the set are removed.
     *
     * @param userId  the user whose roles are synchronised
     * @param roleIds the desired roles; {@code null} clears every assignment
     * @return the resulting assignments
     */
    List<UserRole> replaceRoles(UUID userId, Set<UUID> roleIds);

    /**
     * Removes a single role from a user.
     *
     * @param userId the user losing the role
     * @param roleId the role to remove
     * @throws top.creativelab.user.application.exception.UserRoleNotFoundException if the assignment does not exist
     */
    void unassignRole(UUID userId, UUID roleId);

    /**
     * @param userId the user to inspect
     * @return every role assigned to the user
     */
    List<UserRole> findByUserId(UUID userId);

    /**
     * @param roleId the role to inspect
     * @return every user holding the role
     */
    List<UserRole> findByRoleId(UUID roleId);

    /**
     * @param userId the user to inspect
     * @param roleId the role to look for
     * @return {@code true} when the user holds the role
     */
    boolean hasRole(UUID userId, UUID roleId);

    /**
     * @param userId the user to inspect
     * @return number of roles assigned to the user
     */
    long countRoles(UUID userId);
}
