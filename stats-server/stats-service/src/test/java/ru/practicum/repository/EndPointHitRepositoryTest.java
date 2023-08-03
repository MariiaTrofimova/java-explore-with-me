package ru.practicum.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.model.App;
import ru.practicum.model.EndpointHit;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.practicum.util.dateTime.toInstant;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
class EndPointHitRepositoryTest {
    private static final LocalDateTime NOW = LocalDateTime.now();

    private final EndPointHitRepository hitRepo;
    private final AppRepository appRepo;

    private EndpointHit hit;
    private EndpointHit.EndpointHitBuilder hitBuilder;
    private App.AppBuilder appBuilder;
    private Instant start;
    private Instant end;

    @BeforeEach
    void setup() {
        String appName = "ewm-main-service";
        String uri = "/events/1";
        String ip = "121.0.0.1";
        Instant timestamp = toInstant(NOW);

        appBuilder = App.builder()
                .name(appName)
                .uri(uri);

        App app = appBuilder.build();
        long appId = appRepo.add(app);

        hitBuilder = EndpointHit.builder()
                .appId(appId)
                .ip(ip)
                .timestamp(timestamp);

        start = toInstant(NOW.minusHours(1));
        end = toInstant(NOW);
    }

    @Test
    void addEndpointHit() {
        hit = hitBuilder.build();
        EndpointHit hitAdded = hitRepo.addEndpointHit(hit);

        assertThat(hitAdded)
                .isNotNull()
                .hasFieldOrPropertyWithValue("appId", hit.getAppId());
    }

    @Test
    void getHitsQtyByAppIdWithoutUrisUniqueFalse() {
        //EmptyMap
        Map<Long, Long> hitsQtyByAppId = hitRepo.getViewsByAppId(start, end);
        assertThat(hitsQtyByAppId)
                .isNotNull()
                .hasSize(0);

        //SingleMap
        hit = hitBuilder.build();
        EndpointHit hitAdded = hitRepo.addEndpointHit(hit);

        hitsQtyByAppId = hitRepo.getViewsByAppId(start, end.plusSeconds(5));
        assertThat(hitsQtyByAppId)
                .isNotNull()
                .hasSize(1);
        long appId = hitAdded.getAppId();
        assertTrue(hitsQtyByAppId.containsKey(appId));
        assertEquals(1, hitsQtyByAppId.get(appId));

        //Regular Case
        EndpointHit hit1 = hitBuilder.ip("121.0.0.2").build();
        hitRepo.addEndpointHit(hit1);

        hitsQtyByAppId = hitRepo.getViewsByAppId(start, end.plusSeconds(5));
        assertThat(hitsQtyByAppId)
                .isNotNull()
                .hasSize(1);
        assertTrue(hitsQtyByAppId.containsKey(appId));
        assertEquals(2, hitsQtyByAppId.get(appId));

        //Two apps
        App app2 = appBuilder.uri("/events/2").build();
        long appId2 = appRepo.add(app2);
        EndpointHit hit2 = hitBuilder.appId(appId2).build();
        hitRepo.addEndpointHit(hit2);

        hitsQtyByAppId = hitRepo.getViewsByAppId(start, end.plusSeconds(5));
        assertThat(hitsQtyByAppId)
                .isNotNull()
                .hasSize(2);
        assertTrue(hitsQtyByAppId.containsKey(appId2));
        assertEquals(1, hitsQtyByAppId.get(appId2));
    }

