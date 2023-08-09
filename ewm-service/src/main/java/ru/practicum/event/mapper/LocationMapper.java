package ru.practicum.event.mapper;

import ru.practicum.event.dto.LocationDto;
import ru.practicum.event.model.Location;

public class LocationMapper {

    public static Location toLocation(LocationDto locationDto) {
        return Location.builder()
                .lat(locationDto.getLat())
                .lon(locationDto.getLon())
                .build();
    }

    public static LocationDto locationDto(Location location) {
        return LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }
}
