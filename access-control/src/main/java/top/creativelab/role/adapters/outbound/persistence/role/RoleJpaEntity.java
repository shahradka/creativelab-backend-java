package top.creativelab.role.adapters.outbound.persistence.role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import persistance.JpaEntityBaseModel;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "roles")
public class RoleJpaEntity extends JpaEntityBaseModel {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "permission_ids")
    private Set<UUID> permissionIds = new HashSet<>();

    public RoleJpaEntity(UUID id, String name, Set<UUID> permissionIds){
       super(id);
       this.name = name;
       this.permissionIds.addAll(permissionIds);
    }
}
