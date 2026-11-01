package top.creativelab.user.domain.user;

import org.junit.jupiter.api.Test;
import top.creativelab.user.domain.user.vo.BirthDate;
import top.creativelab.user.domain.user.vo.Email;
import top.creativelab.user.domain.user.vo.Password;
import top.creativelab.user.domain.user.vo.UserId;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {

    private User newUser() {
        return User.create(UserId.newId(), "john", Password.fromHash("hash"));
    }

    @Test
    void createInitializesPendingStatusAndEmptyNames() {
        User user = newUser();

        assertThat(user.getUserStatus()).isEqualTo(UserStatus.PENDING);
        assertThat(user.getFirstname()).isEmpty();
        assertThat(user.getLastname()).isEmpty();
    }

    @Test
    void createWithNamesStoresNames() {
        User user = User.create(UserId.newId(), "john", Password.fromHash("hash"), "John", "Doe");

        assertThat(user.getFirstname()).isEqualTo("John");
        assertThat(user.getLastname()).isEqualTo("Doe");
    }

    @Test
    void activateFromPendingSetsActive() {
        User user = newUser();

        user.activate();

        assertThat(user.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void activateFromNonPendingThrows() {
        User user = newUser();
        user.activate();

        assertThatThrownBy(user::activate).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void suspendFromActiveSetsSuspended() {
        User user = newUser();
        user.activate();

        user.suspend();

        assertThat(user.getUserStatus()).isEqualTo(UserStatus.SUSPENDED);
    }

    @Test
    void suspendWhenAlreadySuspendedThrows() {
        User user = newUser();
        user.activate();
        user.suspend();

        assertThatThrownBy(user::suspend).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void reActiveFromSuspendedSetsActive() {
        User user = newUser();
        user.activate();
        user.suspend();

        user.reActive();

        assertThat(user.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void reActiveWhenNotSuspendedThrows() {
        User user = newUser();

        assertThatThrownBy(user::reActive).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deleteSetsDeleted() {
        User user = newUser();

        user.delete();

        assertThat(user.getUserStatus()).isEqualTo(UserStatus.DELETED);
    }

    @Test
    void deleteWhenAlreadyDeletedThrows() {
        User user = newUser();
        user.delete();

        assertThatThrownBy(user::delete).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void changePasswordReplacesPassword() {
        User user = newUser();
        Password newPassword = Password.fromHash("new-hash");

        user.changePassword(newPassword);

        assertThat(user.getPassword()).isEqualTo(newPassword);
    }

    @Test
    void changePasswordWithNullThrows() {
        User user = newUser();

        assertThatThrownBy(() -> user.changePassword(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updateFirstnameAndLastname() {
        User user = newUser();

        user.updateFirstname("Jane");
        user.updateLastname("Smith");

        assertThat(user.getFirstname()).isEqualTo("Jane");
        assertThat(user.getLastname()).isEqualTo("Smith");
    }

    @Test
    void updateEmailReplacesEmail() {
        User user = newUser();
        Email email = new Email("jane@example.com");

        user.updateEmail(email);

        assertThat(user.getEmail()).isEqualTo(email);

        Email replacement = new Email("other@example.com");
        user.updateEmail(replacement);

        assertThat(user.getEmail()).isEqualTo(replacement);
    }

    @Test
    void updateBirthdateStoresBirthDate() {
        User user = newUser();
        BirthDate birthDate = new BirthDate(LocalDate.of(1990, 1, 1));

        user.updateBirthdate(birthDate);

        assertThat(user.getBirthday()).isEqualTo(birthDate);
    }

    @Test
    void rehydratePreservesPersistedState() {
        UserId id = new UserId(UUID.randomUUID());
        Email email = new Email("jane@example.com");
        BirthDate birthDate = new BirthDate(LocalDate.of(1990, 1, 1));

        User user = User.rehydrate(id, "jane", Password.fromHash("hash"), "Jane", "Doe",
                email, birthDate, UserStatus.SUSPENDED);

        assertThat(user.getId()).isEqualTo(id);
        assertThat(user.getUsername()).isEqualTo("jane");
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getBirthday()).isEqualTo(birthDate);
        assertThat(user.getUserStatus()).isEqualTo(UserStatus.SUSPENDED);
    }
}
