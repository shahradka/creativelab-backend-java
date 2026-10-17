package top.creativelab.user.domain.user.vo;

import lombok.Value;

import java.time.LocalDate;

@Value
public class BirthDate {
    LocalDate value;

    public BirthDate(LocalDate value) {
        if (value.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Birth date cannot be in the future");
        }

        this.value = value;
    }
}
