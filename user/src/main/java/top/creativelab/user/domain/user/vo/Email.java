package top.creativelab.user.domain.user.vo;

import lombok.Value;

@Value
public class Email {

    String value;

    public Email(String value) {
        if (value == null || !value.matches("^[^@]+@[^@]+\\.[^@]+$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.value = value.toLowerCase();
    }
}