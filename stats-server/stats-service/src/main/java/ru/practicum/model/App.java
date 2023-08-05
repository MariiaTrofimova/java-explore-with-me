package ru.practicum.model;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class App {
    private long id;
    private String name;
    private String uri;

    public Map<String, Object> toMap() {
        return Map.of(
                "name", name,
                "uri", uri
        );
    }
}
