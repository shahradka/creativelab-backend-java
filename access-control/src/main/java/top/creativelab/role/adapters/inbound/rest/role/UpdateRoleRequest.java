package top.creativelab.role.adapters.inbound.rest.role;

import java.util.Set;
import java.util.UUID;

public record UpdateRoleRequest(String name, Set<UUID> permissionIdSet) {

}
