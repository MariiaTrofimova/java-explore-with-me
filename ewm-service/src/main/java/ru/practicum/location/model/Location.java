package ru.practicum.location.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.location.enums.LocationType;

import java.util.Map;

@Data
@Builder
public class Location {
    private long id;
    private float lat;
    private float lon;
    private int radius;
    private String name;
    private LocationType type;

    public Map<String, Object> toMap() {
        return Map.of(
                "lat", lat,
                "lon", lon
        );
    }

    public Map<String, Object> toFullMap() {
        return Map.of(
                "lat", lat,
                "lon", lon,
                "radius", radius,
                "name", name,
                "type", type.toString()
        );
    }
}
