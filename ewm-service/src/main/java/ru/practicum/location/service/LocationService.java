package ru.practicum.location.service;

import ru.practicum.location.dto.LocationFullDto;
import ru.practicum.location.dto.SearchAreaDto;

import java.util.List;

public interface LocationService {
    LocationFullDto add(LocationFullDto locationFullDto);

    LocationFullDto patch(long locId, LocationFullDto locationFullDto);

    void delete(long locId);

    List<LocationFullDto> getAllByLocationCriteria(SearchAreaDto searchArea, String type,
                                                   int from, int size);

}
