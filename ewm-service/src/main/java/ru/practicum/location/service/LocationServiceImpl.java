package ru.practicum.location.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.error.exceptions.ConflictException;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.location.dto.LocationFullDto;
import ru.practicum.location.dto.SearchAreaDto;
import ru.practicum.location.enums.LocationType;
import ru.practicum.location.mapper.LocationMapper;
import ru.practicum.location.model.Location;
import ru.practicum.location.model.LocationCriteria;
import ru.practicum.location.repository.LocationRepository;

import java.util.Collections;
import java.util.List;

import static ru.practicum.util.Validation.validateStringField;

@Service
@Slf4j
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {
    private final LocationRepository repository;
    private final EventRepository eventRepo;


    @Override
    public LocationFullDto add(LocationFullDto locationFullDto) {
        Location location = LocationMapper.toLocation(locationFullDto);
        try {
            long id = repository.add(location);
            location.setId(id);
        } catch (RuntimeException e) {
            reportLatLonUniqueConflict(e, location);
        }
        return LocationMapper.toLocationFullDto(location);
    }

    @Override
    public LocationFullDto patch(long locId, LocationFullDto locationFullDto) {
        Location location = repository.findById(locId);
        updateNotNullFields(location, locationFullDto);
        location = repository.update(location);
        return LocationMapper.toLocationFullDto(location);
    }

    private void updateNotNullFields(Location location, LocationFullDto locationFullDto) {
        Float lat = locationFullDto.getLat();
        Float lon = locationFullDto.getLon();
        Integer radius = locationFullDto.getRadius();
        String type = locationFullDto.getType();
        String name = locationFullDto.getName();
        if (lat != null) {
            location.setLat(lat);
        }
        if (lon != null) {
            location.setLon(lon);
        }
        if (radius != null) {
            location.setRadius(radius);
        }
        if (type != null) {
            LocationType newType = LocationType.from(locationFullDto.getType())
                    .orElseThrow(() -> new IllegalArgumentException("Unknown type: " + locationFullDto.getType()));
            location.setType(newType);
        }
        if (name != null) {
            validateStringField(name, "Название локации", 1, 50);
            location.setName(name);
        }
    }

    @Override
    public void delete(long locId) {
        //с локацией не должно быть связано ни одного события.
        long eventsQty = eventRepo.countEventsByLocationId(locId);
        if (eventsQty == 0) {
            repository.delete(locId);
        } else {
            throw new ConflictException(
                    String.format("С локацией с id %d связано %d событий", locId, eventsQty));
        }
    }

    @Override
    public List<LocationFullDto> getAllByLocationCriteria(SearchAreaDto searchArea, String type,
                                                          int from, int size) {
        LocationCriteria criteria = LocationCriteria.builder()
                .searchArea(searchArea)
                .type(type)
                .from(from)
                .size(size)
                .build();
        List<Location> locations = repository.getByCriteria(criteria);
        if (locations.isEmpty()) {
            return Collections.emptyList();
        }
        return LocationMapper.toLocationFullDto(locations);
    }

    private void reportLatLonUniqueConflict(RuntimeException e, Location location) {
        String error = e.getMessage();
        String constraint = "uq_location_lat_lon";
        if (error.contains(constraint)) {
            error = String.format("Локация с широтой %f и долготой %f уже существует",
                    location.getLat(), location.getLon());
            log.warn("Попытка дублирования координат: ({}, {})", location.getLat(), location.getLon());
            throw new ConflictException(error);
        }
        throw new RuntimeException("Ошибка при передаче данных в БД");
    }
}