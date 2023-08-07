package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatsClient;
import ru.practicum.client.exception.StatsRequestException;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventDto;
import ru.practicum.event.enums.EventSort;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.repository.RequestRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private static final String APP = "ewm-main-service";
    private static final String URI = "/events/";
    private static final LocalDateTime NOW = LocalDateTime.now();

    private final EventRepository repository;
    private final RequestRepository requestRepo;
    private final StatsClient client;

    @Override
    public List<EventFullDto> getAllByFiltersByAdmin(List<Long> users, List<EventState> states, List<Long> categories, Instant start, Instant end, Integer from, Integer size) {
        return null;
    }

    @Override
    @Transactional
    public EventFullDto patchByAdmin(long eventId, UpdateEventDto updateEventDto) {
        return null;
    }

    @Override
    public List<EventFullDto> getByFiltersPublic(String text, List<Long> categories, boolean paid, Instant start, Optional<Instant> endOptional, boolean onlyAvailable, EventSort sort, Integer from, Integer size) {
        return null;
    }

    @Override
    public EventFullDto getByIdPublic(long id, String ip) {

        addEndHitPoint(id, ip);
        return null;
    }

    @Override
    public List<EventFullDto> getByUserId(long userId, Integer from, Integer size) {
        return null;
    }

    @Override
    public EventFullDto getUsersEventById(long userId, long eventId) {
        return null;
    }

    @Override
    public List<ParticipationRequestDto> getRequestsForUsersEvent(long userId, long eventId) {
        return null;
    }

    @Override
    @Transactional
    public EventFullDto add(long userId, NewEventDto newEventDto) {
        return null;
    }

    @Override
    @Transactional
    public EventFullDto update(long userId, long eventId, UpdateEventDto updateEventDto) {
        return null;
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequests(long userId, long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        return null;
    }

    private void addEndHitPoint(long id, String ip) {
        EndpointHitDto hitDto = EndpointHitDto.builder()
                .app(APP)
                .uri(URI + id)
                .ip(ip)
                .timestamp(NOW)
                .build();
        try {
            client.addEndPointHit(hitDto);
        } catch (StatsRequestException e) {
            throw new StatsRequestException(
                    String.format("Ошибка добавления просмотра события с id %d пользователем %s:", id, ip)
                            + e.getMessage());
        }
    }
}
