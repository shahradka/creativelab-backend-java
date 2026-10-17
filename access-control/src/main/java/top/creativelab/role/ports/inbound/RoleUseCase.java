package top.creativelab.role.ports.inbound;

import top.creativelab.role.domain.Role;
import top.creativelab.role.domain.vo.PermissionId;
import top.creativelab.role.domain.vo.RoleId;

import java.util.List;
import java.util.Set;

public interface RoleUseCase {
    Role create(String name, Set<PermissionId> permissionIds);
    Role update(RoleId roleId, String name, Set<PermissionId> permissionIds);
    void Delete(RoleId roleId);
    List<Role> findByName(String name);
    Role findById(RoleId roleId);
    boolean isRoleExistById(RoleId id);
}
