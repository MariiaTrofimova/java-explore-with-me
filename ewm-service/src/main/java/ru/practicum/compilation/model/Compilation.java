package ru.practicum.compilation.model;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

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
}