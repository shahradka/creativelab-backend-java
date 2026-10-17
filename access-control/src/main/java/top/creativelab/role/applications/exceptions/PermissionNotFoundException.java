package top.creativelab.role.applications.exceptions;

import java.util.UUID;

public class PermissionNotFoundException extends RuntimeException {
    public PermissionNotFoundException(UUID id) {
        super("Permission not found!: %s".formatted(id));
    }
}
