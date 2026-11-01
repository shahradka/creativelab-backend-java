package top.creativelab.user.adapters.inbound.graphql.user;

import top.creativelab.user.domain.user.UserStatus;
import top.creativelab.user.ports.UserFilter;

/**
 * GraphQL input object for filtering the user list.
 */
public record UserFilterInput(
        String username,
        String email,
        String firstname,
        String lastname,
        UserStatus status
) {

    public UserFilter toFilter() {
        return new UserFilter(username, email, firstname, lastname, status);
    }
}
