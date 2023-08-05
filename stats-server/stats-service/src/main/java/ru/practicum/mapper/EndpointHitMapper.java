package ru.practicum.mapper;

import ru.practicum.dto.EndpointHitDto;
import ru.practicum.model.EndpointHit;

import java.time.Instant;

import static ru.practicum.util.DateTime.toInstant;

public class EndpointHitMapper {
    public static EndpointHit toEndpointHit(EndpointHitDto endpointHitDto, long appId) {
        Instant timestamp = toInstant(endpointHitDto.getTimestamp());
        return EndpointHit.builder()
                .appId(appId)
                .ip(endpointHitDto.getIp())
                .timestamp(timestamp)
                .build();
    }
}
