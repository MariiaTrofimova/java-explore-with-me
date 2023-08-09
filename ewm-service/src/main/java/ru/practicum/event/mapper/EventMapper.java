package ru.practicum.event.mapper;

import ru.practicum.category.Category;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.client.exception.StatsRequestException;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.Location;
import ru.practicum.user.User;
import ru.practicum.user.mapper.UserMapper;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.practicum.util.DateTime.toInstant;
import static ru.practicum.util.DateTime.toLocalDateTime;

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
                                              ViewStatsDto viewStatsDto) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(category))
                .confirmedRequests(confirmedRequests)
                .createdOn(toLocalDateTime(event.getCreatedOn()))
                .description(event.getDescription())
                .eventDate(toLocalDateTime(event.getEventDate()))
                .initiator(UserMapper.toUserShortDto(user))
                .location(LocationMapper.locationDto(location))
                .paid(event.isPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(toLocalDateTime(event.getPublishedOn()))
                .requestModeration(event.isRequestModeration())
                .title(event.getTitle())
                .views(viewStatsDto.getHits())
                .build();
    }

    public static List<EventFullDto> toEventFullDtoList(List<Event> events,
                                                        List<Category> categories,
                                                        List<User> users,
                                                        List<Location> locations,
                                                        Map<Long, Integer> confirmedRequestsByEventId,
                                                        List<ViewStatsDto> viewStatsDtos) {
        Map<Long, Category> categoriesById = makeCategoryMap(categories);
        Map<Long, User> usersById = makeUsersMap(users);
        Map<Long, ViewStatsDto> viewsByEventId = makeViewStatsMap(viewStatsDtos);
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

    private static long getEventId(ViewStatsDto viewStatsDto) {
        String uri = viewStatsDto.getUri();
        try {
            return Long.parseLong(String.valueOf(uri.charAt(uri.length() - 1)));
        } catch (NumberFormatException e) {
            throw new StatsRequestException("Ошибка запроса данных статистики для событий");
        }

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
        Map<Long, ViewStatsDto> viewsByEventId = makeViewStatsMap(viewStatsDtos);

        return events.stream().map(event -> {
            long eventId = event.getId();
            return toEventShortDto(event,
                    categoriesById.get(event.getCategoryId()),
                    confirmedRequestsByEventId.get(eventId),
                    usersById.get(event.getInitiator()),
                    viewsByEventId.get(eventId).getHits()
            );
        }).collect(Collectors.toList());
    }

    private static Map<Long, ViewStatsDto> makeViewStatsMap(List<ViewStatsDto> viewStatsDtos) {
        return viewStatsDtos.stream()
                .collect(Collectors.toMap(EventMapper::getEventId, Function.identity()));
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