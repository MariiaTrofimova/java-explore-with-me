package ru.practicum.event.mapper;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.category.Category;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.model.Event;
import ru.practicum.location.mapper.LocationMapper;
import ru.practicum.location.model.Location;
import ru.practicum.user.User;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.util.Statistics;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.practicum.util.DateTime.toInstant;
import static ru.practicum.util.DateTime.toLocalDateTime;

@Slf4j
public class EventMapper {

    public static Event toEvent(NewEventDto newEventDto, long locationId, long userId) {
        return Event.builder()
                .annotation(newEventDto.getAnnotation())
                .categoryId(newEventDto.getCategory())
                .description(newEventDto.getDescription())
                .eventDate(toInstant(newEventDto.getEventDate()))
                .locationId(locationId)
                .paid(newEventDto.isPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.getRequestModeration())
                .title(newEventDto.getTitle())
                .initiator(userId)
                .eventState(EventState.PENDING)
                .build();
    }

    public static EventFullDto toEventFullDto(Event event,
                                              Category category,
                                              User user,
                                              Location location,
                                              int confirmedRequests,
                                              int views) {
        EventFullDto eventFullDto = EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(category))
                .confirmedRequests(confirmedRequests)
                .createdOn(toLocalDateTime(event.getCreatedOn()))
                .description(event.getDescription())
                .eventDate(toLocalDateTime(event.getEventDate()))
                .initiator(UserMapper.toUserShortDto(user))
                .location(LocationMapper.toLocationDto(location))
                .paid(event.isPaid())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.isRequestModeration())
                .title(event.getTitle())
                .views(views)
                .state(event.getEventState())
                .build();
        if (event.getPublishedOn() != null) {
            eventFullDto.setPublishedOn(toLocalDateTime(event.getPublishedOn()));
        }
        return eventFullDto;
    }

    public static List<EventFullDto> toEventFullDtoList(List<Event> events,
                                                        List<Category> categories,
                                                        List<User> users,
                                                        List<Location> locations,
                                                        Map<Long, Integer> confirmedRequestsByEventId,
                                                        List<ViewStatsDto> viewStatsDtos) {
        Map<Long, Category> categoriesById = makeCategoryMap(categories);
        Map<Long, User> usersById = makeUsersMap(users);
        Map<Long, Integer> viewsByEventId = makeViewMap(viewStatsDtos, events);
        Map<Long, Location> locationsById = makeLocationsMap(locations);

        return events.stream().map(event -> {
            long eventId = event.getId();
            return toEventFullDto(event,
                    categoriesById.get(event.getCategoryId()),
                    usersById.get(event.getInitiator()),
                    locationsById.get(event.getLocationId()),
                    confirmedRequestsByEventId.get(eventId),
                    viewsByEventId.get(eventId)
            );
        }).collect(Collectors.toList());
    }

    public static EventShortDto toEventShortDto(Event event,
                                                Category category,
                                                int confirmedRequests,
                                                User user,
                                                long views) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(category))
                .confirmedRequests(confirmedRequests)
                .eventDate(toLocalDateTime(event.getEventDate()))
                .initiator(UserMapper.toUserShortDto(user))
                .paid(event.isPaid())
                .title(event.getTitle())
                .views(views)
                .build();
    }

    public static List<EventShortDto> toEventShortDto(List<Event> events,
                                                      List<Category> categories,
                                                      List<User> users,
                                                      Map<Long, Integer> confirmedRequestsByEventId,
                                                      List<ViewStatsDto> viewStatsDtos) {
        Map<Long, Category> categoriesById = makeCategoryMap(categories);
        Map<Long, User> usersById = makeUsersMap(users);
        Map<Long, Integer> viewsByEventId = makeViewMap(viewStatsDtos, events);

        return events.stream().map(event -> {
            long eventId = event.getId();
            return toEventShortDto(event,
                    categoriesById.get(event.getCategoryId()),
                    confirmedRequestsByEventId.get(eventId),
                    usersById.get(event.getInitiator()),
                    viewsByEventId.get(eventId)
            );
        }).collect(Collectors.toList());
    }

    private static Map<Long, Integer> makeViewMap(List<ViewStatsDto> viewStatsDtos, List<Event> events) {
        Map<Long, Integer> viewsByEventId = events.stream()
                .collect(Collectors.toMap(Event::getId, event -> 0));
        if (viewStatsDtos.isEmpty()) {
            return viewsByEventId;
        }
        viewStatsDtos.forEach(viewStatsDto -> viewsByEventId.put(Statistics.getEventId(viewStatsDto), (int) viewStatsDto.getHits()));
        return viewsByEventId;
    }

    private static Map<Long, User> makeUsersMap(List<User> users) {
        return users.stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
    }

    private static Map<Long, Category> makeCategoryMap(List<Category> categories) {
        return categories.stream()
                .collect(Collectors.toMap(Category::getId, Function.identity()));
    }

    private static Map<Long, Location> makeLocationsMap(List<Location> locations) {
        return locations.stream()
                .collect(Collectors.toMap(Location::getId, Function.identity()));
    }
}