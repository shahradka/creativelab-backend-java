package top.creativelab.role.domain.vo;

import lombok.Value;

import java.util.UUID;

@Value
public class PermissionId {
    UUID value;
    public static PermissionId newId (){
        return new PermissionId(UUID.randomUUID());
    }
}
