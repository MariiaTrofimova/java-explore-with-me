package ru.practicum.service;

import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import java.util.List;

public interface StatsService {

    void addHit(EndpointHitDto endpointHitDto);

    List<ViewStatsDto> getStats(String start, String end, List<String> uris, boolean unique);
}
