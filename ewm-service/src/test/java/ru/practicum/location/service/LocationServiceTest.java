package ru.practicum.location.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.error.exceptions.ConflictException;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.location.dto.LocationFullDto;
import ru.practicum.location.dto.NewLocationDto;
import ru.practicum.location.model.Location;
import ru.practicum.location.model.LocationCriteria;
import ru.practicum.location.repository.LocationRepository;

import javax.validation.ValidationException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.practicum.location.enums.LocationType.OUTDOOR;

@ExtendWith(MockitoExtension.class)
class LocationServiceTest {

    @Mock
    private LocationRepository repository;

    @Mock
    private EventRepository eventRepo;

    @InjectMocks
    private LocationServiceImpl service;

    private LocationFullDto locationFullDto;
    private Location location;
    private LocationFullDto.LocationFullDtoBuilder dtoBuilder;
    private NewLocationDto.NewLocationDtoBuilder newDtoBuilder;
    private Location.LocationBuilder builder;

    @BeforeEach
    void setup() {
        dtoBuilder = LocationFullDto.builder()
                .id(1L)
                .lat(0f)
                .lon(0f)
                .radius(1)
                .type(OUTDOOR.toString())
                .name("park");

        newDtoBuilder = NewLocationDto.builder()
                .lat(0f)
                .lon(0f)
                .radius(1)
                .type(OUTDOOR.toString())
                .name("park");

        builder = Location.builder()
                .id(1L)
                .lat(0f)
                .lon(0f)
                .radius(1)
                .type(OUTDOOR)
                .name("park");
    }

    @Test
    void shouldAdd() {
        location = builder.build();
        NewLocationDto newLocationDto = newDtoBuilder.build();

        when(repository.add(any(Location.class))).thenReturn(location.getId());
        locationFullDto = service.add(newLocationDto);

        assertNotNull(locationFullDto);
        assertEquals(location.getName(), locationFullDto.getName());
        verify(repository, times(1)).add(any(Location.class));
    }

    @Test
    void shouldPatch() {
        long locId = 1L;
        location = builder.build();

        //Null parameters
        LocationFullDto dtoToUpdate = LocationFullDto.builder().build();
        when(repository.findById(anyLong())).thenReturn(location);
        when(repository.update(any(Location.class))).thenReturn(location);
        locationFullDto = service.patch(locId, dtoToUpdate);
        assertNotNull(locationFullDto);

        //Regular Case
        locationFullDto = dtoBuilder.build();
        locationFullDto = service.patch(locId, locationFullDto);
        assertNotNull(locationFullDto);

        //Fail Name
        LocationFullDto failedNameDtoToUpdate = LocationFullDto.builder()
                .name("")
                .build();
        String fieldName = "Название локации";
        String message = String.format("Поле %s не может быть пустым", fieldName);
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.patch(locId, failedNameDtoToUpdate));
        assertEquals(message, exception.getMessage());

        //Fail Type
        LocationFullDto failedTypeDtoToUpdate = LocationFullDto.builder()
                .type("asdf")
                .build();
        message = String.format("Unknown type: %s", failedTypeDtoToUpdate.getType());
        IllegalArgumentException typeException = assertThrows(
                IllegalArgumentException.class,
                () -> service.patch(locId, failedTypeDtoToUpdate));
        assertEquals(message, typeException.getMessage());
    }

    @Test
    void shouldDelete() {
        //Fail by events existence
        long locId = 1L;
        int eventsQty = 1;
        when(eventRepo.countEventsByLocationId(anyLong())).thenReturn(1L);
        String message = String.format("С локацией с id %d связано %d событий", locId, eventsQty);
        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> service.delete(locId));
        assertEquals(message, exception.getMessage());

        //Regular Case
        doNothing().when(repository).delete(anyLong());
        when(eventRepo.countEventsByLocationId(anyLong())).thenReturn(0L);
        service.delete(locId);
    }

    @Test
    void shouldGetAllByLocationCriteria() {
        //Empty List | Null fields
        Float lat = null;
        Float lon = null;
        Integer radius = null;
        String type = null;
        int from = 0;
        int size = 10;
        when(repository.getByCriteria(any(LocationCriteria.class))).thenReturn(Collections.emptyList());
        List<LocationFullDto> locationFullDtos = service.getAllByLocationCriteria(lat, lon, radius, type, from, size);
        assertNotNull(locationFullDtos);
        assertEquals(0, locationFullDtos.size());

        //Failed search by absence of some searchArea fields
        String message = "Область поиска должна быть задана тремя параметрами: lat, lon, radius";
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.getAllByLocationCriteria(1f, lon, radius, type, from, size)
        );
        assertEquals(message, exception.getMessage());

        assertThrows(
                IllegalArgumentException.class,
                () -> service.getAllByLocationCriteria(lat, 1f, radius, type, from, size)
        );
        assertEquals(message, exception.getMessage());

        assertThrows(
                IllegalArgumentException.class,
                () -> service.getAllByLocationCriteria(lat, lon, 1, type, from, size)
        );
        assertEquals(message, exception.getMessage());

        //Regular Case | Single List
        Float lat1 = 0f;
        Float lon1 = 0f;
        Integer radius1 = 1;
        String type1 = OUTDOOR.toString();
        location = builder.build();
        when(repository.getByCriteria(any(LocationCriteria.class))).thenReturn(List.of(location));
        locationFullDtos = service.getAllByLocationCriteria(lat1, lon1, radius1, type1, from, size);
        assertNotNull(locationFullDtos);
        assertEquals(1, locationFullDtos.size());

    }
}