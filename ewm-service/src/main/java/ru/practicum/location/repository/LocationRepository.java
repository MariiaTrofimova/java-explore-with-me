package ru.practicum.location.repository;

import ru.practicum.location.model.Location;
import ru.practicum.location.model.LocationCriteria;

import java.util.List;

public interface LocationRepository {
    List<Location> findByIds(List<Long> locationIds);

    Location findById(long locationId);

    List<Location> findNearestByLatAndLon(Location location);

    long add(Location location);

    long addByUser(Location location);

    List<Location> getByCriteria(LocationCriteria criteria);

    void delete(long locId);

    Location update(Location location);
}
