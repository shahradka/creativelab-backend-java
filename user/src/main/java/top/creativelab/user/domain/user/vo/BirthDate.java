package top.creativelab.user.domain.user.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Value;

import java.time.LocalDate;

@Value
public class BirthDate {
    LocalDate value;

    @JsonCreator
    public BirthDate(LocalDate value) {
        if (value == null) {
            throw new IllegalArgumentException("Birth date cannot be null");
        }
        if (value.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Birth date cannot be in the future");
        }

        this.value = value;
    }

    @JsonValue
    public LocalDate getValue() {
        return value;
    }
}
