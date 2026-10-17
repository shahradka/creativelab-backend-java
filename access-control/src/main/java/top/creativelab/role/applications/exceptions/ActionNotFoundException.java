package top.creativelab.role.applications.exceptions;

import java.util.UUID;

public class ActionNotFoundException extends RuntimeException {
    public ActionNotFoundException(UUID id) {
        super("Action is not found: %s".formatted(id));
    }

    public ActionNotFoundException(String name) {
        super("Action is not found: %s".formatted(name));
    }
}
