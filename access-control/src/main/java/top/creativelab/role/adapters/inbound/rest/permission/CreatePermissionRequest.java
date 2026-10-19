package top.creativelab.role.adapters.inbound.rest.permission;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreatePermissionRequest(@NotBlank String name,
                                      @NotNull UUID resourceId, @NotNull UUID actionId) {

}
