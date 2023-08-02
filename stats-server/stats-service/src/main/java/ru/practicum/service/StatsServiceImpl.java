package ru.practicum.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.mapper.EndpointHitMapper;
import ru.practicum.model.App;
import ru.practicum.model.EndpointHit;
import ru.practicum.repository.AppRepository;
import ru.practicum.repository.EndPointHitRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.util.dateTime.decodeDateTime;
import static ru.practicum.util.dateTime.parseDateTime;

@Service
@Transactional(readOnly = true)
@Slf4j
@AllArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final EndPointHitRepository hitRepo;
    private final AppRepository appRepo;

    @Override
    @Transactional
    public void add(EndpointHitDto endpointHitDto) {
        String name = endpointHitDto.getApp();
        String uri = endpointHitDto.getUri();
        long appId;
        Optional<App> appOptional = appRepo.findByAppAndUri(name, uri);

        if (appOptional.isEmpty()) {
            App appToAdd = App.builder()
                    .name(name)
                    .uri(uri)
                    .build();
            appId = appRepo.add(appToAdd);
        } else {
            appId = appOptional.get().getId();
        }

        EndpointHit endpointHit = EndpointHitMapper.toEndpointHit(endpointHitDto, appId);
        hitRepo.addEndpointHit(endpointHit);
        log.debug("Пользователь {} просмотрел uri {} сервиса {}", endpointHit.getIp(), uri, name);
    }

    @Override
    public List<ViewStatsDto> getStats(String startString, String endString, String[] uris, boolean unique) {
        String startDecoded = decodeDateTime(startString);
        String endDecoded = decodeDateTime(endString);
        Instant start = parseDateTime(startDecoded);
        Instant end = parseDateTime(endDecoded);

        Map<Long, Long> viewsByAppId;
        List<App> apps;
        List<Long> appIds;

        if (uris.length == 0) {
            viewsByAppId = unique ? hitRepo.getViewsByAppId(start, end) : hitRepo.getUniqueViewsByAppId(start, end);
            appIds = new ArrayList<>(viewsByAppId.keySet());
            apps = appRepo.getAppsByIds(appIds);
        } else {
            apps = appRepo.getAppsByUris(uris);
            appIds = apps.stream().map(App::getId).collect(Collectors.toList());
            viewsByAppId = unique ? hitRepo.getViewsByAppId(start, end, appIds) :
                    hitRepo.getUniqueViewsByAppId(start, end, appIds);
        }

        return apps.stream()
                .map(app -> ViewStatsDto.builder()
                        .app(app.getName())
                        .uri(app.getUri())
                        .hits(viewsByAppId.get(app.getId()))
                        .build())
                .collect(Collectors.toList());
    }
}