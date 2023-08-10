package ru.practicum.compilation.model;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class Compilation {
    private long id;
    private String title;
    private boolean pinned;

    private final List<Long> events = new ArrayList<>();

    public void addEvents(List<Long> eventsToAdd) {
        events.addAll(eventsToAdd);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("pinned", pinned);
        return map;
    }
}