package ru.practicum.location.mapper;

import ru.practicum.location.dto.LocationDto;
import ru.practicum.location.dto.LocationFullDto;
import ru.practicum.location.enums.LocationType;
import ru.practicum.location.model.Location;

import java.util.List;
import java.util.stream.Collectors;

public class LocationMapper {

    public static Location toLocation(LocationDto locationDto) {
        return Location.builder()
                .lat(locationDto.getLat())
                .lon(locationDto.getLon())
                .build();
    }

    public static LocationDto toLocationDto(Location location) {
        return LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }

    public static LocationFullDto toLocationFullDto(Location location) {
        return LocationFullDto.builder()
                .id(location.getId())
                .lat(location.getLat())
                .lon(location.getLon())
                .radius(location.getRadius())
                .name(location.getName())
                .build();
    }

    public static List<LocationFullDto> toLocationFullDto(List<Location> locations) {
        return locations.stream().map(LocationMapper::toLocationFullDto).collect(Collectors.toList());
    }

    public static Location toLocation(LocationFullDto locationFullDto) {
        LocationType type = LocationType.from(locationFullDto.getType())
                .orElseThrow(() -> new IllegalArgumentException("Unknown type: " + locationFullDto.getType()));
        return Location.builder()
                .lat(locationFullDto.getLat())
                .lon(locationFullDto.getLon())
                .radius(locationFullDto.getRadius())
                .name(locationFullDto.getName())
                .type(type)
                .build();
    }
}
