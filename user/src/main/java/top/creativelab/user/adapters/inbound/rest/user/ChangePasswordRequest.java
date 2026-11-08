package top.creativelab.user.adapters.inbound.rest.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "ChangePasswordRequest", description = "Payload used to change a user's password.")
record ChangePasswordRequest(
        @Schema(description = "New password", example = "N3wS3cureP@ss", requiredMode = Schema.RequiredMode.REQUIRED)
        @Valid @NotBlank String newPassword,
        @Schema(description = "Current password", example = "0ldS3cureP@ss")
        @Valid String oldPassword) {
}
