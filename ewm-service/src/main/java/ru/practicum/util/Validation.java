package ru.practicum.util;

import javax.validation.ValidationException;
import java.time.Instant;
import java.time.LocalDateTime;

public class Validation {
    private static final LocalDateTime NOW = LocalDateTime.now();

    public static void validateStringField(String field, String fieldName, int min, int max) {
        if (field.isBlank()) {
            throw new ValidationException(String.format("Поле %s не может быть пустым", fieldName));
        }
        if (field.length() < min || field.length() > max) {
            throw new ValidationException(String.format("Длина поля %s должна быть от %d до %d", fieldName, min, max));
        }
    }

    public static void validatePositive(int field, String fieldName) {
        if (field < 0) {
            throw new ValidationException(String.format("Поле %s не может быть отрицательным", fieldName));
        }

    }

    public static void validatePositive(long field, String fieldName) {
        if (field < 0) {
            throw new ValidationException(String.format("Поле %s не может быть отрицательным", fieldName));
        }
    }

    public static void validateEventDate(LocalDateTime eventDateToUpdate) {
        if (!eventDateToUpdate.isAfter(NOW)) {
            throw new ValidationException("Поле eventDate должно содержать дату, которая еще не наступила");
        }
    }

    public static void validateStartEndDates(Instant start, Instant end) {
        if (!end.isAfter(start)) {
            throw new ValidationException("Дата окончания диапазона должна быть позже даты начала");
        }
    }
}
