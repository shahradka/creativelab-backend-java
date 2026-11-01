package top.creativelab.user.adapters.inbound.rest.user;

import org.junit.jupiter.api.Test;
import top.creativelab.user.domain.user.User;
import top.creativelab.user.domain.user.UserStatus;
import top.creativelab.user.domain.user.vo.BirthDate;
import top.creativelab.user.domain.user.vo.Email;
import top.creativelab.user.domain.user.vo.Password;
import top.creativelab.user.domain.user.vo.UserId;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserResponseTest {

    @Test
    void fromDomainMapsAllFields() {
        UUID id = UUID.randomUUID();
        Email email = new Email("john@example.com");
        BirthDate birthDate = new BirthDate(LocalDate.of(1990, 1, 1));
        User user = User.rehydrate(new UserId(id), "john", Password.fromHash("hash"),
                "John", "Doe", email, birthDate, UserStatus.ACTIVE);

        UserResponse response = UserResponse.fromDomain(user);

        assertThat(response.getId()).isEqualTo(id);
        assertThat(response.getUsername()).isEqualTo("john");
        assertThat(response.getFirstname()).isEqualTo("John");
        assertThat(response.getLastname()).isEqualTo("Doe");
        assertThat(response.getEmail()).isEqualTo(email);
        assertThat(response.getBirthday()).isEqualTo(birthDate);
        assertThat(response.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
    }
}
