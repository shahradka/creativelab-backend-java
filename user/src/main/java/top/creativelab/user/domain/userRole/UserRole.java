package top.creativelab.user.domain.userRole;

import lombok.Getter;
import top.creativelab.user.domain.user.vo.UserId;
import top.creativelab.user.domain.userRole.vo.RoleId;
import top.creativelab.user.domain.userRole.vo.UserRoleId;

/**
 * Assignment of a role (owned by the {@code access-control} service) to a user.
 *
 * <p>The aggregate is immutable: a role is either assigned or not, there is no intermediate state
 * to transition through. Reassigning the same role is rejected by the unique constraint on
 * {@code (user_id, role_id)}.</p>
 */
@Getter
public class UserRole {

    private final UserRoleId id;
    private final UserId userId;
    private final RoleId roleId;

    public UserRole(UserRoleId id, UserId userId, RoleId roleId) {
        if (id == null) {
            throw new IllegalArgumentException("UserRoleId can't be null");
        }
        if (userId == null) {
            throw new IllegalArgumentException("UserId can't be null");
        }
        if (roleId == null) {
            throw new IllegalArgumentException("RoleId can't be null");
        }
        this.id = id;
        this.userId = userId;
        this.roleId = roleId;
    }

    /**
     * Creates a brand new assignment with a generated identifier.
     */
    public static UserRole assign(UserId userId, RoleId roleId) {
        return new UserRole(UserRoleId.newId(), userId, roleId);
    }

    /**
     * Rehydrates an assignment from persisted state.
     */
    public static UserRole rehydrate(UserRoleId id, UserId userId, RoleId roleId) {
        return new UserRole(id, userId, roleId);
    }

    public boolean belongsTo(UserId userId) {
        return this.userId.equals(userId);
    }

    public boolean references(RoleId roleId) {
        return this.roleId.equals(roleId);
    }
}
