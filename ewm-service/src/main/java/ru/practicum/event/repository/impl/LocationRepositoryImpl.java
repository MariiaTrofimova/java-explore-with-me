package ru.practicum.event.repository.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.Location;
import ru.practicum.event.repository.LocationRepository;

import java.util.List;

@Repository
@Slf4j
public class LocationRepositoryImpl implements LocationRepository {
    @Override
    public List<Location> findByIds(List<Long> locationIds) {
        return null;
    }

    @Override
    public Location findById(long locationId) {
        return null;
    }

    @Override
    public List<Location> findByLatAndLon(Location location) {
        return null;
    }

    @Override
    public long add(Location location) {
        return 0;
    }
}
