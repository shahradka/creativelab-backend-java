package top.creativelab.role.applications;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.creativelab.role.applications.exceptions.PermissionNotFoundException;
import top.creativelab.role.domain.Permission;
import top.creativelab.role.domain.vo.ActionId;
import top.creativelab.role.domain.vo.PermissionId;
import top.creativelab.role.domain.vo.ResourceId;
import top.creativelab.role.ports.inbound.PermissionUseCase;
import top.creativelab.role.ports.outbound.ActionRepository;
import top.creativelab.role.ports.outbound.PermissionRepository;
import top.creativelab.role.ports.outbound.ResourceRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PermissionService implements PermissionUseCase {

    private final PermissionRepository repository;
    private final ActionRepository actionRepository;
    private final ResourceRepository resourceRepository;

    @Override
    public Permission create(String name, UUID resourceUUIDId, UUID actionUUIDId) {
        if(name.isBlank())
            throw new IllegalArgumentException("Permission name can't be empty");
        ActionId actionId = new ActionId(actionUUIDId);
        if(!actionRepository.isExistById(actionId))
            throw new IllegalArgumentException("ActionId isn't valid");
        ResourceId resourceId = new ResourceId(resourceUUIDId);
        if(!resourceRepository.isExistsById(resourceId))
            throw new IllegalArgumentException("ResourceId isn't valid");


        return repository.save(new Permission(PermissionId.newId(), name, resourceId, actionId));
    }

    @Override
    public List<Permission> findByName(String name) {
        return repository.findByName(name);
    }

    @Override
    public Permission findById(UUID id) {

        PermissionId permissionId = new PermissionId(id);
        return repository.findById(permissionId).orElseThrow(() -> new PermissionNotFoundException(permissionId.getValue()));
    }

    @Override
    public List<Permission> findAll() {
        return repository.findAll();
    }


    @Override
    public Permission update(UUID id, Permission permission) {

        PermissionId permissionId = new PermissionId(id);

        if(!repository.isExistById(permissionId))
            throw new PermissionNotFoundException(permissionId.getValue());

        Permission modifiedPermission = repository.findById(permissionId).orElseThrow(
                ()-> new PermissionNotFoundException(permissionId.getValue())
        );

        modifiedPermission.update(permission.getName(), permission.getResourceId(), permission.getActionId());

        repository.save(modifiedPermission);

        return modifiedPermission;
    }

    @Override
    public void delete(UUID id) {
        PermissionId permissionId = new PermissionId(id);
        if(!repository.isExistById(permissionId))
            throw new PermissionNotFoundException(permissionId.getValue());
        repository.delete(permissionId);
    }
}
