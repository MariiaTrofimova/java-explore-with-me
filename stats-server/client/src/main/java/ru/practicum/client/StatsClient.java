package ru.practicum.client;

import org.springframework.validation.annotation.Validated;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;
import java.util.List;

@Validated
public interface StatsClient {
    List<ViewStatsDto> getStatistics(@NotNull(message = "Отсутствует дата начала диапазона")
                                     @Past(message = "Дата начала диапазона должна быть в прошлом")
                                     LocalDateTime start,
                                     @NotNull(message = "Дата конца диапазона не может быть пустой")
                                     @PastOrPresent(message = "Дата конца диапазона не может быть в будущем")
                                     LocalDateTime end,
                                     @NotNull(message = "Отсутствует список uri")
                                     List<String> uris,
                                     @NotNull(message = "Отсутствует флаг уникальности ip")
                                     Boolean unique);

    List<ViewStatsDto> getStatistics(@NotNull(message = "Отсутствует дата начала диапазона")
                                     @Past(message = "Дата начала диапазона должна быть в прошлом")
                                     LocalDateTime start,
                                     @NotNull(message = "Дата конца диапазона не может быть пустой")
                                     @PastOrPresent(message = "Дата конца диапазона не может быть в будущем")
                                     LocalDateTime end);

    List<ViewStatsDto> getStatistics(@NotNull(message = "Отсутствует дата начала диапазона")
                                     @Past(message = "Дата начала диапазона должна быть в прошлом")
                                     LocalDateTime start,
                                     @NotNull(message = "Дата конца диапазона не может быть пустой")
                                     @PastOrPresent(message = "Дата конца диапазона не может быть в будущем")
                                     LocalDateTime end,
                                     @NotNull(message = "Отсутствует список uri")
                                     List<String> uris);

    List<ViewStatsDto> getStatistics(@NotNull(message = "Отсутствует дата начала диапазона")
                                     @Past(message = "Дата начала диапазона должна быть в прошлом")
                                     LocalDateTime start,
                                     @NotNull(message = "Дата конца диапазона не может быть пустой")
                                     @PastOrPresent(message = "Дата конца диапазона не может быть в будущем")
                                     LocalDateTime end,
                                     @NotNull(message = "Отсутствует флаг уникальности ip")
                                     Boolean unique);

    void addEndPointHit(@Valid EndpointHitDto endpointHitDto);
}
