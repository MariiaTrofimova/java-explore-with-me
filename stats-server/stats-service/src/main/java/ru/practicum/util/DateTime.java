package ru.practicum.util;

import javax.validation.ValidationException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class DateTime {
    public static final ZoneOffset ZONE_OFFSET = OffsetDateTime.now().getOffset();
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Instant parseDateTime(String dateTime) {
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, formatter);
        return localDateTime.toInstant(ZONE_OFFSET);
    }

    public static String decodeDateTime(String dateTime) {
        return URLDecoder.decode(dateTime, StandardCharsets.UTF_8);
    }

    public static String encodeDate(LocalDateTime dateTime) {
        return URLEncoder.encode(dateTime.format(formatter), StandardCharsets.UTF_8);
    }

    public static Instant toInstant(LocalDateTime localDateTime) {
        return localDateTime.toInstant(ZONE_OFFSET);
    }

    public static void validateStartEndDates(Instant start, Instant end) {
        if (!end.isAfter(start)) {
            throw new ValidationException("Дата окончания диапазона должна быть позже даты начала");
        }
    }
}
