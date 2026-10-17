package top.creativelab.role.adapters.inbound.rest.role;

import top.creativelab.role.domain.Role;
import top.creativelab.role.domain.vo.PermissionId;

import java.util.UUID;
import java.util.stream.Collectors;

public record RoleResponse(String id, String name, String permissionIds) {
    public static RoleResponse from(Role role){
        if(!role.getPermissionSet().isEmpty())
            return new RoleResponse(role.getId().getValue().toString(), role.getName(), role.getPermissionSet().stream().map(PermissionId::getValue).map(UUID::toString).collect(Collectors.joining(", ")));
        return new RoleResponse(role.getId().getValue().toString(), role.getName(), "");
    }
}
