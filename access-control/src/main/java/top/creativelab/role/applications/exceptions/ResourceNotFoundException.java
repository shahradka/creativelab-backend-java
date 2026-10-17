package top.creativelab.role.applications.exceptions;

import java.util.UUID;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(UUID id) {
        super("Resource not found: %s".formatted(id));
    }
    public ResourceNotFoundException(String name) {
        super("Resource not found: %s".formatted(name));
    }

}
