package top.creativelab.user.domain.user.vo;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BirthDateTest {

    @Test
    void acceptsPastDate() {
        LocalDate date = LocalDate.of(1990, 1, 1);

        assertThat(new BirthDate(date).getValue()).isEqualTo(date);
    }

    @Test
    void rejectsFutureDate() {
        assertThatThrownBy(() -> new BirthDate(LocalDate.now().plusDays(1)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsNull() {
        assertThatThrownBy(() -> new BirthDate(null)).isInstanceOf(IllegalArgumentException.class);
    }
}
