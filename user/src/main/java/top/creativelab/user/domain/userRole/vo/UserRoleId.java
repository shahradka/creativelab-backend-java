package top.creativelab.user.domain.userRole.vo;

import lombok.Value;

import java.util.UUID;

/**
 * Identifier of a user/role assignment row.
 */
@Value
public class UserRoleId {

    UUID value;

    public static UserRoleId newId() {
        return new UserRoleId(UUID.randomUUID());
    }
}
