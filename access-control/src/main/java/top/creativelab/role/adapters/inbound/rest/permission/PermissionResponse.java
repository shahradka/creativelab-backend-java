package top.creativelab.role.adapters.inbound.rest.permission;

import top.creativelab.role.domain.Action;
import top.creativelab.role.domain.Permission;

import java.util.UUID;

public record PermissionResponse(UUID id, String name, UUID resourceId, UUID actionId) {

    public static PermissionResponse from(Permission permission){
        return new PermissionResponse(permission.getPermissionId().getValue(), permission.getName(), permission.getResourceId().getValue(), permission.getActionId().getValue());
    }
}