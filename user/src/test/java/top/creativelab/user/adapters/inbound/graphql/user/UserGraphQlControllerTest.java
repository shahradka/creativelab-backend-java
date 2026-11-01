package top.creativelab.user.adapters.inbound.graphql.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import top.creativelab.user.domain.user.User;
import top.creativelab.user.domain.user.UserStatus;
import top.creativelab.user.domain.user.vo.BirthDate;
import top.creativelab.user.domain.user.vo.Email;
import top.creativelab.user.domain.user.vo.Password;
import top.creativelab.user.domain.user.vo.UserId;
import top.creativelab.common.ports.PageResult;
import top.creativelab.user.ports.UserFilter;
import top.creativelab.user.ports.inbound.user.UserManagementUseCase;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserGraphQlControllerTest {

    @Mock
    private UserManagementUseCase useCase;

    private UserGraphQlController controller;

    @BeforeEach
    void setUp() {
        controller = new UserGraphQlController(useCase);
    }

    private User user() {
        return User.rehydrate(
                new UserId(UUID.randomUUID()),
                "john",
                Password.fromHash("hash"),
                "John",
                "Doe",
                new Email("john@example.com"),
                new BirthDate(LocalDate.of(1990, 1, 1)),
                UserStatus.ACTIVE
        );
    }

    @Test
    void usersMapsPageAndFilter() {
        UserFilter filter = new UserFilter("jo", null, null, null, UserStatus.ACTIVE);
        PageResult<User> page = PageResult.of(List.of(user()), 0, 10, 1);
        when(useCase.findUsers(0, 10, filter)).thenReturn(page);

        UserPageGraphQlResponse response = controller.users(0, 10,
                new UserFilterInput("jo", null, null, null, UserStatus.ACTIVE));

        assertThat(response.content()).hasSize(1);
        assertThat(response.content().getFirst().username()).isEqualTo("john");
        assertThat(response.content().getFirst().email()).isEqualTo("john@example.com");
        assertThat(response.content().getFirst().birthday()).isEqualTo("1990-01-01");
        assertThat(response.totalElements()).isEqualTo(1);
        assertThat(response.totalPages()).isEqualTo(1);
    }

    @Test
    void usersUsesEmptyFilterWhenNull() {
        PageResult<User> page = PageResult.of(List.of(), 0, 10, 0);
        when(useCase.findUsers(0, 10, UserFilter.empty())).thenReturn(page);

        UserPageGraphQlResponse response = controller.users(0, 10, null);

        assertThat(response.content()).isEmpty();
        verify(useCase).findUsers(0, 10, UserFilter.empty());
    }
}
