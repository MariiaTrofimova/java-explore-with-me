package ru.practicum.validation;

import javax.validation.ValidationException;
import java.time.LocalDateTime;

public class Validation {

    public static void validateDates(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new ValidationException("Дата начала диапазона не может быть позже даты конца диапазона");
        }
    }
}
