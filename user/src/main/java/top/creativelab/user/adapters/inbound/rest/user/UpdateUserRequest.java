package top.creativelab.user.adapters.inbound.rest.user;

import top.creativelab.user.domain.user.vo.BirthDate;
import top.creativelab.user.domain.user.vo.Email;
import top.creativelab.user.ports.inbound.user.UpdateUserCommand;

public record UpdateUserRequest(
        String firstname,
        String lastname,
        Email email,
        BirthDate birthdate
) {

    public UpdateUserCommand toCommand() {
        return new UpdateUserCommand(firstname, lastname, email, birthdate);
    }
}
