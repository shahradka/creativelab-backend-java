package top.creativelab.user.adapters.outbound.persistence.userRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import persistance.JpaEntityBaseModel;

import java.util.UUID;

/**
 * JPA representation of the {@code user_roles} join table.
 *
 * <p>{@code role_id} intentionally has no foreign key: roles are owned by the
 * {@code access-control} service and live in a different database.</p>
 */
@Entity
@Table(
        name = "user_roles",
        uniqueConstraints = @UniqueConstraint(
                name = "uniq_user_roles_user_id_and_role_id",
                columnNames = {"user_id", "role_id"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserRoleJpaEntity extends JpaEntityBaseModel {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "role_id", nullable = false)
    private UUID roleId;

    public UserRoleJpaEntity(UUID id, UUID userId, UUID roleId) {
        super(id);
        updateFrom(userId, roleId);
    }

    /**
     * Copies the mutable state from the domain aggregate. The identifier and the auditing
     * timestamps are intentionally left untouched.
     */
    public void updateFrom(UUID userId, UUID roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }
}
