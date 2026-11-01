package top.creativelab.user.domain.user.vo;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class PasswordTest {

    @Test
    void fromRawEncodesValidPassword() {
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        when(encoder.encode("Str0ng!Pass")).thenReturn("encoded");

        Password password = Password.fromRaw("Str0ng!Pass", encoder);

        assertThat(password.getHash()).isEqualTo("encoded");
    }

    @Test
    void fromRawRejectsWeakPasswordWithoutEncoding() {
        PasswordEncoder encoder = mock(PasswordEncoder.class);

        assertThatThrownBy(() -> Password.fromRaw("weak", encoder))
                .isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(encoder);
    }

    @Test
    void fromHashWrapsHash() {
        assertThat(Password.fromHash("abc").getHash()).isEqualTo("abc");
    }

    @Test
    void validateAcceptsStrongPassword() {
        assertThatCode(() -> Password.validate("Str0ng!Pass")).doesNotThrowAnyException();
    }

    @Test
    void validateRejectsNull() {
        assertThatThrownBy(() -> Password.validate(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void validateRejectsMissingSpecialCharacter() {
        assertThatThrownBy(() -> Password.validate("Str0ngPass1"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void validateRejectsTooShort() {
        assertThatThrownBy(() -> Password.validate("Ab1!")).isInstanceOf(IllegalArgumentException.class);
    }
}
