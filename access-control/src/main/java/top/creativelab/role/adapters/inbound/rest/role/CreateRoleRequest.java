package top.creativelab.role.adapters.inbound.rest.role;

import jakarta.validation.constraints.NotNull;

import java.util.Set;
import java.util.UUID;

public record CreateRoleRequest(@NotNull String name, Set<UUID> permissionIdSet) {

}
