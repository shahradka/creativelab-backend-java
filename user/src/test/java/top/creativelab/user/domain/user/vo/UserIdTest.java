package top.creativelab.user.domain.user.vo;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserIdTest {

    @Test
    void newIdGeneratesValue() {
        assertThat(UserId.newId().getValue()).isNotNull();
    }

    @Test
    void newIdGeneratesUniqueValues() {
        assertThat(UserId.newId()).isNotEqualTo(UserId.newId());
    }
}
