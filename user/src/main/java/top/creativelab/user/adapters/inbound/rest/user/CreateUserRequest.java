package top.creativelab.user.adapters.inbound.rest.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "CreateUserRequest", description = "Payload used to register a new user.")
public record CreateUserRequest(
        @Schema(description = "Unique username of the new user", example = "jane.doe", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String username,
        @Schema(description = "Plain-text password of the new user", example = "S3cureP@ssw0rd", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String password) {
}
