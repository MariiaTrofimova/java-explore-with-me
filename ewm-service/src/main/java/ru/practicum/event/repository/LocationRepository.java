package ru.practicum.event.repository;

import ru.practicum.event.model.Location;

import java.util.List;

public interface LocationRepository {
    List<Location> findByIds(List<Long> locationIds);

    Location findById(long locationId);

    List<Location> findByLatAndLon(Location location);

    long add(Location location);
}
