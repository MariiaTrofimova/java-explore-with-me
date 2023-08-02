package ru.practicum.service;

import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import java.util.List;

public interface StatsService {

    void add(EndpointHitDto endpointHitDto);

    List<ViewStatsDto> getStats(String start, String end, String[] uris, boolean unique);
}
