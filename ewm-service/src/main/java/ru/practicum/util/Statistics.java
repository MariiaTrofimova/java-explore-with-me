package ru.practicum.util;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.client.exception.StatsRequestException;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.event.model.Event;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import static ru.practicum.util.DateTime.toLocalDateTime;

@Slf4j
public class Statistics {
    private static final String URI = "events";

    public static List<String> makeUris(List<Event> events) {
        List<Long> eventIds = events.stream().map(Event::getId).collect(Collectors.toList());
        return eventIds.stream().map(id -> "/" + URI + "/" + id).collect(Collectors.toList());
    }

    public static LocalDateTime getStartTime(List<Event> events) {
        Instant startStatInst = events.stream()
                .map(Event::getPublishedOn)
                .filter(Objects::nonNull)
                .sorted().collect(Collectors.toList()).get(0);
        return toLocalDateTime(startStatInst);
    }

    public static long getEventId(ViewStatsDto viewStatsDto) {
        StringTokenizer tokenizer = new StringTokenizer(viewStatsDto.getUri(), "/");
        if (!tokenizer.nextToken().equals(URI)) {
            log.warn("Ошибка запроса статистики");
            throw new StatsRequestException("Ошибка запроса статистики");
        }
        try {
            return Long.parseLong(tokenizer.nextToken());
        } catch (NumberFormatException e) {
            throw new StatsRequestException("Ошибка запроса данных статистики");
        }
    }
}
