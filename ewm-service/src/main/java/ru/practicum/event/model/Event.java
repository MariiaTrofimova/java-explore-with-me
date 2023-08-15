package ru.practicum.event.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.event.enums.EventState;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class Event {
    long id;
    private String annotation;
    private long categoryId;
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

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("annotation", annotation);
        map.put("category_id", categoryId);
        map.put("description", description);
        map.put("event_date", Timestamp.from(eventDate));
        map.put("location_id", locationId);
        map.put("paid", paid);
        map.put("participant_limit", participantLimit);
        map.put("request_moderation", requestModeration);
        map.put("title", title);
        map.put("initiator", initiator);
        map.put("state", eventState.toString());
        return map;
    }
}