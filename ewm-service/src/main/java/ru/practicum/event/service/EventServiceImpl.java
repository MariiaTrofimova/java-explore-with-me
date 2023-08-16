package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.client.StatsClient;
import ru.practicum.client.exception.StatsRequestException;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.error.exceptions.ConflictException;
import ru.practicum.error.exceptions.NotFoundException;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventDto;
import ru.practicum.event.enums.EventSort;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.enums.StateActionAdmin;
import ru.practicum.event.enums.StateActionPrivate;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Criteria;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.location.mapper.LocationMapper;
import ru.practicum.location.model.Location;
import ru.practicum.location.repository.LocationRepository;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.User;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.util.StatisticRequestService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.event.enums.EventState.PUBLISHED;
import static ru.practicum.util.DateTime.toInstant;
import static ru.practicum.util.Statistics.getEventId;
import static ru.practicum.util.Validation.*;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private static final String APP = "ewm-main-service";
    private static final String URI = "/events";

    private final EventRepository repository;
    private final CategoryRepository categoryRepo;
    private final RequestRepository requestRepo;
    private final UserRepository userRepo;
    private final LocationRepository locationRepo;
    private final StatsClient client;
    private final StatisticRequestService statsRequestService;

    @Override
    public List<EventFullDto> getAllByCriteriaByAdmin(List<Long> users,
                                                      List<String> states,
                                                      List<Long> categoryIds,
                                                      Instant start,
                                                      Instant end,
                                                      int from,
                                                      int size) {
        Criteria criteria = Criteria.builder()
                .users(users)
                .states(states)
                .categories(categoryIds)
                .start(start)
                .end(end)
                .from(from)
                .size(size)
                .build();
        List<Event> events = repository.getByCriteria(criteria);
        return makeFullResponseDtoList(events);
    }

    @Override
    @Transactional
    public EventFullDto patchByAdmin(long eventId, UpdateEventDto updateEventDto) {
        Event event = repository.findById(eventId);
        updateNotNullFields(event, updateEventDto);
        String stateAction = updateEventDto.getStateAction();
        if (stateAction != null) {
            if (event.getEventState() != EventState.PENDING) {
                log.warn("Событие можно опубликовать или отклонить только в состоянии PENDING");
                throw new ConflictException("Событие можно опубликовать или отклонить только в состоянии PENDING");
            }
            setEventStateByAdminAction(event, stateAction);
        }
        event = repository.update(event);
        return makeFullResponseDto(event);
    }

    @Override
    public List<EventFullDto> getByFiltersPublic(String text,
                                                 List<Long> categoryIds,
                                                 Boolean paid,
                                                 Instant start,
                                                 Instant end,
                                                 boolean onlyAvailable,
                                                 EventSort sort,
                                                 int from,
                                                 int size,
                                                 String ip) {

        if (text != null) {
            text = text.toLowerCase();
        }

        Criteria criteria = Criteria.builder()
                .text(text)
                .categories(categoryIds)
                .paid(paid)
                .start(start)
                .end(end)
                .sort(sort)
                .from(from)
                .size(size)
                .published(true)
                .build();
        List<Event> events = repository.getByCriteria(criteria);
        addEndHitPoint(URI, ip);
        if (onlyAvailable) {
            List<Long> eventIds = events.stream().map(Event::getId).collect(Collectors.toList());
            Map<Long, Integer> confirmedRequestsQty = requestRepo.countConfirmedRequestsByEventIds(eventIds);
            events = events.stream()
                    .filter(event -> confirmedRequestsQty.get(event.getId()) < event.getParticipantLimit())
                    .collect(Collectors.toList());
        }
        events.forEach(event -> addEndHitPoint(URI + "/" + event.getId(), ip));
        return makeFullResponseDtoList(events);
    }

    @Override
    public EventFullDto getByIdPublic(long id, String ip) {
        Event event = repository.findById(id);
        if (event.getEventState() != PUBLISHED) {
            log.warn("Попытка просмотра неопубликованного события с id {} пользователем с ip {}",
                    id, ip);
            throw new NotFoundException(String.format("Событие с id %d еще не опубликовано", id));
        }
        addEndHitPoint(URI + "/" + id, ip);
        return makeFullResponseDto(event);
    }


    @Override
    public List<EventFullDto> getByUserId(long userId, int from, int size) {
        List<Event> events = repository.findByInitiatorId(userId, from, size);
        return makeFullResponseDtoList(events);
    }

    @Override
    public EventFullDto getUsersEventById(long userId, long eventId) {
        Event event = repository.findById(eventId);
        checkInitiator(event, userId);
        return makeFullResponseDto(event);
    }

    @Override
    @Transactional
    public EventFullDto add(long userId, NewEventDto newEventDto) {
        if (newEventDto.getRequestModeration() == null) {
            newEventDto.setRequestModeration(true);
        }
        Location location = LocationMapper.toLocation(newEventDto.getLocation());
        long locationId = setIdToLocation(location);
        Event event = EventMapper.toEvent(newEventDto, locationId, userId);
        event = repository.add(event);
        return makeFullResponseDto(event, location);
    }

    private long setIdToLocation(Location location) {
        List<Location> locations = locationRepo.findNearestByLatAndLon(location);
        long locationId;
        if (locations.isEmpty()) {
            locationId = locationRepo.addByUser(location);
        } else {
            locationId = locations.get(0).getId();
        }
        location.setId(locationId);
        return locationId;
    }

    @Override
    @Transactional
    public EventFullDto update(long userId, long eventId, UpdateEventDto updateEventDto) {
        Event event = repository.findById(eventId);
        if (event.getEventState() == PUBLISHED) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }
        checkInitiator(event, userId);
        updateNotNullFields(event, updateEventDto);
        String stateAction = updateEventDto.getStateAction();
        if (stateAction != null) {
            setEventStateByPrivateAction(event, stateAction);
        }
        event = repository.update(event);
        return makeFullResponseDto(event);
    }

    private void setEventStateByPrivateAction(Event event, String stateAction) {
        StateActionPrivate action = StateActionPrivate.from(stateAction)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateAction));
        if (action == StateActionPrivate.SEND_TO_REVIEW) {
            event.setEventState(EventState.PENDING);
        } else {
            event.setEventState(EventState.CANCELED);
        }
    }

    private void setEventStateByAdminAction(Event event, String stateAction) {
        StateActionAdmin action = StateActionAdmin.from(stateAction)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateAction));
        if (action == StateActionAdmin.PUBLISH_EVENT) {
            event.setEventState(PUBLISHED);
            event.setPublishedOn(Instant.now());
        } else {
            event.setEventState(EventState.CANCELED);
        }
    }

    private void updateNotNullFields(Event event, UpdateEventDto updateEventDto) {
        String annotationToUpdate = updateEventDto.getAnnotation();
        Long categoryToUpdate = updateEventDto.getCategory();
        String descriptionToUpdate = updateEventDto.getDescription();
        LocalDateTime eventDateToUpdate = updateEventDto.getEventDate();
        LocationDto locationToUpdate = updateEventDto.getLocation();
        Boolean paidToUpdate = updateEventDto.getPaid();
        Integer participantLimitToUpdate = updateEventDto.getParticipantLimit();
        Boolean requestModerationToUpdate = updateEventDto.getRequestModeration();
        String titleToUpdate = updateEventDto.getTitle();

        if (annotationToUpdate != null) {
            validateStringField(annotationToUpdate, "аннотация", 20, 2000);
            event.setAnnotation(annotationToUpdate);
        }
        if (categoryToUpdate != null) {
            validatePositive(categoryToUpdate, "id категории");
            event.setCategoryId(categoryToUpdate);
        }
        if (descriptionToUpdate != null) {
            validateStringField(descriptionToUpdate, "описание", 20, 7000);
            event.setDescription(descriptionToUpdate);
        }
        if (eventDateToUpdate != null) {
            validateEventDate(eventDateToUpdate);
            event.setEventDate(toInstant(eventDateToUpdate));
        }
        if (locationToUpdate != null) {
            Location location = LocationMapper.toLocation(locationToUpdate);
            long locationId = setIdToLocation(location);
            event.setLocationId(locationId);
        }
        if (paidToUpdate != null) {
            event.setPaid(paidToUpdate);
        }
        if (participantLimitToUpdate != null) {
            validatePositive(participantLimitToUpdate, "максимальное количество участников");
            event.setParticipantLimit(participantLimitToUpdate);
        }
        if (requestModerationToUpdate != null) {
            event.setRequestModeration(requestModerationToUpdate);
        }
        if (titleToUpdate != null) {
            validateStringField(titleToUpdate, "заголовок", 3, 120);
            event.setTitle(titleToUpdate);
        }
    }

    private EventFullDto makeFullResponseDto(Event event) {
        Location location = locationRepo.findById(event.getLocationId());
        return makeFullResponseDto(event, location);
    }

    private EventFullDto makeFullResponseDto(Event event, Location location) {
        User user = userRepo.findById(event.getInitiator());
        Category category = categoryRepo.findById(event.getCategoryId());
        int confirmedRequests = requestRepo.countConfirmedRequestsByEventId(event.getId());
        int views = 0;
        if (event.getEventState() == PUBLISHED) {
            List<ViewStatsDto> viewStatsDto = statsRequestService.makeStatRequest(List.of(event));
            if (!viewStatsDto.isEmpty()) {
                long eventId = getEventId(viewStatsDto.get(0));
                if (event.getId() != eventId) {
                    throw new StatsRequestException(
                            String.format("Ошибка запроса статистики: запрошенный id %d не соответствует возвращенному %d",
                                    event.getId(), eventId)
                    );
                }
            }
            views = viewStatsDto.isEmpty() ? 0 : (int) viewStatsDto.get(0).getHits();
        }

        return EventMapper.toEventFullDto(event, category, user, location, confirmedRequests, views);
    }

    private List<EventFullDto> makeFullResponseDtoList(List<Event> events) {
        if (events.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> eventIds = events.stream().map(Event::getId).collect(Collectors.toList());

        List<Category> categories = findCategoriesByEvents(events);
        List<User> users = findUsersByEvents(events);
        List<Location> locations = findLocationsByEvents(events);
        Map<Long, Integer> confirmedRequestsByEventId = requestRepo.countConfirmedRequestsByEventIds(eventIds);

        List<ViewStatsDto> viewStatsDtos = statsRequestService.makeStatRequest(events);

        return EventMapper.toEventFullDtoList(events, categories, users, locations,
                confirmedRequestsByEventId, viewStatsDtos);
    }

    private List<Location> findLocationsByEvents(List<Event> events) {
        List<Long> locationIds = events.stream().map(Event::getLocationId).distinct().collect(Collectors.toList());
        return locationRepo.findByIds(locationIds);
    }

    private List<User> findUsersByEvents(List<Event> events) {
        List<Long> userIds = events.stream().map(Event::getInitiator).distinct().collect(Collectors.toList());
        return userRepo.findByIds(userIds);
    }

    private List<Category> findCategoriesByEvents(List<Event> events) {
        List<Long> categoryIds = events.stream().map(Event::getCategoryId).distinct().collect(Collectors.toList());
        return categoryRepo.findByIds(categoryIds);
    }

    private void addEndHitPoint(String uri, String ip) {
        EndpointHitDto hitDto = EndpointHitDto.builder()
                .app(APP)
                .uri(uri)
                .ip(ip)
                .timestamp(LocalDateTime.now())
                .build();
        try {
            client.addEndPointHit(hitDto);
        } catch (StatsRequestException e) {
            throw new StatsRequestException(
                    String.format("Ошибка добавления просмотра страницы %s пользователем %s: ", uri, ip)
                            + e.getMessage());
        }
    }

    private void checkInitiator(Event event, long userId) {
        if (event.getInitiator() != userId) {
            throw new NotFoundException(
                    String.format("Пользователь с id %d не организатор события с id %d", userId, event.getId()));
        }
    }
}