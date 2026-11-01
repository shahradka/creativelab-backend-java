package top.creativelab.user.application.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import top.creativelab.user.application.exception.PasswordWrongExeption;
import top.creativelab.user.application.exception.UserNotFoundException;
import top.creativelab.user.domain.user.User;
import top.creativelab.user.domain.user.UserStatus;
import top.creativelab.user.domain.user.vo.BirthDate;
import top.creativelab.user.domain.user.vo.Email;
import top.creativelab.user.domain.user.vo.Password;
import top.creativelab.user.domain.user.vo.UserId;
import top.creativelab.common.ports.PageResult;
import top.creativelab.user.ports.UserFilter;
import top.creativelab.user.ports.inbound.user.UpdateUserCommand;
import top.creativelab.user.ports.outbound.user.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserManagementServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserManagementService service;

    @BeforeEach
    void setUp() {
        service = new UserManagementService(userRepository, passwordEncoder);
    }

    private User existingUser() {
        return User.rehydrate(
                new UserId(UUID.randomUUID()),
                "john",
                Password.fromHash("encoded-old"),
                "John",
                "Doe",
                null,
                null,
                UserStatus.ACTIVE
        );
    }

    // =====================
    // create
    // =====================

    @Test
    void createSavesNewUser() {
        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(passwordEncoder.encode("Str0ng!Pass")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User user = service.create("john", "Str0ng!Pass");

        assertThat(user.getUsername()).isEqualTo("john");
        assertThat(user.getPassword().getHash()).isEqualTo("encoded");
        assertThat(user.getUserStatus()).isEqualTo(UserStatus.PENDING);
        verify(userRepository).save(user);
    }

    @Test
    void createRejectsDuplicateUsername() {
        when(userRepository.existsByUsername("john")).thenReturn(true);

        assertThatThrownBy(() -> service.create("john", "Str0ng!Pass"))
                .isInstanceOf(IllegalStateException.class);
        verify(userRepository, never()).save(any());
    }

    // =====================
    // update
    // =====================

    @Test
    void updateAppliesFields() {
        User user = existingUser();
        when(userRepository.findUserByUsername("john")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UpdateUserCommand command = new UpdateUserCommand("Jane", "Smith",
                new Email("jane@example.com"), new BirthDate(LocalDate.of(1991, 2, 2)));

        User updated = service.update("john", command);

        assertThat(updated.getFirstname()).isEqualTo("Jane");
        assertThat(updated.getLastname()).isEqualTo("Smith");
        assertThat(updated.getEmail()).isEqualTo(new Email("jane@example.com"));
        verify(userRepository).save(user);
    }

    @Test
    void updateIgnoresNullFields() {
        User user = existingUser();
        when(userRepository.findUserByUsername("john")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        service.update("john", new UpdateUserCommand(null, null, null, null));

        assertThat(user.getFirstname()).isEqualTo("John");
        assertThat(user.getLastname()).isEqualTo("Doe");
    }

    @Test
    void updateThrowsWhenUsernameEmpty() {
        UpdateUserCommand command = new UpdateUserCommand("Jane", "Smith", null, null);

        assertThatThrownBy(() -> service.update("", command))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updateThrowsWhenUserMissing() {
        when(userRepository.findUserByUsername("john")).thenReturn(Optional.empty());
        UpdateUserCommand command = new UpdateUserCommand("Jane", "Smith", null, null);

        assertThatThrownBy(() -> service.update("john", command))
                .isInstanceOf(UserNotFoundException.class);
    }

    // =====================
    // status transitions
    // =====================

    @Test
    void activateUserActivates() {
        User user = User.create(UserId.newId(), "john", Password.fromHash("h"));
        when(userRepository.findUserByUsername("john")).thenReturn(Optional.of(user));

        service.activateUser("john");

        assertThat(user.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
        verify(userRepository).save(user);
    }

    @Test
    void activateUserThrowsWhenMissing() {
        when(userRepository.findUserByUsername("john")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.activateUser("john"))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void suspendSuspends() {
        User user = existingUser();
        when(userRepository.findUserByUsername("john")).thenReturn(Optional.of(user));

        service.suspend("john");

        assertThat(user.getUserStatus()).isEqualTo(UserStatus.SUSPENDED);
        verify(userRepository).save(user);
    }

    @Test
    void reActiveReactivates() {
        User user = existingUser();
        user.suspend();
        when(userRepository.findUserByUsername("john")).thenReturn(Optional.of(user));

        service.reActive("john");

        assertThat(user.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
        verify(userRepository).save(user);
    }

    @Test
    void deleteSoftDeletes() {
        User user = existingUser();
        when(userRepository.findUserByUsername("john")).thenReturn(Optional.of(user));

        service.delete("john");

        assertThat(user.getUserStatus()).isEqualTo(UserStatus.DELETED);
        verify(userRepository).save(user);
    }

    // =====================
    // queries
    // =====================

    @Test
    void findActiveUsersDelegates() {
        List<User> users = List.of(existingUser());
        when(userRepository.findByStatus(UserStatus.ACTIVE)).thenReturn(users);

        assertThat(service.findActiveUsers()).isEqualTo(users);
    }

    @Test
    void findPendingUsersDelegates() {
        when(userRepository.findByStatus(UserStatus.PENDING)).thenReturn(List.of());

        assertThat(service.findPendingUsers()).isEmpty();
    }

    @Test
    void findSuspendedUsersDelegates() {
        when(userRepository.findByStatus(UserStatus.SUSPENDED)).thenReturn(List.of());

        assertThat(service.findSuspendedUsers()).isEmpty();
    }

    @Test
    void findDeletedUsersDelegates() {
        when(userRepository.findByStatus(UserStatus.DELETED)).thenReturn(List.of());

        assertThat(service.findDeletedUsers()).isEmpty();
    }

    @Test
    void findAllUsersDelegates() {
        when(userRepository.findAll()).thenReturn(List.of(existingUser()));

        assertThat(service.findAllUsers()).hasSize(1);
    }

    @Test
    void findUsersDelegatesToRepository() {
        UserFilter filter = new UserFilter("jo", null, null, null, UserStatus.ACTIVE);
        PageResult<User> page = PageResult.of(List.of(existingUser()), 0, 10, 1);
        when(userRepository.findPage(0, 10, filter)).thenReturn(page);

        assertThat(service.findUsers(0, 10, filter)).isEqualTo(page);
    }

    @Test
    void findUsersUsesEmptyFilterWhenNull() {
        PageResult<User> page = PageResult.of(List.of(), 0, 10, 0);
        when(userRepository.findPage(0, 10, UserFilter.empty())).thenReturn(page);

        assertThat(service.findUsers(0, 10, null)).isEqualTo(page);
    }

    @Test
    void findUsersRejectsNegativePage() {
        assertThatThrownBy(() -> service.findUsers(-1, 10, UserFilter.empty()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void findUsersRejectsInvalidSize() {
        assertThatThrownBy(() -> service.findUsers(0, 0, UserFilter.empty()))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.findUsers(0, 101, UserFilter.empty()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void countTotalUsersDelegates() {
        when(userRepository.count()).thenReturn(5L);

        assertThat(service.countTotalUsers()).isEqualTo(5L);
    }

    @Test
    void countActiveUsersDelegates() {
        when(userRepository.countByStatus(UserStatus.ACTIVE)).thenReturn(2L);

        assertThat(service.countActiveUsers()).isEqualTo(2L);
    }

    @Test
    void countPendingUsersDelegates() {
        when(userRepository.countByStatus(UserStatus.PENDING)).thenReturn(3L);

        assertThat(service.countPendingUsers()).isEqualTo(3L);
    }

    @Test
    void countSuspendedUsersDelegates() {
        when(userRepository.countByStatus(UserStatus.SUSPENDED)).thenReturn(1L);

        assertThat(service.countSuspendedUsers()).isEqualTo(1L);
    }

    @Test
    void countDeletedUsersDelegates() {
        when(userRepository.countByStatus(UserStatus.DELETED)).thenReturn(4L);

        assertThat(service.countDeletedUsers()).isEqualTo(4L);
    }

    @Test
    void findByUsernamePatternDelegates() {
        when(userRepository.findByUsernameContaining("jo")).thenReturn(List.of(existingUser()));

        assertThat(service.findByUsernamePattern("jo")).hasSize(1);
    }

    @Test
    void findByEmailPatternDelegates() {
        when(userRepository.findByEmailContaining("example")).thenReturn(List.of(existingUser()));

        assertThat(service.findByEmailPattern("example")).hasSize(1);
    }

    @Test
    void findByFirstnamePatternDelegates() {
        when(userRepository.findByFirstnameContaining("Jo")).thenReturn(List.of(existingUser()));

        assertThat(service.findByFirstnamePattern("Jo")).hasSize(1);
    }

    @Test
    void findByLastnamePatternDelegates() {
        when(userRepository.findByLastnameContaining("Do")).thenReturn(List.of(existingUser()));

        assertThat(service.findByLastnamePattern("Do")).hasSize(1);
    }

    @Test
    void existsByUsernameDelegates() {
        when(userRepository.existsByUsername("john")).thenReturn(true);

        assertThat(service.existsByUsername("john")).isTrue();
    }

    @Test
    void findByIdDelegates() {
        UUID id = UUID.randomUUID();
        User user = existingUser();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        assertThat(service.findById(id)).contains(user);
    }

    @Test
    void findUserByUsernameDelegates() {
        User user = existingUser();
        when(userRepository.findUserByUsername("john")).thenReturn(Optional.of(user));

        assertThat(service.findUserByUsername("john")).contains(user);
    }

    @Test
    void existsByIdDelegates() {
        UUID id = UUID.randomUUID();
        when(userRepository.existsById(id)).thenReturn(true);

        assertThat(service.existsById(id)).isTrue();
    }

    // =====================
    // changePassword
    // =====================

    @Test
    void changePasswordUpdatesWhenOldMatches() {
        User user = existingUser();
        when(userRepository.findUserByUsername("john")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Old!Pass1", "encoded-old")).thenReturn(true);
        when(passwordEncoder.encode("New!Pass1")).thenReturn("encoded-new");

        service.changePassword("john", "Old!Pass1", "New!Pass1");

        assertThat(user.getPassword().getHash()).isEqualTo("encoded-new");
        verify(userRepository).save(user);
    }

    @Test
    void changePasswordThrowsWhenOldWrong() {
        User user = existingUser();
        when(userRepository.findUserByUsername("john")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Wrong!Pass1", "encoded-old")).thenReturn(false);

        assertThatThrownBy(() -> service.changePassword("john", "Wrong!Pass1", "New!Pass1"))
                .isInstanceOf(PasswordWrongExeption.class);
        verify(userRepository, never()).save(any());
    }

    @Test
    void changePasswordThrowsWhenUserMissing() {
        when(userRepository.findUserByUsername("john")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.changePassword("john", "Old!Pass1", "New!Pass1"))
                .isInstanceOf(UserNotFoundException.class);
    }

    // =====================
    // validation
    // =====================

    @Test
    void validatePasswordAcceptsStrong() {
        assertThatCode(() -> service.validatePassword("Str0ng!Pass")).doesNotThrowAnyException();
    }

    @Test
    void validatePasswordRejectsWeak() {
        assertThatThrownBy(() -> service.validatePassword("weak"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void validateUsernameAcceptsValid() {
        assertThatCode(() -> service.validateUsername("john_doe")).doesNotThrowAnyException();
    }

    @Test
    void validateUsernameRejectsShort() {
        assertThatThrownBy(() -> service.validateUsername("jo"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void validateUsernameRejectsInvalidStart() {
        assertThatThrownBy(() -> service.validateUsername("1john"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void validateEmailAcceptsValid() {
        assertThatCode(() -> service.validateEmail(new Email("john@example.com")))
                .doesNotThrowAnyException();
    }

    @Test
    void validateEmailRejectsNull() {
        assertThatThrownBy(() -> service.validateEmail(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getUserByUsernameReturnsUser() {
        User user = existingUser();
        when(userRepository.findUserByUsername("john")).thenReturn(Optional.of(user));

        assertThat(service.getUserByUsername("john")).isEqualTo(user);
    }

    @Test
    void getUserByUsernameThrowsWhenMissing() {
        when(userRepository.findUserByUsername("john")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getUserByUsername("john"))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void getUserByIdReturnsUser() {
        UUID id = UUID.randomUUID();
        User user = existingUser();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        assertThat(service.getUserById(id)).isEqualTo(user);
    }

    @Test
    void getUserByIdThrowsWhenMissing() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getUserById(id))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void getUserByIdRejectsNull() {
        assertThatThrownBy(() -> service.getUserById(null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
