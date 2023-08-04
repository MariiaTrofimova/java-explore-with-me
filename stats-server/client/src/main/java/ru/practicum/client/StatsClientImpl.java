package ru.practicum.client;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.client.exception.StatsRequestException;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.practicum.validation.Validation.validateDates;
import static ru.practicum.validation.Validation.validateEndPointHitDto;

@Service
@Validated
public class StatsClientImpl implements StatsClient {
    private static final String PATH_HIT = "/hit";
    private static final String PATH_STATS_WITH_DATE_PARAMS = "/stats?start={start}&end={end}";
    private static final String PARAM_UNIQUE = "&unique={unique}";
    private static final String PARAM_URIS = "&uris={uris}";
    private static final String SERVER_URL = "http://stats-server:9090";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final RestTemplate rest;

    public StatsClientImpl() {
        rest = new RestTemplateBuilder()
                .uriTemplateHandler(new DefaultUriBuilderFactory(SERVER_URL))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    @Override
    public void addEndPointHit(EndpointHitDto endpointHitDto) {
        validateEndPointHitDto(endpointHitDto);
        HttpEntity<EndpointHitDto> request = new HttpEntity<>(endpointHitDto);
        ResponseEntity<Void> response = rest.exchange(PATH_HIT, HttpMethod.POST,
                request, Void.class);
        HttpStatus code = response.getStatusCode();
        if (code != HttpStatus.CREATED) {
            throw new StatsRequestException("Ошибка при сохранении данных: " + response.getBody());
        }
    }

    @Override
    public List<ViewStatsDto> getStatistics(LocalDateTime start,
                                            LocalDateTime end,
                                            List<String> uris,
                                            Boolean unique) {

        Map<String, Object> parameters = addDateParameters(start, end);
        parameters.put("uris", String.join(",", uris));
        parameters.put("unique", unique);
        String url = PATH_STATS_WITH_DATE_PARAMS + PARAM_URIS + PARAM_UNIQUE;
        return sendGetRequest(url, parameters);
    }

    @Override
    public List<ViewStatsDto> getStatistics(LocalDateTime start,
                                            LocalDateTime end) {
        Map<String, Object> parameters = addDateParameters(start, end);
        return sendGetRequest(PATH_STATS_WITH_DATE_PARAMS, parameters);
    }

    @Override
    public List<ViewStatsDto> getStatistics(LocalDateTime start,
                                            LocalDateTime end,
                                            List<String> uris) {
        Map<String, Object> parameters = addDateParameters(start, end);
        parameters.put("uris", String.join(",", uris));
        String url = PATH_STATS_WITH_DATE_PARAMS + PARAM_URIS;
        return sendGetRequest(url, parameters);
    }

    @Override
    public List<ViewStatsDto> getStatistics(LocalDateTime start,
                                            LocalDateTime end,
                                            Boolean unique) {
        Map<String, Object> parameters = addDateParameters(start, end);
        parameters.put("unique", unique);
        String url = PATH_STATS_WITH_DATE_PARAMS + PARAM_UNIQUE;
        return sendGetRequest(url, parameters);
    }

    private Map<String, Object> addDateParameters(LocalDateTime start, LocalDateTime end) {
        validateDates(start, end);
        String startEncoded = encodeDate(start);
        String endEncoded = encodeDate(end);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("start", startEncoded);
        parameters.put("end", endEncoded);
        return parameters;
    }

    private String encodeDate(LocalDateTime dateTime) {
        String dateTimeString = dateTime.format(formatter);
        return URLEncoder.encode(dateTimeString, StandardCharsets.UTF_8);
    }

    private List<ViewStatsDto> sendGetRequest(String url, Map<String, Object> parameters) {
        ViewStatsDto[] response;
        try {
            response = rest.getForObject(url, ViewStatsDto[].class, parameters);
        } catch (RuntimeException e) {
            throw new StatsRequestException("Ошибка при запросе данных статистики: " + e.getMessage());
        }
        if (response == null) {
            throw new StatsRequestException("Ошибка при запросе данных статистики");
        }
        return Arrays.asList(response);
    }
}