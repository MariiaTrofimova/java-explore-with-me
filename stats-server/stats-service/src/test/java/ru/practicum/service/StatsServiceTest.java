package ru.practicum.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.mapper.EndpointHitMapper;
import ru.practicum.model.App;
import ru.practicum.model.EndpointHit;
import ru.practicum.repository.AppRepository;
import ru.practicum.repository.EndPointHitRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static ru.practicum.util.DateTime.encodeDate;
import static ru.practicum.util.DateTime.formatter;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

    @Mock
    private AppRepository appRepo;

    @Mock
    private EndPointHitRepository hitRepo;

    @InjectMocks
    private StatsServiceImpl service;
    private static final LocalDateTime NOW = LocalDateTime.now();

    private EndpointHitDto hitDto;
    private EndpointHitDto.EndpointHitDtoBuilder hitDtoBuilder;

    private String start;
    private String end;

    @BeforeEach
    void setup() {
        String timestamp = NOW.format(formatter);
        String app = "ewm-main-service";
        String uri = "/events/1";
        String ip = "121.0.0.1";
        hitDtoBuilder = EndpointHitDto.builder()
                .app(app)
                .uri(uri)
                .ip(ip)
                .timestamp(timestamp);

        start = encodeDate(NOW.minusHours(1));
        end = encodeDate(NOW);

    }

    @Test
    void shouldAddHitForNewApp() {
        hitDto = hitDtoBuilder.build();
        long appId = 1L;

        //App is not in DB
        when(appRepo.findByAppAndUri(hitDto.getApp(), hitDto.getUri())).thenReturn(Collections.emptyList());
        App appToAdd = App.builder()
                .name(hitDto.getApp())
                .uri(hitDto.getUri())
                .build();
        when(appRepo.add(appToAdd)).thenReturn(appId);
        EndpointHit hit = EndpointHitMapper.toEndpointHit(hitDto, appId);
        EndpointHit hitAdded = EndpointHitMapper.toEndpointHit(hitDtoBuilder.build(), appId);
        hitAdded.setId(1L);
        when(hitRepo.addEndpointHit(hit)).thenReturn(hitAdded);

        service.addHit(hitDto);
    }

    @Test
    void shouldAddHitForExistingApp() {
        hitDto = hitDtoBuilder.build();
        long appId = 1L;

        App app = App.builder()
                .id(appId)
                .name(hitDto.getApp())
                .uri(hitDto.getUri())
                .build();

        //App is in DB
        when(appRepo.findByAppAndUri(hitDto.getApp(), hitDto.getUri())).thenReturn(List.of(app));
        EndpointHit hit = EndpointHitMapper.toEndpointHit(hitDto, appId);
        EndpointHit hitAdded = EndpointHitMapper.toEndpointHit(hitDtoBuilder.build(), appId);
        hitAdded.setId(1L);
        when(hitRepo.addEndpointHit(hit)).thenReturn(hitAdded);

        service.addHit(hitDto);
    }

    @Test
    void shouldGetStatsForEmptyListOfUris() {
        //Not Unique  | EmptyList
        when(hitRepo.getViewsByAppId(any(), any())).thenReturn(Collections.emptyMap());
        List<ViewStatsDto> viewStatsDTOs = service.getStats(start, end, Collections.emptyList(), false);
        assertThat(viewStatsDTOs)
                .isNotNull()
                .hasSize(0);
    }

    @Test
    void shouldGetStatsForEmptyListOfUrisSingleList() {
        //Not Unique | SingleList
        String appName = "ewm-main-service";
        String uri = "/events/1";
        long appId = 1L;
        long hitsQty = 1L;
        Map<Long, Long> hitsQtyByAppId = Map.of(appId, hitsQty);
        App app = App.builder()
                .id(appId)
                .name(appName)
                .uri(uri)
                .build();
        when(hitRepo.getViewsByAppId(any(), any())).thenReturn(hitsQtyByAppId);
        when(appRepo.getAppsByIds(List.of(appId))).thenReturn(List.of(app));
        List<ViewStatsDto> viewStatsDTOs = service.getStats(start, end, Collections.emptyList(), false);

        assertThat(viewStatsDTOs)
                .isNotNull()
                .hasSize(1);
        assertEquals(appName, viewStatsDTOs.get(0).getApp());
    }

    @Test
    void shouldGetStatsWithListOfUrisEmptyList() {
        //Not Unique  | EmptyList
        String uri = "/events/1";
        when(appRepo.getAppsByUris(List.of(uri))).thenReturn(Collections.emptyList());
        List<ViewStatsDto> viewStatsDTOs = service.getStats(start, end, List.of(uri), false);
        assertThat(viewStatsDTOs)
                .isNotNull()
                .hasSize(0);
    }

    @Test
    void shouldGetStatsWithListOfUrisSingleList() {
        //Not Unique  | SingleList
        long appId = 1L;
        long hitsQty = 1L;
        Map<Long, Long> hitsQtyByAppId = Map.of(appId, hitsQty);
        String appName = "ewm-main-service";
        String uri = "/events/1";

        App app = App.builder()
                .id(appId)
                .name(appName)
                .uri(uri)
                .build();

        when(appRepo.getAppsByUris(List.of(uri))).thenReturn(List.of(app));
        when(hitRepo.getViewsByAppId(any(), any(), anyList())).thenReturn(hitsQtyByAppId);
        List<ViewStatsDto> viewStatsDTOs = service.getStats(start, end, List.of(uri), false);
        assertThat(viewStatsDTOs)
                .isNotNull()
                .hasSize(1);
    }
}