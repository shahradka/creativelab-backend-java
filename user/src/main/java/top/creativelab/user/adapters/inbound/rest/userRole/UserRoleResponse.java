package top.creativelab.user.adapters.inbound.rest.userRole;

import io.swagger.v3.oas.annotations.media.Schema;
import top.creativelab.user.domain.userRole.UserRole;

import java.util.UUID;

@Schema(name = "UserRoleResponse", description = "A user-role assignment.")
public record UserRoleResponse(
        @Schema(description = "Identifier of the assignment", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,
        @Schema(description = "Identifier of the user", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID userId,
        @Schema(description = "Identifier of the role", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID roleId) {

    public static UserRoleResponse fromDomain(UserRole userRole) {
        return new UserRoleResponse(
                userRole.getId().getValue(),
                userRole.getUserId().getValue(),
                userRole.getRoleId().getValue()
        );
    }
}
