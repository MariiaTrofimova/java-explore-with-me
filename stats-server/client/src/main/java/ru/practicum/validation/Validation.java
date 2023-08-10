package ru.practicum.validation;

import javax.validation.ValidationException;
import java.time.LocalDateTime;

public class Validation {

    public static void validateDates(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new ValidationException(String.format("Дата начала диапазона %s позже даты конца диапазона %s",
                    start, end));
        }
    }
}
