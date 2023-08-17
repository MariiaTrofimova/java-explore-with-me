package ru.practicum.event.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.event.enums.EventSort;
import ru.practicum.location.model.Location;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class Criteria {
    private Boolean published;
    private String text;
    private List<Long> users;
    private List<String> states;
    private List<Long> categories;
    private Boolean paid;
    private Instant start;
    private Instant end;
    private EventSort sort;
    private Location location;
    private int from;
    private int size;


}