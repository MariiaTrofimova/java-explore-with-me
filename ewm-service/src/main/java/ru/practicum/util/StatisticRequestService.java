package ru.practicum.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.event.model.Event;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.event.enums.EventState.PUBLISHED;
import static ru.practicum.util.Statistics.getStartTime;
import static ru.practicum.util.Statistics.makeUris;

@Service
@RequiredArgsConstructor
public class StatisticRequestService {
    private final StatsClient client;

    public List<ViewStatsDto> makeStatRequest(List<Event> events) {
        if (events.stream().noneMatch(event -> event.getEventState() == PUBLISHED)) {
            return Collections.emptyList();
        }
        List<Event> eventsPublished = events.stream()
                .filter(event -> event.getEventState() == PUBLISHED)
                .collect(Collectors.toList());
        List<String> uris = makeUris(eventsPublished);
        LocalDateTime startStat = getStartTime(eventsPublished);
        boolean unique = true;
        return client.getStatistics(startStat.minusHours(1), LocalDateTime.now(), uris, unique);
    }

}
