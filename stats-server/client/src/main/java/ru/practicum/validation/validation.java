package ru.practicum.validation;

import ru.practicum.dto.EndpointHitDto;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class validation {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void validateDates(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new ValidationException("Дата начала диапазона не может быть позже даты конца диапазона");
        }
    }

    public static void validateEndPointHitDto(EndpointHitDto endpointHitDto) {
        if (!isValid(endpointHitDto.getTimestamp())) {
            throw new ValidationException("Некорректный формат даты");
        }
    }

    private static boolean isValid(String dateTimeStr) {
        try {
            LocalDateTime.parse(dateTimeStr, formatter);
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }
}
