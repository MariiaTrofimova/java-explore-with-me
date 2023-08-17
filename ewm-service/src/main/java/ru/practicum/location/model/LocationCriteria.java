package ru.practicum.location.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LocationCriteria {
    private SearchArea searchArea;
    private String type;
    private int from;
    private int size;
}
