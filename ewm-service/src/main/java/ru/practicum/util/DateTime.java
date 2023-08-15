package ru.practicum.util;

import java.time.*;

public class DateTime {
    private static final ZoneId ZONE_ID = ZoneId.systemDefault();
    public static final ZoneOffset ZONE_OFFSET = OffsetDateTime.now().getOffset();

    public static Instant toInstant(LocalDateTime localDateTime) {
        return localDateTime.toInstant(ZONE_OFFSET);
    }

    public static LocalDateTime toLocalDateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZONE_ID);
    }
}
