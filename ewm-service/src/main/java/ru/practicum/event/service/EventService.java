package ru.practicum.event.service;

import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventDto;
import ru.practicum.event.enums.EventSort;

import java.time.Instant;
import java.util.List;

public interface EventService {
    List<EventFullDto> getAllByCriteriaByAdmin(List<Long> users,
                                               List<String> states,
                                               List<Long> categories,
                                               Instant start,
                                               Instant end,
                                               Float lat,
                                               Float lon,
                                               Integer radius,
                                               int from,
                                               int size);

    EventFullDto patchByAdmin(long eventId, UpdateEventDto updateEventDto);

    List<EventFullDto> getByFiltersPublic(String text,
                                          List<Long> categories,
                                          Boolean paid,
                                          Instant start,
                                          Instant end,
                                          boolean onlyAvailable,
                                          EventSort sort,
                                          int from,
                                          int size,
                                          String ip);

    EventFullDto getByIdPublic(long id, String ip);

    List<EventFullDto> getByUserId(long userId, int from, int size);

    EventFullDto getUsersEventById(long userId, long eventId);

    EventFullDto add(long userId, NewEventDto newEventDto);

    EventFullDto update(long userId, long eventId, UpdateEventDto updateEventDto);

}