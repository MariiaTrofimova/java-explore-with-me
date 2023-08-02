package ru.practicum.repository;

import ru.practicum.dto.ViewStatsDto;
import ru.practicum.model.EndpointHit;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface EndPointHitRepository {
    void addEndpointHit(EndpointHit endpointHit);

    Map<Long, Long> getViewsByAppId(Instant start, Instant end);

    Map<Long, Long> getViewsByAppId(Instant start, Instant end, List<Long> appIds);

    Map<Long, Long> getUniqueViewsByAppId(Instant start, Instant end);

    Map<Long, Long> getUniqueViewsByAppId(Instant start, Instant end, List<Long> appIds);
}
