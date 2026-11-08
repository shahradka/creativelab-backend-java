package top.creativelab.user.adapters.inbound.rest.userRole;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.Set;
import java.util.UUID;

/**
 * Body of {@code PUT /user-roles/user/{userId}}.
 *
 * <p>The supplied set becomes the complete set of roles of the user.</p>
 */
@Schema(name = "ReplaceUserRolesRequest", description = "Payload used to replace the complete set of roles of a user.")
public record ReplaceUserRolesRequest(
        @Schema(description = "Complete set of role identifiers the user should have",
                example = "[\"3fa85f64-5717-4562-b3fc-2c963f66afa6\"]",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull Set<UUID> roleIds) {
}
