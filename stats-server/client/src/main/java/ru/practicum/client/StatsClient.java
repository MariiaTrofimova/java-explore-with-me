package ru.practicum.client;

import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsClient {
    List<ViewStatsDto> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);

    List<ViewStatsDto> getStatistics(LocalDateTime start, LocalDateTime end);

    List<ViewStatsDto> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris);

    List<ViewStatsDto> getStatistics(LocalDateTime start, LocalDateTime end, Boolean unique);

    void addEndPointHit(EndpointHitDto endpointHitDto);
}
