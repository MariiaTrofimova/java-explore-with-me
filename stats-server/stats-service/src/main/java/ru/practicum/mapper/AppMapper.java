package ru.practicum.mapper;

import ru.practicum.dto.ViewStatsDto;
import ru.practicum.model.App;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AppMapper {
    public static List<ViewStatsDto> toViewStatsDtoList (List<App> apps, Map<Long, Long> viewsByAppIds) {
        return apps.stream()
                .map(app -> ViewStatsDto.builder()
                        .app(app.getName())
                        .uri(app.getUri())
                        .hits(viewsByAppIds.get(app.getId()))
                        .build())
                .sorted()
                .collect(Collectors.toList());
    }
}
