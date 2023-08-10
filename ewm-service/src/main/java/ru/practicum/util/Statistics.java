package ru.practicum.util;

import ru.practicum.event.model.Event;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.util.DateTime.toLocalDateTime;

public class Statistics {
    private static final String URI = "/events";

    public static List<String> makeUris(List<Event> events) {
        List<Long> eventIds = events.stream().map(Event::getId).collect(Collectors.toList());
        return eventIds.stream().map(id -> URI + "/" + id).collect(Collectors.toList());
    }

    public static LocalDateTime getStartTime(List<Event> events) {
        Instant startStatInst = events.stream()
                .map(Event::getPublishedOn)
                .filter(Objects::nonNull)
                .sorted().collect(Collectors.toList()).get(0);
        return toLocalDateTime(startStatInst);
    }
}
