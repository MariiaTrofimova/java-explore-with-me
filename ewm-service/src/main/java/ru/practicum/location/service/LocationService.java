package ru.practicum.location.service;

import ru.practicum.location.dto.LocationFullDto;
import ru.practicum.location.dto.NewLocationDto;

import java.util.List;

public interface LocationService {
    LocationFullDto add(NewLocationDto newLocationDto);

    LocationFullDto patch(long locId, LocationFullDto locationFullDto);

    void delete(long locId);

    List<LocationFullDto> getAllByLocationCriteria(Float lat, Float lon, Integer radius, String type,
                                                   int from, int size);
}
