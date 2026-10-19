package top.creativelab.role.adapters.outbound.persistence.rolePermission;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import persistance.JpaEntityBaseModel;
import top.creativelab.role.adapters.outbound.persistence.permission.PermissionJpaEntity;
import top.creativelab.role.adapters.outbound.persistence.role.RoleJpaEntity;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "roles_permissions")
public class RolePermissionJpaEntity extends JpaEntityBaseModel {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "roles_id")
    private RoleJpaEntity roleJpaEntity;

    @Column(name = "permissions_id", nullable = false)
    private UUID permissionId;

    public RolePermissionJpaEntity(UUID id, UUID permissionId, RoleJpaEntity roleJpaEntity){
        this.permissionId = permissionId;
        this.roleJpaEntity = roleJpaEntity;
        super(id);
    }

}
