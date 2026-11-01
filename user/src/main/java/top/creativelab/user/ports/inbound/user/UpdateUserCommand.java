package top.creativelab.user.ports.inbound.user;

import top.creativelab.user.domain.user.vo.BirthDate;
import top.creativelab.user.domain.user.vo.Email;

/**
 * Inbound command describing the mutable fields of a user profile.
 * Kept in the ports layer so the application service does not depend on REST DTOs.
 */
public record UpdateUserCommand(
        String firstname,
        String lastname,
        Email email,
        BirthDate birthdate
) {
}
