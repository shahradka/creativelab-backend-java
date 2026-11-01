package top.creativelab.user.adapters.inbound.graphql.user;

import top.creativelab.user.domain.user.User;
import top.creativelab.user.domain.user.UserStatus;

/**
 * GraphQL representation of a user. Value objects are flattened to scalars.
 */
public record UserGraphQlResponse(
        String id,
        String username,
        String firstname,
        String lastname,
        String email,
        String birthday,
        UserStatus status
) {

    public static UserGraphQlResponse from(User user) {
        return new UserGraphQlResponse(
                user.getId().getValue().toString(),
                user.getUsername(),
                user.getFirstname(),
                user.getLastname(),
                user.getEmail() != null ? user.getEmail().getValue() : null,
                user.getBirthday() != null ? user.getBirthday().getValue().toString() : null,
                user.getUserStatus()
        );
    }
}
