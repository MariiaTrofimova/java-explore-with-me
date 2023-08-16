package ru.practicum.location.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.location.dto.SearchAreaDto;

@Data
@Builder
public class LocationCriteria {
    private SearchAreaDto searchArea;
    private String type;
    private int from;
    private int size;
}
