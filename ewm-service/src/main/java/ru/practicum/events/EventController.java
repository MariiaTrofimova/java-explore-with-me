package ru.practicum.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.client.StatsClient;
import ru.practicum.client.exception.StatsRequestException;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
@Slf4j
public class EventController {
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final StatsClient client;

    @GetMapping("{id}")
    public String testClientAddHit(@PathVariable long id,
                                   HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String uri = request.getRequestURI();
        String app = "ewm-main-service";
        String timestamp = LocalDateTime.now().format(formatter);

        EndpointHitDto hitDto = EndpointHitDto.builder()
                .app(app)
                .uri(uri)
                .ip(ip)
                .timestamp(timestamp)
                .build();
        try {
            client.addEndPointHit(hitDto);
        } catch (StatsRequestException e) {
            throw new StatsRequestException(
                    String.format("Ошибка добавления просмотра страницы %s пользователем %s:", uri, ip)
                            + e.getMessage());
        }

        return String.format("Добавлен просмотр страницы %s пользователем %s", uri, ip);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> testClientGetStat() {
        LocalDateTime start = LocalDateTime.now().minusHours(1);
        LocalDateTime end = LocalDateTime.now();
        try {
            return client.getStatistics(start, end);
        } catch (StatsRequestException e) {
            throw new StatsRequestException(e.getMessage());
        }
    }

    @GetMapping("/stats-unique")
    public List<ViewStatsDto> testClientGetStatUnique() {
        LocalDateTime start = LocalDateTime.now().minusHours(1);
        LocalDateTime end = LocalDateTime.now();
        try {
            return client.getStatistics(start, end, true);
        } catch (StatsRequestException e) {
            throw new StatsRequestException(e.getMessage());
        }
    }

    @GetMapping("/stats-uris")
    public List<ViewStatsDto> testClientGetStatWithUris() {
        LocalDateTime start = LocalDateTime.now().minusHours(1);
        LocalDateTime end = LocalDateTime.now();
        List<String> uris = List.of("/events/1");
        try {
            return client.getStatistics(start, end, uris);
        } catch (StatsRequestException e) {
            throw new StatsRequestException(e.getMessage());
        }
    }

    @GetMapping("/stats-uris-unique")
    public List<ViewStatsDto> testClientGetStatWithUrisUnique() {
        LocalDateTime start = LocalDateTime.now().minusHours(1);
        LocalDateTime end = LocalDateTime.now();
        List<String> uris = List.of("/events/1", "/events/2");
        try {
            return client.getStatistics(start, end, uris, true);
        } catch (StatsRequestException e) {
            throw new StatsRequestException(e.getMessage());
        }
    }
}