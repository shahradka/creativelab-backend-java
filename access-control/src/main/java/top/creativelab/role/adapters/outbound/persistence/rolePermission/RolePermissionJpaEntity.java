package top.creativelab.role.adapters.outbound.persistence.rolePermission;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import persistance.JpaEntityBaseModel;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "roles_permissions")
public class RolePermissionJpaEntity extends JpaEntityBaseModel {

    @Column(name = "permissions_id", nullable = false)
    private UUID permissionId;

    @Column(name = "roles_id", nullable = false)
    private UUID rolesId;

    RolePermissionJpaEntity(UUID id, UUID permissionId, UUID rolesId){
        super(id);
        this.permissionId = permissionId;
        this.rolesId = rolesId;
    }
}
