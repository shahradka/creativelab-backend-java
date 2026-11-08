package top.creativelab.user.adapters.inbound.rest.user;

import io.swagger.v3.oas.annotations.media.Schema;
import top.creativelab.user.domain.user.vo.BirthDate;
import top.creativelab.user.domain.user.vo.Email;
import top.creativelab.user.ports.inbound.user.UpdateUserCommand;

@Schema(name = "UpdateUserRequest", description = "Profile fields that can be updated for an existing user.")
public record UpdateUserRequest(
        @Schema(description = "New first name", example = "Jane") String firstname,
        @Schema(description = "New last name", example = "Doe") String lastname,
        @Schema(description = "New email address", example = "jane.doe@example.com") Email email,
        @Schema(description = "New birth date", example = "1990-01-31") BirthDate birthdate
) {

    public UpdateUserCommand toCommand() {
        return new UpdateUserCommand(firstname, lastname, email, birthdate);
    }
}
