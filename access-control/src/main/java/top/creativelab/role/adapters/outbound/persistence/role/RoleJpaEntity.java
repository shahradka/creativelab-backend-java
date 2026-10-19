package top.creativelab.role.adapters.outbound.persistence.role;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import persistance.JpaEntityBaseModel;
import top.creativelab.role.adapters.outbound.persistence.rolePermission.RolePermissionJpaEntity;
import top.creativelab.role.domain.vo.PermissionId;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "roles")
public class RoleJpaEntity extends JpaEntityBaseModel {

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "roles_id")
    private final Set<RolePermissionJpaEntity> rolesPermissionsJpaEntityList = new HashSet<>();

    public Set<UUID> getPermissionIds() {
        return rolesPermissionsJpaEntityList.isEmpty() ? Collections.emptySet() : rolesPermissionsJpaEntityList.stream().map(RolePermissionJpaEntity::getPermissionId).collect(Collectors.toSet());
    }

    public RoleJpaEntity(UUID id, String name, Set<UUID> permissionIds) {
        super(id);
        this.name = name;
    }
}
