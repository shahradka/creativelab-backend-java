package top.creativelab.role.domain;

import lombok.Getter;
import lombok.Value;
import top.creativelab.role.domain.vo.PermissionId;
import top.creativelab.role.domain.vo.RoleId;

import java.util.HashSet;
import java.util.Set;

@Getter
public class Role {
    private final RoleId id;
    private String name;
    private final Set<PermissionId> permissionSet;

    public Role(RoleId id, String name, Set<PermissionId> permissionSet) {
        if (id == null) throw new IllegalArgumentException("RoleId can't be null");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name can't be null or empty");

        this.id = id;
        this.name = name;
        this.permissionSet = permissionSet != null ? permissionSet : new HashSet<>();
    }

    public void updatePermissions(Set<PermissionId> permissionIdSet){
        if(permissionIdSet == null)
            throw new IllegalArgumentException("Permisson can't be null");
        this.permissionSet.clear();
        this.permissionSet.addAll(permissionIdSet);
    }


    public void updateName(String name){
        this.name = name;
    }

    public boolean hasAccess(PermissionId permissionId){

        if(permissionId == null){
            throw new IllegalArgumentException("PermissionId can not be null");
        }

        return this.permissionSet.contains(permissionId);
    }
}
