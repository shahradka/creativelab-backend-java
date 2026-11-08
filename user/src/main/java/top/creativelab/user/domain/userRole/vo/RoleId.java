package top.creativelab.user.domain.userRole.vo;

import lombok.Value;

import java.util.UUID;

/**
 * Reference to a role owned by the {@code access-control} service.
 *
 * <p>Roles live in a different database, therefore only the identifier is stored on this side and
 * no foreign key is enforced.</p>
 */
@Value
public class RoleId {

    UUID value;

    public static RoleId newId() {
        return new RoleId(UUID.randomUUID());
    }
}
