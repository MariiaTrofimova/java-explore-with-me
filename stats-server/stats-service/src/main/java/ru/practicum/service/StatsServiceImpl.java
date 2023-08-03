package ru.practicum.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.mapper.AppMapper;
import ru.practicum.mapper.EndpointHitMapper;
import ru.practicum.model.App;
import ru.practicum.model.EndpointHit;
import ru.practicum.repository.AppRepository;
import ru.practicum.repository.EndPointHitRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
    public void addHit(EndpointHitDto endpointHitDto) {
        String name = endpointHitDto.getApp();
        String uri = endpointHitDto.getUri();
        long appId;

        List<App> apps = appRepo.findByAppAndUri(name, uri);

        if (apps.isEmpty()) {
            App appToAdd = App.builder()
                    .name(name)
                    .uri(uri)
                    .build();
            appId = appRepo.add(appToAdd);
        } else {
            appId = apps.get(0).getId();
        }

        EndpointHit endpointHit = EndpointHitMapper.toEndpointHit(endpointHitDto, appId);
        hitRepo.addEndpointHit(endpointHit);
        log.debug("Пользователь {} просмотрел uri {} сервиса {}", endpointHit.getIp(), uri, name);
    }

    @Override
    public List<ViewStatsDto> getStats(String startEncoded, String endEncoded, List<String> uris, boolean unique) {
        String startDecoded = decodeDateTime(startEncoded);
        String endDecoded = decodeDateTime(endEncoded);

        Instant start = parseDateTime(startDecoded);
        Instant end = parseDateTime(endDecoded);

        Map<Long, Long> viewsByAppId;
        List<App> apps;
        List<Long> appIds;

        if (uris.isEmpty()) {
            viewsByAppId = unique ? hitRepo.getUniqueViewsByAppId(start, end) : hitRepo.getViewsByAppId(start, end);
            if (viewsByAppId.isEmpty()) {
                log.debug("Пустой отчет для периода с {} по {}. Уникальные просмотры {}",
                        start, end, unique);
                return Collections.emptyList();
            }
            appIds = new ArrayList<>(viewsByAppId.keySet());
            apps = appRepo.getAppsByIds(appIds);
        } else {
            apps = appRepo.getAppsByUris(uris);
            if (apps.isEmpty()) {
                log.debug("Пустой отчет для периода с {} по {}. Список uri {}. Уникальные просмотры {}",
                        start, end, uris, unique);
                return Collections.emptyList();
            }
            appIds = apps.stream().map(App::getId).collect(Collectors.toList());
            viewsByAppId = unique ? hitRepo.getUniqueViewsByAppId(start, end, appIds) :
                    hitRepo.getViewsByAppId(start, end, appIds);
        }
        log.debug("Запрос статистики для периода с {} по {}. Список uri {}. Уникальные просмотры {}",
                start, end, uris, unique);
        return AppMapper.toViewStatsDtoList(apps, viewsByAppId);
    }
}