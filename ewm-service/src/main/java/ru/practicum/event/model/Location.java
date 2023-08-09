package ru.practicum.event.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Location {
    private long id;
    private float lat;
    private float lon;
}
