package ru.practicum.request;

import lombok.Builder;
import lombok.Data;
import ru.practicum.request.enums.RequestStatus;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class Request {
    private long id;
    private long eventId;
    private long requesterId;
    private Instant created;
    private RequestStatus status;

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("event_id", eventId);
        map.put("requester_id", requesterId);
        map.put("status", status.toString());
        return map;
    }
}