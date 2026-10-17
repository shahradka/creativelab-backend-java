package top.creativelab.role.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import top.creativelab.role.domain.vo.ActionId;
import top.creativelab.role.domain.vo.PermissionId;
import top.creativelab.role.domain.vo.ResourceId;

import java.util.ArrayList;
import java.util.UUID;

@Value
public class Permission {
    private final PermissionId permissionId;
    private String name;
    private ResourceId resourceId;
    private ActionId actionId;


    public static Permission create(String name, ResourceId resourceId, ActionId actionId){
        return new Permission(PermissionId.newId(), name, resourceId, actionId);
    }

    public Permission update(String newName, ResourceId resourceId, ActionId actionId){
        return new Permission(this.permissionId, newName, resourceId, actionId);
    }

}
