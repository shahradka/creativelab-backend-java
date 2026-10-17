package top.creativelab.role.adapters.inbound.rest.permission;

import jakarta.validation.constraints.NotBlank;

public record CreatePermissionRequest(@NotBlank String name) {

}
