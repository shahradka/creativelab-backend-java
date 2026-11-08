package top.creativelab.user.application.exception;

import java.util.UUID;

public class UserRoleNotFoundException extends RuntimeException {

    public UserRoleNotFoundException(UUID userId, UUID roleId) {
        super("Role %s is not assigned to user %s".formatted(roleId, userId));
    }

    public UserRoleNotFoundException(String message) {
        super(message);
    }
}
