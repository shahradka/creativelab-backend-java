package top.creativelab.user.domain.user.vo;

import lombok.Value;

import java.util.UUID;

@Value
public class UserId {
    String value;

    public static UserId newId() {
        return new UserId(UUID.randomUUID().toString());
    }
}
