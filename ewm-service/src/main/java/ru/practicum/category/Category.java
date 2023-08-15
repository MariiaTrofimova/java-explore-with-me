package ru.practicum.category;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class Category {
    private long id;
    private String name;

    public Map<String, Object> toMap() {
        return Map.of(
                "name", name
        );
    }
}