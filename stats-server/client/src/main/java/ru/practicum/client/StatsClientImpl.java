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
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.dto.ViewStatsListDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.PastOrPresent;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.practicum.validation.validation.validateDates;
import static ru.practicum.validation.validation.validateEndPointHitDto;

@Service
@Validated
public class StatsClientImpl implements StatsClient {
    private static final String PATH_HIT = "/hit";
    private static final String PATH_STATS = "/stats";
    private static final String SERVER_URL = "${STATS_SERVER_URL}";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final RestTemplate rest;

    public StatsClientImpl() {
        rest = new RestTemplateBuilder()
                .uriTemplateHandler(new DefaultUriBuilderFactory(SERVER_URL))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    public void addEndPointHit(@Valid EndpointHitDto endpointHitDto) {
        validateEndPointHitDto(endpointHitDto);
        HttpEntity<EndpointHitDto> request = new HttpEntity<>(endpointHitDto);
        ResponseEntity<Void> response = rest.exchange(PATH_HIT, HttpMethod.POST,
                request, Void.class);
        HttpStatus code = response.getStatusCode();
        if (code != HttpStatus.CREATED) {
            throw new RuntimeException("Ошибка при сохранении данных");
        }
    }

    public List<ViewStatsDto> getStatistics(@NotNull(message = "Отсутствует дата начала диапазона")
                                            @Past(message = "Дата начала диапазона должна быть в прошлом")
                                            LocalDateTime start,
                                            @NotNull(message = "Дата конца диапазона не может быть пустой")
                                            @PastOrPresent(message = "Дата конца диапазона не может быть в будущем")
                                            LocalDateTime end,
                                            @NotNull(message = "Отсутствует список uri") String[] uris,
                                            @NotNull(message = "Отсутствует флаг уникальности ip") Boolean unique) {

        Map<String, Object> parameters = addDateParameters(start, end);
        parameters.put("uris", uris);
        parameters.put("unique", unique);
        return sendGetRequest(parameters);
    }

    public List<ViewStatsDto> getStatistics(@NotNull(message = "Отсутствует дата начала диапазона")
                                            @Past(message = "Дата начала диапазона должна быть в прошлом")
                                            LocalDateTime start,
                                            @NotNull(message = "Дата конца диапазона не может быть пустой")
                                            @PastOrPresent(message = "Дата конца диапазона не может быть в будущем")
                                            LocalDateTime end) {
        Map<String, Object> parameters = addDateParameters(start, end);
        return sendGetRequest(parameters);
    }

    public List<ViewStatsDto> getStatistics(@NotNull(message = "Отсутствует дата начала диапазона")
                                            @Past(message = "Дата начала диапазона должна быть в прошлом")
                                            LocalDateTime start,
                                            @NotNull(message = "Дата конца диапазона не может быть пустой")
                                            @PastOrPresent(message = "Дата конца диапазона не может быть в будущем")
                                            LocalDateTime end,
                                            @NotNull(message = "Список uri не может быть пустым") String[] uris) {
        Map<String, Object> parameters = addDateParameters(start, end);
        parameters.put("uris", uris);
        return sendGetRequest(parameters);
    }

    public List<ViewStatsDto> getStatistics(@NotNull(message = "Отсутствует дата начала диапазона")
                                            @Past(message = "Дата начала диапазона должна быть в прошлом")
                                            LocalDateTime start,
                                            @NotNull(message = "Дата конца диапазона не может быть пустой")
                                            @PastOrPresent(message = "Дата конца диапазона не может быть в будущем")
                                            LocalDateTime end,
                                            @NotNull(message = "Отсутствует флаг уникальности ip") Boolean unique) {
        Map<String, Object> parameters = addDateParameters(start, end);
        parameters.put("unique", unique);
        return sendGetRequest(parameters);
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
        return URLEncoder.encode(dateTime.format(formatter), StandardCharsets.UTF_8);
    }

    private List<ViewStatsDto> sendGetRequest(Map<String, Object> parameters) {
        ViewStatsListDto response = rest.getForObject(PATH_STATS, ViewStatsListDto.class, parameters);
        if (response == null) {
            throw new RuntimeException("Ошибка при запросе данных");
        }
        return response.getViewStatsDtoList();
    }
}