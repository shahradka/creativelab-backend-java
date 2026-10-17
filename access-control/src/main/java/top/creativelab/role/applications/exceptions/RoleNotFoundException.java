package top.creativelab.role.applications.exceptions;

import java.util.UUID;

public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(UUID roleId) {
        super("Role Id Not found: ".formatted(roleId));
    }
}
