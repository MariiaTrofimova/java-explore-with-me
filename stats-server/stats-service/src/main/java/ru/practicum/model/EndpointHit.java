package ru.practicum.model;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
public class EndpointHit {
    private long id;
    private long appId;
    private String ip;
    private Instant timestamp;

    public Map<String,Object> toMap() {
        return Map.of(
                "app_id", appId,
                "ip", ip,
                "timestamp", timestamp
        );
    }
}
