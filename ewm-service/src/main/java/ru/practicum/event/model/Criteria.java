package ru.practicum.event.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.event.enums.EventSort;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class Criteria {
    private String text;
    private List<Long> categories;
    private Boolean paid;
    private Instant start;
    private Instant end;
    private boolean onlyAvailable;
    private EventSort sort;
    private int from;
    private int size;
}