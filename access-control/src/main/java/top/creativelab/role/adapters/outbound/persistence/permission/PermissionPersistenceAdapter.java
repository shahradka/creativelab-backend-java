package top.creativelab.role.adapters.outbound.persistence.permission;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import persistance.BasePersistenceAdapter;
import top.creativelab.role.adapters.outbound.persistence.resource.ResourceJpaEntity;
import top.creativelab.role.domain.Permission;
import top.creativelab.role.domain.Resource;
import top.creativelab.role.domain.vo.ActionId;
import top.creativelab.role.domain.vo.PermissionId;
import top.creativelab.role.domain.vo.ResourceId;
import top.creativelab.role.ports.outbound.PermissionRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class PermissionPersistenceAdapter extends BasePersistenceAdapter<Permission, PermissionJpaEntity, UUID> implements PermissionRepository {

    public PermissionPersistenceAdapter(PermissionJpaRepository repository) {
        super(repository);
    }

    @Override
    public List<Permission> findByName(String name) {
        return  ((PermissionJpaRepository)repository).findByNameContainingIgnoreCase(name).stream().map(this::mapToDomain).toList();
    }

    @Override
    public Optional<Permission> findById(PermissionId permissionId) {
        return super.findById(permissionId.getValue());
    }


    @Override
    public boolean isExistById(PermissionId permissionId) {
        return repository.existsById(permissionId.getValue());
    }

    @Override
    public void delete(PermissionId id) {
        repository.deleteById(id.getValue());
    }

    // =====================
    // Mapping
    // =====================

    protected PermissionJpaEntity mapToJpaEntity(Permission permission) {
        return new PermissionJpaEntity(
            permission.getPermissionId().getValue(),
            permission.getName(),
            permission.getActionId().getValue(),
            permission.getResourceId().getValue()
        );
    }

    protected Permission mapToDomain(PermissionJpaEntity entity) {
        return new Permission(
                new PermissionId(entity.getId()),
                entity.getName(),
                new ResourceId(entity.getResourceId()),
                new ActionId(entity.getActionId())
        );
    }
}