    @Test
    void testGetHitsQtyByAppIdWithoutUrisUniqueTrue() {
        //EmptyMap
        Map<Long, Long> hitsQtyByAppId = hitRepo.getUniqueViewsByAppId(start, end);
        assertThat(hitsQtyByAppId)
                .isNotNull()
                .hasSize(0);

        //SingleMap
        hit = hitBuilder.build();
        EndpointHit hitAdded = hitRepo.addEndpointHit(hit);

        hitsQtyByAppId = hitRepo.getUniqueViewsByAppId(start, end);
        assertThat(hitsQtyByAppId)
                .isNotNull()
                .hasSize(1);
        long appId = hitAdded.getAppId();
        assertTrue(hitsQtyByAppId.containsKey(appId));
        assertEquals(1, hitsQtyByAppId.get(appId));

        //Double View
        EndpointHit hit1 = hitBuilder.timestamp(Instant.now()).build();
        hitRepo.addEndpointHit(hit1);

        hitsQtyByAppId = hitRepo.getUniqueViewsByAppId(start, end.plusSeconds(5));
        assertThat(hitsQtyByAppId)
                .isNotNull()
                .hasSize(1);
        assertTrue(hitsQtyByAppId.containsKey(appId));
        assertEquals(1, hitsQtyByAppId.get(appId));
    }

    @Test
    void getUniqueHitsQtyByAppIdWithUrisUniqueFalse() {
        //EmptyMap
        Map<Long, Long> hitsQtyByAppId = hitRepo.getViewsByAppId(start, end, List.of(1L));
        assertThat(hitsQtyByAppId)
                .isNotNull()
                .hasSize(0);

        //Single Map
        hit = hitBuilder.build();
        EndpointHit hitAdded = hitRepo.addEndpointHit(hit);
        long appId = hitAdded.getAppId();

        hitsQtyByAppId = hitRepo.getViewsByAppId(start, end, List.of(appId));
        assertThat(hitsQtyByAppId)
                .isNotNull()
                .hasSize(1);
        assertTrue(hitsQtyByAppId.containsKey(appId));
        assertEquals(1, hitsQtyByAppId.get(appId));

        //Two apps
        App app2 = appBuilder.uri("/events/2").build();
        long appId2 = appRepo.add(app2);
        EndpointHit hit2 = hitBuilder.appId(appId2).build();
        hitRepo.addEndpointHit(hit2);

        hitsQtyByAppId = hitRepo.getViewsByAppId(start, end, List.of(appId, appId2));
        assertThat(hitsQtyByAppId)
                .isNotNull()
                .hasSize(2);
        assertTrue(hitsQtyByAppId.containsKey(appId2));
        assertEquals(1, hitsQtyByAppId.get(appId2));
    }

    @Test
    void testGetUniqueHitsQtyByAppIdWithUrisUniqueTrue() {
        //EmptyMap
        Map<Long, Long> hitsQtyByAppId = hitRepo.getUniqueViewsByAppId(start, end, List.of(1L));
        assertThat(hitsQtyByAppId)
                .isNotNull()
                .hasSize(0);

        //Single Map
        hit = hitBuilder.build();
        EndpointHit hitAdded = hitRepo.addEndpointHit(hit);
        long appId = hitAdded.getAppId();

        hitsQtyByAppId = hitRepo.getUniqueViewsByAppId(start, end, List.of(appId));
        assertThat(hitsQtyByAppId)
                .isNotNull()
                .hasSize(1);
        assertTrue(hitsQtyByAppId.containsKey(appId));
        assertEquals(1, hitsQtyByAppId.get(appId));

        //Double View
        EndpointHit hit1 = hitBuilder.timestamp(Instant.now()).build();
        hitRepo.addEndpointHit(hit1);

        hitsQtyByAppId = hitRepo.getUniqueViewsByAppId(start, end.plusSeconds(5), List.of(appId));
        assertThat(hitsQtyByAppId)
                .isNotNull()
                .hasSize(1);
        assertTrue(hitsQtyByAppId.containsKey(appId));
        assertEquals(1, hitsQtyByAppId.get(appId));

        //Two apps
        App app2 = appBuilder.uri("/events/2").build();
        long appId2 = appRepo.add(app2);
        EndpointHit hit2 = hitBuilder.appId(appId2).build();
        hitRepo.addEndpointHit(hit2);

        hitsQtyByAppId = hitRepo.getUniqueViewsByAppId(start, end.plusSeconds(5), List.of(appId, appId2));
        assertThat(hitsQtyByAppId)
                .isNotNull()
                .hasSize(2);
        assertTrue(hitsQtyByAppId.containsKey(appId2));
        assertEquals(1, hitsQtyByAppId.get(appId2));
    }
}