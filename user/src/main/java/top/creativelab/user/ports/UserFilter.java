package top.creativelab.user.ports;

import top.creativelab.user.domain.user.UserStatus;

/**
 * Optional filters applied when listing users. A {@code null} field means "no filter".
 */
public record UserFilter(
        String username,
        String email,
        String firstname,
        String lastname,
        UserStatus status
) {

    public static UserFilter empty() {
        return new UserFilter(null, null, null, null, null);
    }
}
