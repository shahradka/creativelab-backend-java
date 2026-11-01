package top.creativelab.user.adapters.inbound.graphql.user;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import top.creativelab.user.domain.user.User;
import top.creativelab.common.ports.PageResult;
import top.creativelab.user.ports.UserFilter;
import top.creativelab.user.ports.inbound.user.UserManagementUseCase;

/**
 * GraphQL entry point for querying users with filtering and pagination.
 */
@Controller
public class UserGraphQlController {

    private final UserManagementUseCase userManagementUseCase;

    public UserGraphQlController(UserManagementUseCase userManagementUseCase) {
        this.userManagementUseCase = userManagementUseCase;
    }

    @QueryMapping
    public UserPageGraphQlResponse users(@Argument("page") int page,
                                         @Argument("size") int size,
                                         @Argument("filter") UserFilterInput filter) {
        UserFilter domainFilter = filter == null ? UserFilter.empty() : filter.toFilter();
        PageResult<User> result = userManagementUseCase.findUsers(page, size, domainFilter);
        return UserPageGraphQlResponse.from(result);
    }
}
