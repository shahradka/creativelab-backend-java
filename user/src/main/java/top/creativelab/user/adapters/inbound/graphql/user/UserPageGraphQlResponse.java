package top.creativelab.user.adapters.inbound.graphql.user;

import top.creativelab.user.domain.user.User;
import top.creativelab.common.ports.PageResult;

import java.util.List;

/**
 * GraphQL representation of a page of users, including the total counts.
 */
public record UserPageGraphQlResponse(
        List<UserGraphQlResponse> content,
        int page,
        int size,
        int totalElements,
        int totalPages
) {

    public static UserPageGraphQlResponse from(PageResult<User> result) {
        return new UserPageGraphQlResponse(
                result.content().stream().map(UserGraphQlResponse::from).toList(),
                result.page(),
                result.size(),
                Math.toIntExact(result.totalElements()),
                result.totalPages()
        );
    }
}
