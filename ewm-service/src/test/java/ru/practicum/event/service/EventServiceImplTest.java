package ru.practicum.event.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.category.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.model.Criteria;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.location.model.Location;
import ru.practicum.location.repository.LocationRepository;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.User;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.util.StatisticRequestService;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {
    private static final int FROM = 0;
    private static final int SIZE = 10;

    @Mock
    private LocationRepository locationRepo;
    @Mock
    private CategoryRepository categoryRepo;
    @Mock
    private UserRepository userRepo;
    @Mock
    private RequestRepository requestRepo;
    @Mock
    private EventRepository repository;
    @Mock
    private StatisticRequestService statsRequestService;
    @InjectMocks
    private EventServiceImpl service;
    private Event event;
    private Location location;
    private Category category;
    private User user;
    private ViewStatsDto viewStatsDto;
    private Criteria.CriteriaBuilder criteriaBuilder;

    @BeforeEach
    void setup() {
        criteriaBuilder = Criteria.builder()
                .from(FROM)
                .size(SIZE);

        event = Event.builder()
                .eventState(EventState.PUBLISHED)
                .paid(false)
                .eventDate(Instant.now())
                .createdOn(Instant.now().minusSeconds(5))
                .initiator(1L)
                .title("title")
                .id(1L)
                .requestModeration(false)
                .participantLimit(0)
                .locationId(1L)
                .description("description")
                .categoryId(1L)
                .annotation("annotation")
                .build();

        location = Location.builder()
                .id(1L)
                .build();
        category = Category.builder()
                .id(1L)
                .build();
        user = User.builder()
                .id(1L)
                .build();
        viewStatsDto = ViewStatsDto.builder()
                .app("app")
                .uri("/events/1")
                .hits(1)
                .build();

    }

    @Test
    void shouldGetAllByCriteriaByAdmin() {
        //EmptyList | Null parameters
        when(repository.getByCriteria(criteriaBuilder.build())).thenReturn(Collections.emptyList());
        List<EventFullDto> eventFullDtos = service.getAllByCriteriaByAdmin(null, null, null, null, null,
                null, null, null, FROM, SIZE);
        assertNotNull(eventFullDtos);
        assertEquals(0, eventFullDtos.size());

        //SingleList | Null parameters
        when(repository.getByCriteria(any())).thenReturn(List.of(event));
        when(locationRepo.findByIds(anyList())).thenReturn(List.of(location));
        when(categoryRepo.findByIds(anyList())).thenReturn(List.of(category));
        when(userRepo.findByIds(anyList())).thenReturn(List.of(user));
        when(requestRepo.countConfirmedRequestsByEventIds(anyList())).thenReturn(Map.of(1L, 1));
        when(statsRequestService.makeStatRequest(anyList())).thenReturn(List.of(viewStatsDto));
        eventFullDtos = service.getAllByCriteriaByAdmin(null, null, null, null, null,
                null, null, null, FROM, SIZE);
        assertNotNull(eventFullDtos);
        assertEquals(1, eventFullDtos.size());
    }

    @Test
    void shouldFailGetAllByCriteriaByAdmin() {
        //Failed search by absence of some searchArea fields
        String message = "Область поиска должна быть задана тремя параметрами: lat, lon, radius";
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.getAllByCriteriaByAdmin(null, null, null, null, null,
                        1f, null, null, FROM, SIZE)
        );
        assertEquals(message, exception.getMessage());
    }
}