package ru.practicum.util;

import javax.validation.ValidationException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateTime {
    private static final ZoneId ZONE_ID = ZoneId.systemDefault();
    public static final ZoneOffset ZONE_OFFSET = OffsetDateTime.now().getOffset();
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Instant parseDateTime(String dateTime) {
        LocalDateTime localDateTime;
        try {
            localDateTime = LocalDateTime.parse(dateTime, formatter);
        } catch (DateTimeParseException e) {
            throw new ValidationException(e.getMessage());
        }
        return localDateTime.toInstant(ZONE_OFFSET);
    }

    public static String decodeDateTime(String dateTime) {
        return URLDecoder.decode(dateTime, StandardCharsets.UTF_8);
    }

    public static Instant toInstant(LocalDateTime localDateTime) {
        return localDateTime.toInstant(ZONE_OFFSET);
    }

    public static LocalDateTime toLocalDateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZONE_ID);
    }

    public static Instant parseEncodedDateTime(String dateTime) {
        String decodedDateTime = decodeDateTime(dateTime);
        return parseDateTime(decodedDateTime);

    }
}
