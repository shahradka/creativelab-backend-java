package top.creativelab.role.domain.vo;

import lombok.Value;

import java.util.UUID;

@Value
public class ResourceId {
    UUID value;
    public static ResourceId newId() {
        return new ResourceId(UUID.randomUUID());
    }
}
