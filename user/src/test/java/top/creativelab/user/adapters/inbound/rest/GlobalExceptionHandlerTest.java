package top.creativelab.user.adapters.inbound.rest;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import top.creativelab.user.application.exception.PasswordWrongExeption;
import top.creativelab.user.application.exception.UserNotFoundException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void userNotFoundMapsTo404() {
        ResponseEntity<Map<String, String>> response =
                handler.handleUserNotFound(new UserNotFoundException("john"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).containsKey("error");
    }

    @Test
    void wrongPasswordMapsTo400() {
        ResponseEntity<Map<String, String>> response =
                handler.handleWrongPassword(new PasswordWrongExeption());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsKey("error");
    }

    @Test
    void illegalArgumentMapsTo400() {
        ResponseEntity<Map<String, String>> response =
                handler.handleIllegalArgument(new IllegalArgumentException("bad"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("error", "bad");
    }

    @Test
    void illegalStateMapsTo409() {
        ResponseEntity<Map<String, String>> response =
                handler.handleIllegalState(new IllegalStateException("conflict"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).containsEntry("error", "conflict");
    }
}
