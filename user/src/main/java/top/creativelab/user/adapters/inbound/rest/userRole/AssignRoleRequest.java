package top.creativelab.user.adapters.inbound.rest.userRole;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Body of {@code POST /user-roles/user/{userId}}.
 */
@Schema(name = "AssignRoleRequest", description = "Payload used to assign a single role to a user.")
public record AssignRoleRequest(
        @Schema(description = "Identifier of the role to assign",
                example = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull UUID roleId) {
}
