package top.creativelab.user.domain.user.vo;

import lombok.Value;

import java.util.UUID;

@Value
public class UserId {
    UUID value;

    public static UserId newId() {
        return new UserId(UUID.randomUUID());
    }
}
