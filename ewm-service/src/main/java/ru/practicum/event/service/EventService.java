package ru.practicum.event.service;

import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventDto;
import ru.practicum.event.enums.EventSort;
import ru.practicum.event.enums.EventState;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface EventService {
    List<EventFullDto> getAllByFiltersByAdmin(List<Long> users,
                                              List<EventState> states,
                                              List<Long> categories,
                                              Instant start,
                                              Instant end,
                                              Integer from,
                                              Integer size);

    EventFullDto patchByAdmin(long eventId, UpdateEventDto updateEventDto);

    List<EventFullDto> getByFiltersPublic(String text,
                                          List<Long> categories,
                                          boolean paid,
                                          Instant start,
                                          Optional<Instant> endOptional,
                                          boolean onlyAvailable,
                                          EventSort sort,
                                          Integer from,
                                          Integer size);

    EventFullDto getByIdPublic(long id, String ip);

    List<EventFullDto> getByUserId(long userId, Integer from, Integer size);

    EventFullDto getUsersEventById(long userId, long eventId);

    List<ParticipationRequestDto> getRequestsForUsersEvent(long userId, long eventId);

    EventFullDto add(long userId, NewEventDto newEventDto);

    EventFullDto update(long userId, long eventId, UpdateEventDto updateEventDto);

    EventRequestStatusUpdateResult updateRequests(long userId, long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);
}