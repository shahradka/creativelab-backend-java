package top.creativelab.role.ports.outbound;

import top.creativelab.role.domain.Permission;
import top.creativelab.role.domain.vo.PermissionId;

import java.util.List;
import java.util.Optional;

public interface PermissionRepository {
    Permission save(Permission permission);
    List<Permission> findAll();
    List<Permission> findByName(String name);
    Optional<Permission> findById(PermissionId permissionId);
    boolean isExistById(PermissionId permissionId);
    void delete(PermissionId id);
}
