package top.creativelab.role.domain.vo;

import lombok.Value;

import java.util.UUID;

@Value
public class RoleId {
    UUID value;
    public static RoleId newId() { return new RoleId(UUID.randomUUID());}
}
