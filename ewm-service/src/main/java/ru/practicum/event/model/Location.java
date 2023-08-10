package ru.practicum.event.model;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class Location {
    private long id;
    private float lat;
    private float lon;

    public Map<String, Object> toMap() {
        return Map.of(
                "lat", lat,
                "lon", lon
        );
    }
}
