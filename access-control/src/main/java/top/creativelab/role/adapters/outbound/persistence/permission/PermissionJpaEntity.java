package top.creativelab.role.adapters.outbound.persistence.permission;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import persistance.JpaEntityBaseModel;
import top.creativelab.role.domain.vo.ResourceId;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "permissions")
public class PermissionJpaEntity extends JpaEntityBaseModel {

    @Column(nullable = false)
    public String name;

    @Column(nullable = false, name = "action_id")
    public UUID actionId;

    @Column(nullable = false, name = "resource_id")
    public UUID resourceId;

    @Builder
    PermissionJpaEntity(UUID id, String name, UUID actionId, UUID resourceId){
        super(id);
        this.name = name;
        this.actionId = actionId;
        this.resourceId = resourceId;
    }

}
