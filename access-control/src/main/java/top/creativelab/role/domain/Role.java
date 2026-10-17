package top.creativelab.role.domain;

import lombok.Value;
import top.creativelab.role.domain.vo.PermissionId;
import top.creativelab.role.domain.vo.RoleId;

import java.util.Set;

@Value
public class Role {
    private final RoleId id;
    private final String name;
    private final Set<PermissionId> permissionSet;

    public void addPermission(PermissionId permissionId){
        if(permissionId == null)
            throw new IllegalArgumentException("Permisson can't be null");
        this.permissionSet.add(permissionId);
    }

    public boolean hasAccess(PermissionId permissionId){

        if(permissionId == null){
            throw new IllegalArgumentException("PermissionId can not be null");
        }

        return this.permissionSet.contains(permissionId);
    }
}
