package ru.practicum.event.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.event.enums.EventState;

import java.time.Instant;

@Data
@Builder
public class Event {
    long id;
    private String annotation;
    private Long categoryId;
    private String description;
    private Instant eventDate;
    private long locationId;
    private boolean paid;
    private int participantLimit;
    private boolean requestModeration;
    private String title;
    private long initiator;
    private Instant createdOn;
    private Instant publishedOn;
    private EventState eventState;
}