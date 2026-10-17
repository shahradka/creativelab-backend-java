package top.creativelab.role.domain.vo;

import lombok.Value;

import java.util.UUID;

@Value
public class ActionId {
    UUID value;
    public static ActionId newId() {
        return new ActionId(UUID.randomUUID());
    }
}
