package top.creativelab.role.ports.inbound;

import top.creativelab.role.domain.Permission;
import top.creativelab.role.domain.vo.ActionId;
import top.creativelab.role.domain.vo.ResourceId;

import java.util.List;
import java.util.UUID;

public interface PermissionUseCase {
    Permission create(String name, UUID resourceId, UUID actionId);
    List<Permission> findByName(String name);
    Permission findById(UUID id);
    List<Permission> findAll();
    Permission update(UUID id, Permission permission);
    void delete(UUID id);
}
